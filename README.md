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

## 🏗️ Architecture

For a comprehensive overview of the backend architecture, including SOLID principles, design patterns, and validation layer organization, see [BACKEND_ARCHITECTURE_SUMMARY.md](./BACKEND_ARCHITECTURE_SUMMARY.md).

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

## 🔐 Login & Test Accounts

### Admin Login
Access the admin panel to manage courses, sections, and meetings.

**Admin Credentials:**
- **Password**: `admin123`

**Admin Panel**: http://localhost:5173/admin

### Student Test Accounts

Use any of the following student accounts to explore the student experience:

#### Student 1: Nancy Collins
- **Student ID**: `1`
- **Email**: `nancy.collins9@student.maplewood.edu`
- **Grade Level**: 9
- **Status**: Active

#### Student 2: Jason Flores
- **Student ID**: `101`
- **Email**: `jason.flores10@student.maplewood.edu`
- **Grade Level**: 10
- **Status**: Active

**Note**: Student login uses the student ID (numeric) to authenticate. Navigate to the login page and enter the student ID to access their dashboard.

---