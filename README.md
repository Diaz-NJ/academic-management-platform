# Academic Management Platform

A comprehensive web-based platform for BSIT-3I students at Pateros Technological College to manage academic tasks, schedules, and collaboration.

## Features

- **Task & Deadline Tracker**: Kanban-style dashboard for managing assignments
- **Integrated Academic Calendar**: Color-coded events and reminders
- **Progress Reports & Analytics**: Visual summaries of academic performance
- **Collaboration Tools**: Group project management
- **Personalized Notifications**: Custom alerts for deadlines and classes

## Technologies Used

- **Frontend**: HTML5, CSS3, JavaScript
- **Backend**: Java (Servlets)
- **Database**: MySQL
- **Version Control**: Git & GitHub

## Installation

### Prerequisites
- JDK 11 or higher
- MySQL 8.0 or higher
- Apache Tomcat 9.0 or higher
- Git

### Setup Instructions

1. **Clone the repository**
```bash
   git clone https://github.com/YOUR-USERNAME/academic-management-platform.git
   cd academic-management-platform
```

2. **Setup Database**
```bash
   mysql -u root -p < src/database/schema.sql
```

3. **Configure Database Connection**
   Edit `src/backend/java/com/ptc/amp/config/DatabaseConfig.java` with your MySQL credentials.

4. **Build and Deploy**
   - Compile Java files
   - Deploy to Tomcat
   - Access at `http://localhost:8080/amp`

## Usage

1. Register/Login with your student credentials
2. Create tasks and set deadlines
3. View your dashboard and manage workflow
4. Collaborate with classmates on group projects
5. Track your academic progress

## Team Members

- Neil John Diaz
- Joyce Therese Repollo
- Ivan Clard Mangaoang
- Cerwin Oliver Almario
- John Benedict Palermo

## License

This project is developed for academic purposes at Pateros Technological College.