# Backend API Endpoints Reference

## Base URL
```
http://localhost:8080/api/v1
```

## 📚 Course Endpoints
```
GET    /courses                                    - Get all courses (paginated)
GET    /courses/{id}                              - Get course by ID
GET    /courses/code/{code}                       - Get course by code
GET    /courses/type/{type}                       - Get courses by type
GET    /courses/semester/{semesterOrder}          - Get courses by semester order
GET    /courses/semester-name/{semesterName}      - Get courses by semester name
GET    /courses/grade-level/{gradeLevel}          - Get courses by grade level
GET    /courses/with-prerequisites                - Get all courses with prerequisites
GET    /courses/{id}/prerequisite-chain           - Get prerequisite chain for a course
GET    /courses/{id}/dependents                   - Get dependent courses
GET    /courses/search                            - Search/filter courses
GET    /courses/specialization/{specializationId} - Get courses by specialization
GET    /courses/{courseId}/available-sections     - Get available sections for a course
POST   /courses                                   - Create new course
PUT    /courses/{id}                              - Update course
DELETE /courses/{id}                              - Delete course
```

## 🏫 Course Section Endpoints
```
GET    /course-sections                                              - Get all sections (paginated)
GET    /course-sections/{id}                                        - Get section by ID
GET    /course-sections/search                                      - Search/filter sections
GET    /course-sections/search/course/{courseId}                    - Get sections for a course
GET    /course-sections/search/teacher/{teacherId}                  - Get sections by teacher
GET    /course-sections/search/classroom/{classroomId}              - Get sections by classroom
GET    /course-sections/search/semester/{semesterId}                - Get sections by semester
GET    /course-sections/search/course/{courseId}/semester/{semId}   - Get sections by course & semester
GET    /course-sections/search/teacher/{teacherId}/semester/{semId} - Get sections by teacher & semester
GET    /course-sections/search/classroom/{classId}/semester/{semId} - Get sections by classroom & semester
GET    /course-sections/search/available                            - Get available sections
POST   /course-sections                                             - Create new section
PUT    /course-sections/{id}                                        - Update section
DELETE /course-sections/{id}                                        - Delete section
PUT    /course-sections/{id}/enroll                                 - Increment enrollment count
PUT    /course-sections/{id}/withdraw                               - Decrement enrollment count
```

## 📅 Course Section Meeting Endpoints
```
GET    /course-section-meetings                           - Get all meetings
GET    /course-section-meetings/{id}                      - Get meeting by ID
GET    /course-section-meetings/search/section/{sectionId} - Get meetings for a section
GET    /course-section-meetings/search/day/{dayOfWeek}     - Get meetings by day of week
GET    /course-section-meetings/search/conflicts          - Get conflicting meetings
GET    /course-section-meetings/search/time-range         - Get meetings in time range
POST   /course-section-meetings                           - Create new meeting
PUT    /course-section-meetings/{id}                      - Update meeting
DELETE /course-section-meetings/{id}                      - Delete meeting
```

## 👨‍🏫 Teacher Endpoints
```
GET    /teachers                              - Get all teachers
GET    /teachers/{id}                         - Get teacher by ID
GET    /teachers/search/email                 - Search teacher by email
GET    /teachers/specialization/{specId}      - Get teachers by specialization
POST   /teachers                              - Create new teacher
PUT    /teachers/{id}                         - Update teacher
DELETE /teachers/{id}                         - Delete teacher
```

## 🏛️ Classroom Endpoints
```
GET    /classrooms                            - Get all classrooms
GET    /classrooms/{id}                       - Get classroom by ID
GET    /classrooms/search/name                - Search classroom by name
GET    /classrooms/room-type/{roomTypeId}     - Get classrooms by room type
GET    /classrooms/floor/{floor}              - Get classrooms by floor
POST   /classrooms                            - Create new classroom
PUT    /classrooms/{id}                       - Update classroom
DELETE /classrooms/{id}                       - Delete classroom
```

## 📚 Specialization Endpoints
```
GET    /specializations                       - Get all specializations
GET    /specializations/{id}                  - Get specialization by ID
GET    /specializations/search/name           - Search specialization by name
GET    /specializations/room-type/{roomTypeId} - Get specializations by room type
POST   /specializations                       - Create new specialization
PUT    /specializations/{id}                  - Update specialization
DELETE /specializations/{id}                  - Delete specialization
```

