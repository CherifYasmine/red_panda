# Maplewood High School - Backend Architecture Summary

## Overview

The backend is a **course enrollment and academic management system** built with **Spring Boot** and **SQLite**. It enforces comprehensive business rules for course scheduling, student enrollment, prerequisite validation, and academic tracking.

---

## Architecture Style

**DDD-Inspired Layered Architecture** - pragmatic domain organization combined with clean layers:

- **API Layer:** REST controllers receiving requests, returning DTOs
- **Service Layer:** Business logic, validation orchestration
- **Validation Layer:** Dedicated validators for business rules
- **Repository Layer:** Data access with custom queries
- **Domain Layer:** JPA entities representing core concepts

Each domain (catalog, scheduling, enrollment, student, school) owns its own complete stack of packages.

---

## Core Entities

### Pre-Existing (Foundation)
- **Course:** Catalog entry - code, name, credits, prerequisites, specialization
- **Student:** Enrollment record - name, grade level, email
- **Teacher:** Instructor - specialization must match courses taught
- **Classroom:** Physical space - capacity, room type (classroom/lab/studio)
- **Semester:** Academic term - ordering for prerequisite validation
- **Specialization:** Subject area links (English, Math, Science, etc.)
- **StudentCourseHistory:** Completed courses with grades and outcomes

### New Entities (Created for System)
- **CourseSection:** Specific course offering (teacher, classroom, capacity, semester)
- **CourseSectionMeeting:** Individual class meeting (day, time, duration)
- **CurrentEnrollment:** Active student enrollment in a section

---

## Design Patterns

### 1. Domain Organization
- Entities grouped by business capability, not technical layer
- Each domain (enrollment/, scheduling/, catalog/) contains: entity/, repository/, service/, validator/, api/
- Improves cohesion, enables team parallelization

### 2. DTO Pattern
- Controllers return DTOs, never raw entities
- Prevents over-fetching, controls serialization, decouples API contract from database schema
- Mappers handle entity ↔ DTO conversion centrally

### 3. Validator Pattern
- Separate validator beans per entity type
- Validators encapsulate business rule logic
- Throw IllegalArgumentException on violation (caught globally)
- Runs **before** persistence

### 4. Repository Pattern
- Custom query methods for complex filtering
- Supports multi-parameter searches (specialization, courseType, gradeLevel, semester)

---

## Validation System

### Philosophy
Fail fast, validate early. Validations happen before database persistence.

**Three Levels:**
1. Entity constraints (@NotNull, @Max, @Min)
2. Validator component logic (business rules)
3. Service-level checks (state-dependent rules)

---

## Course Section Meeting Validation

When creating a meeting, 6 validations run:

| # | Validation | Purpose | Example |
|---|-----------|---------|---------|
| 1 | **Lunch Hour Block** | No classes 12:00 PM - 1:00 PM | Reject 12:30-1:30 meeting |
| 2 | **Teacher Daily Hours** | Max 4 hours teaching per day | Reject if teacher > 4 hrs on day |
| 3 | **Course Hour Type** | Core 4-6 hrs/week, Elective 2-4 hrs/week | Enforce min/max meeting totals |
| 4 | **Total Hours Match** | Sum of meetings = course.hoursPerWeek | Reject if total exceeds requirement |
| 5 | **Schedule Conflicts** | No classroom double-booking, no teacher conflicts | Reject if classroom/teacher already scheduled |
| 6 | **Unique Meetings** | No duplicate day/time for same section | Reject duplicate MWF 10:00 |

**Cost Optimization:** Checks ordered cheapest to most expensive (in-memory before database queries).

---

## Student Enrollment Validation

When a student enrolls, 7 validations run:

| # | Validation | Purpose | Example |
|---|-----------|---------|---------|
| 1 | **No Duplicate Course** | Student not already enrolled in same course this semester | Reject if already in Section A of MAT101 |
| 2 | **Not Already Completed** | Can't retake courses already passed | Reject if student passed MAT101 in Fall 2023 |
| 3 | **Grade Level** | Student's grade level within course min/max range | Reject sophomore enrolling in seniors-only course |
| 4 | **Section Capacity** | Section hasn't reached max (usually 10) | Reject if section full |
| 5 | **Course Limit** | Student not exceeding 5 courses per semester | Reject 6th course enrollment |
| 6 | **Prerequisites** | Student has passed all prerequisites in correct semester order | Reject Eng II if Eng II not completed |
| 7 | **Schedule Conflicts** | No overlapping class times for student | Reject if student has MWF 10-11 when trying to add MWF 10-12 |

**Order Rationale:** Fail cheapest (database lookups) first, reserve expensive checks (prerequisite chains, conflict detection) for last.

---

## Academic Metrics

Calculated on-demand for each student:

- **GPA:** (Credits Earned) / (Credits Attempted) × 4.0
  - Earned = sum of credits from passed courses
  - Attempted = sum of credits from passed + failed courses
  
- **Credits Earned:** Total credits from completed courses

- **Credits to Graduation:** 30 - credits earned (bottoms out at 0)

**Design Choice:** Calculated per-request, not cached. Rationale: Metrics update infrequently, caching adds complexity. Revisit if performance analysis shows bottleneck.

---

## Meeting Scheduling

### Key Constraints

1. **Lunch Hour:** No classes 12:00 PM - 1:00 PM (school-wide)
2. **Teacher Availability:** No teacher teaches > 4 hours/day
3. **Classroom Availability:** No double-booking of rooms
4. **Course Requirements:** Total meeting hours must equal course.hoursPerWeek
5. **Teacher-Course Match:** Teacher's specialization must match course specialization
6. **Room-Course Match:** Classroom room type must match specialization's requirement

---

## Prerequisite Validation

Prerequisites form chains:
- ENG101 (no prereq)
- → ENG102 (requires ENG101)
- → ENG201 (requires ENG102)
- → ENG202 (requires ENG201)
- etc.

When validating enrollment, system:
1. Checks StudentCourseHistory for prerequisite course
2. Verifies status = PASSED (not failed, not withdrawn)
3. Enforces semester ordering (can't take Spring course in Fall)
4. Rejects enrollment if prerequisite not satisfied

---

## Concurrency Control

### The Problem
If 2+ students enroll simultaneously in a section with capacity=1, both see `enrollmentCount=0` during validation and both pass. First student increments to 1, second student increments to 2 (over capacity).

### Solution: Optimistic Locking + Retry
- **CourseSection has `@Version` field** - Automatically incremented by Hibernate on save
- When first student saves, version becomes 2
- Second student's save attempts with old version=1 → OptimisticLockException thrown
- Service catches exception and retries (max 3 times) with fresh data from DB
- Fresh data shows updated `enrollmentCount` → validation fails with "Section at capacity" (409)

**Benefits:** No database locks, automatic retry, correct error message, prevents infinite loops

---

## Error Handling & HTTP Status Codes

Clear error semantics help clients understand exactly what went wrong:

| Status | Scenario | Examples |
|--------|----------|----------|
| **400 Bad Request** | Malformed request / validation failure | Missing required field, invalid JSON, prerequisite not passed |
| **404 Not Found** | Resource doesn't exist | Student ID 999 not found, Course ID 50 not found |
| **409 Conflict** | Business conflict with system state | Section at capacity, student already enrolled in course, schedule conflict, teacher/classroom booked |


---
