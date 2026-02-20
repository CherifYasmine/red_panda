# 🏫 Maplewood High School - Course Planning System

A full-stack course planning application built with **Spring Boot** backend and **React + TypeScript** frontend. Students can browse courses, build their semester schedule, track graduation progress, and manage enrollments with real-time validation.

---

## ✨ Features

### Student Features
- **Course Browser** - Browse and filter available courses with detailed information
- **Schedule Builder** - Add/remove courses with real-time validation
- **Academic Dashboard** - View GPA, credits earned, graduation progress (n/30)
- **Current Schedule** - Weekly calendar view of enrolled courses
- **Course History** - Track completed courses and grades from previous semesters
- **Enrollment Validation**:
  - ✓ Prerequisite checking
  - ✓ Schedule conflict detection
  - ✓ Maximum 5 courses per semester limit
  - ✓ Grade level requirements
  - ✓ Section capacity validation

### Admin Features
- **Hierarchical Navigation** - Courses → Sections → Meetings
- **Course Management** - View all courses with advanced filtering
- **Section Management** - Create and manage course sections
- **Meeting Management** - Schedule meetings with teacher and classroom assignments
---

## 🛠️ Tech Stack

### Backend
- **Java 21** with Spring Boot 4.0.3
- **Maven** for build management
- **SQLite** database with Hibernate JPA
- **RESTful API** with validation layer

### Frontend
- **React 19** with TypeScript
- **Vite** for fast development and building
- **Zustand** for state management
- **Tailwind CSS** for styling
- **React Router** for navigation
- **Axios** for HTTP client

### Infrastructure
- **Docker** & **Docker Compose** for containerization
- Multi-stage builds for optimized images

---

## 🚀 Quick Start with Docker

### Prerequisites
- Docker and Docker Compose installed
- No need to install Java, Node.js, or other dependencies

### Run with Docker Compose

```bash
cd red_panda

# Start both backend and frontend
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

Services will be available at:
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html

### Stop Services

```bash
docker-compose down

# Remove all images and volumes
docker-compose down -v
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
```

---

## 📦 Manual Setup

### Prerequisites
- **Java 21** or higher
- **Maven 3.8+**
- **Node.js 20+** and npm
- **SQLite 3** (optional, for database inspection)

### 1. Backend Setup

```bash
cd backend

# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run

# Server will start on http://localhost:8080
```

**Backend Configuration:**
- Database: `maplewood_school.sqlite`
- API Docs: http://localhost:8080/swagger-ui.html
- Logs: Console output with Spring Boot banner

### 2. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Development mode with hot reload
npm run dev

# Frontend will be available at http://localhost:5173
```

**Development Server Features:**
- Hot Module Replacement (HMR) for instant updates
- Fast TypeScript checking
- Detailed error messages

---

## 📁 Project Structure

```

```

---

## 🔌 API Documentation

### Base URL
- **Development**: http://localhost:8080
- **Docker**: http://backend:8080

### Core Endpoints

#### Courses
- `GET /api/courses` - List all courses with filters
- `GET /api/courses/{id}` - Get course details


Full API documentation available at: http://localhost:8080/swagger-ui.html

---

## 🗄️ Database

### Database File
Located at: `./maplewood_school.sqlite`

### Key Tables
- `students` - 400 students (grades 9-12)
- `courses` - 57 courses with prerequisites
- `teachers` - 50 teachers across 9 specializations
- `classrooms` - 60 rooms with different types
- `course_sections` - Sections offered (created by admin)
- `course_section_meetings` - Meeting times for sections
- `current_enrollments` - Active semester enrollments
- `student_course_history` - Completed courses and grades
- `semesters` - Semester definitions