## 🏷️ Room Type Endpoints
```
GET    /room-types                            - Get all room types
GET    /room-types/{id}                       - Get room type by ID
GET    /room-types/search/name                - Search room type by name
POST   /room-types                            - Create new room type
PUT    /room-types/{id}                       - Update room type
DELETE /room-types/{id}                       - Delete room type
```

## 📅 Semester Endpoints
```
GET    /semesters                             - Get all semesters
GET    /semesters/{id}                        - Get semester by ID
GET    /semesters/search                      - Search semesters
GET    /semesters/year/{year}                 - Get semesters by year
GET    /semesters/active                      - Get active semester
GET    /semesters/reference/order             - Get semester by order
POST   /semesters                             - Create new semester
PUT    /semesters/{id}                        - Update semester
POST   /semesters/{id}/set-active             - Set semester as active
DELETE /semesters/{id}                        - Delete semester
```

## 👤 Student Endpoints
```
GET    /students                              - Get all students (paginated)
GET    /students/{id}                         - Get student by ID
GET    /students/search/email                 - Search student by email
GET    /students/search/name                  - Search students by name
GET    /students/search/first-name            - Search students by first name
GET    /students/grade-level/{gradeLevel}     - Get students by grade level
GET    /students/status/{status}              - Get students by status
POST   /students                              - Create new student
PUT    /students/{id}                         - Update student
DELETE /students/{id}                         - Delete student
```

## 📋 Student Course History Endpoints
```
GET    /students/{studentId}/course-history/_all    - Get all course history entries
GET    /students/{studentId}/course-history/{id}    - Get specific history entry
GET    /students/{studentId}/course-history         - Get paginated course history
GET    /students/{studentId}/course-history/passed  - Get passed courses
POST   /students/{studentId}/course-history         - Create history entry
```

## ✅ Enrollment Endpoints
```
POST   /enrollments                    - Create new enrollment
PUT    /enrollments/{id}               - Update enrollment
GET    /enrollments/{id}               - Get enrollment by ID
DELETE /enrollments/{id}               - Delete enrollment (unenroll)
GET    /enrollments/student/{studentId} - Get student's enrollments
GET    /enrollments/section/{sectionId} - Get enrollments for a section
GET    /enrollments/check              - Check if student is enrolled
```

---

## Common Query Parameters

### Pagination
- `page` (default: 0) - Page number (0-indexed)
- `size` (default: varies) - Number of items per page

### Search Filters
- `specialization` - Specialization ID
- `teacher` - Teacher ID
- `semester` - Semester ID
- `course` - Course ID
- `available` - Boolean (only available sections)

### Time Query Parameters
- `dayOfWeek` - 1-5 for Monday-Friday (or day name as string)
- `startTime` - Time in HH:MM format
- `endTime` - Time in HH:MM format

---

## Data Formats

### Time Format
- **Input/Output**: `HH:mm:ss` (e.g., "09:00:00")
- **Frontend Input**: `HH:mm` (e.g., "09:00")

### Day of Week
- Backend: String ('MONDAY', 'TUESDAY', etc.) or Integer (1-5)
- Frontend: String ('MONDAY', 'TUESDAY', etc.)

### Capacity
- Range: 1-100
- Must be greater than or equal to current enrollment count to delete

---

## Response Format

### Paginated Response
```json
{
  "content": [...],
  "page": {
    "number": 0,
    "size": 10,
    "totalElements": 50,
    "totalPages": 5
  }
}
```

### Error Response
```json
{
  "error": "Error message",
  "details": "Additional details"
}
```

---

## Validation Rules

### Course Section Creation
1. Teacher's specialization must match course's specialization
2. Classroom's room type must match course specialization's required room type
3. Course semester order must match active semester's order

### Meeting Creation
1. Start time must be before end time
2. Total meeting hours must not exceed course.hoursPerWeek
3. No duplicate meetings for same section/day/time
4. No teacher conflicts (same teacher at same time)
5. No classroom conflicts (same classroom at same time)
6. Teacher's daily hours must not exceed maxDailyHours
