# Smart Task Manager 🚀

A production-ready RESTful API for a Trello-like task management system with JWT authentication, advanced filtering, and a beautiful dashboard.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Docker Deployment](#docker-deployment)
- [Security](#security)

## ✨ Features

- **User Authentication & Authorization**
  - JWT-based authentication with secure token generation
  - Role-based access control (USER, ADMIN)
  - Email-based login and registration
  - Password encryption using BCrypt

- **Board Management**
  - Create, read, update, and delete boards
  - Board ownership and member management
  - Collaborative workspace with multiple users

- **Task Management**
  - Organize tasks in customizable lists/columns
  - Set task priority levels (LOW, MEDIUM, HIGH)
  - Track task status (TODO, IN_PROGRESS, DONE)
  - Assign tasks to team members
  - Set due dates with overdue task tracking
  - Rich task descriptions

- **Advanced Features**
  - Dashboard with task statistics
  - Filter tasks by status and board
  - Identify overdue tasks
  - Real-time task updates
  - CORS support for frontend integration
  - Drag and drop kanban board
  - Task search and filtering

## 🛠 Tech Stack

### Backend
| Component | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Core language |
| **Spring Boot** | 3.2.5 | Web framework |
| **Spring Security** | 3.2.5 | Authentication/Authorization |
| **Spring Data JPA** | 3.2.5 | Database ORM |
| **PostgreSQL** | Latest | Database |
| **JWT (JJWT)** | 0.12.3 | Token-based auth |
| **Lombok** | Latest | Boilerplate reduction |
| **Maven** | 3.8+ | Build tool |

### Frontend
| Component | Purpose |
|-----------|---------|
| **React 18** | UI framework |
| **Vite** | Build tool |
| **Axios** | HTTP client |
| **React Query** | State management |
| **react-beautiful-dnd** | Drag & drop |
| **TailwindCSS** | Styling |

### Tools & Infrastructure
- **Docker & Docker Compose** - Containerization
- **Git** - Version control
- **Postman/Insomnia** - API testing

## 📁 Project Structure

```
smart-task-manager/
├── src/
│   ├── main/java/com/aman/smart_task_manager/
│   │   ├── SmartTaskManagerApplication.java    # Entry point
│   │   ├── controller/                         # REST endpoints
│   │   │   ├── AuthController.java            # Auth endpoints
│   │   │   ├── BoardController.java           # Board endpoints
│   │   │   ├── TaskController.java            # Task endpoints
│   │   │   ├── TaskListController.java        # Task list endpoints
│   │   │   └── DashboardController.java       # Dashboard metrics
│   │   ├── model/                              # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Board.java
│   │   │   ├── Task.java
│   │   │   ├── TaskList.java
│   │   │   ├── Role.java                      # USER, ADMIN
│   │   │   ├── TaskStatus.java                # TODO, IN_PROGRESS, DONE
│   │   │   └── Priority.java                  # LOW, MEDIUM, HIGH
│   │   ├── repository/                         # Data access layer
│   │   │   ├── UserRepository.java
│   │   │   ├── BoardRepository.java
│   │   │   ├── TaskRepository.java
│   │   │   └── TaskListRepository.java
│   │   └── security/                           # Security configuration
│   │       ├── SecurityConfig.java             # Spring Security setup
│   │       ├── JwtUtil.java                    # JWT token utilities
│   │       └── JwtAuthFilter.java              # JWT authentication filter
│   ├── resources/
│   │   └── application.properties              # Configuration
│   └── test/
├── docker-compose.yml                          # Docker services
├── pom.xml                                     # Maven dependencies
├── mvnw / mvnw.cmd                            # Maven wrapper
└── README.md                                   # This file
```

## 🚀 Setup Instructions

### Prerequisites
- **Java 17** or higher
- **Maven 3.8+** or use the included `mvnw`
- **Docker & Docker Compose** (optional, for containerized setup)
- **PostgreSQL 12+** (or use Docker)

### Local Development Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/amantripathi2025/smart-task-manager.git
cd smart-task-manager
```

#### 2. Configure Environment Variables
Create `src/main/resources/application.properties`:
```properties
# Server Configuration
server.port=8080

# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/smart_task_manager
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=your-super-secret-key-min-256-bits-long-for-production-use-change-this-immediately
jwt.expiration=86400000

# Logging
logging.level.root=INFO
logging.level.com.aman.smart_task_manager=DEBUG

# Timezone
spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Kolkata
```

#### 3. Start PostgreSQL (Docker)
```bash
docker run -d \
  --name postgres-smart-task \
  -e POSTGRES_DB=smart_task_manager \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  postgres:15-alpine
```

#### 4. Build and Run
```bash
# Using Maven wrapper (Unix/Linux/Mac)
./mvnw clean install
./mvnw spring-boot:run

# Using Maven wrapper (Windows)
mvnw.cmd clean install
mvnw.cmd spring-boot:run

# Or using Maven directly
mvn clean install
mvn spring-boot:run
```

The application will start at `http://localhost:8080`

### Docker Setup (Complete)

```bash
# Navigate to project root
cd smart-task-manager

# Start all services (PostgreSQL + Application)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Verify Installation
```bash
# Health check
curl http://localhost:8080/api/auth/register

# Should return: 400 Bad Request (expected, missing body)
```

## 📚 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/auth/register` | Register new user | ❌ |
| `POST` | `/api/auth/login` | Login and get JWT token | ❌ |

### Board Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/boards` | Get all user's boards | ✅ |
| `POST` | `/api/boards` | Create a new board | ✅ |
| `GET` | `/api/boards/{id}` | Get board details | ✅ |
| `PUT` | `/api/boards/{id}` | Update board | ✅ |
| `DELETE` | `/api/boards/{id}` | Delete board | ✅ |

### Task List Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/boards/{boardId}/lists` | Get all lists in a board | ✅ |
| `POST` | `/api/boards/{boardId}/lists` | Create a new list | ✅ |
| `PUT` | `/api/lists/{id}` | Update list | ✅ |
| `DELETE` | `/api/lists/{id}` | Delete list | ✅ |

### Task Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/tasks/board/{boardId}` | Get tasks in a board | ✅ |
| `GET` | `/api/tasks/board/{boardId}?status=TODO` | Get tasks by status | ✅ |
| `POST` | `/api/tasks` | Create a new task | ✅ |
| `PUT` | `/api/tasks/{id}` | Update task | ✅ |
| `DELETE` | `/api/tasks/{id}` | Delete task | ✅ |
| `GET` | `/api/tasks/overdue` | Get overdue tasks | ✅ |

### Dashboard Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/dashboard` | Get dashboard statistics | ✅ |

### Example API Requests

#### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "securePassword123"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "securePassword123"
  }'

# Response:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
# }
```

#### Create Board (with JWT token)
```bash
curl -X POST http://localhost:8080/api/boards \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Project Board",
    "description": "Q2 Project Planning"
  }'
```

#### Create Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement user authentication",
    "description": "Add JWT-based auth to the system",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "taskListId": 1,
    "assigneeId": 1,
    "dueDate": "2025-06-30T17:00:00"
  }'
```

#### Get Dashboard Statistics
```bash
curl -X GET http://localhost:8080/api/dashboard \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Response:
# {
#   "total": 25,
#   "todo": 10,
#   "inProgress": 8,
#   "done": 7,
#   "overdue": 2,
#   "overdueTasks": [...]
# }
```

## ⚙️ Configuration

### JWT Configuration
Edit `application.properties` to customize JWT settings:
```properties
# Token expiration in milliseconds (86400000 = 24 hours)
jwt.expiration=86400000

# Secret key (minimum 256 bits for HMAC-SHA256)
# For production, use strong random key: openssl rand -base64 32
jwt.secret=your-very-secret-key-change-in-production
```

### CORS Configuration
The application includes CORS configuration allowing requests from any origin in development. Customize in `SecurityConfig.java`:
```java
CorsConfiguration config = new CorsConfiguration();
config.setAllowedOrigins(List.of("http://localhost:3000")); // Your frontend URL
```

### Database Configuration
PostgreSQL settings in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smart_task_manager
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

## 🐳 Docker Deployment

### Using Docker Compose
```bash
# Start services
docker-compose up -d

# View running containers
docker-compose ps

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Remove volumes
docker-compose down -v
```

### Using Docker CLI
```bash
# Build image
docker build -t smart-task-manager:latest .

# Run container
docker run -d \
  --name smart-task-app \
  --network host \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/smart_task_manager \
  -e SPRING_DATASOURCE_PASSWORD=password \
  smart-task-manager:latest
```

## 🔒 Security Features

1. **JWT Authentication**
   - Tokens expire after 24 hours by default
   - Secure token signing with HS256 algorithm
   - Stateless authentication (no session storage)

2. **Password Security**
   - Passwords hashed with BCrypt
   - Minimum complexity enforced at registration

3. **Authorization**
   - Role-based access control (USER, ADMIN)
   - User can only access their own boards
   - Protected endpoints require valid JWT token

4. **CSRF Protection**
   - CSRF disabled for stateless API
   - Use HTTPS in production

5. **SQL Injection Prevention**
   - JPA parameterized queries
   - No string concatenation in SQL

6. **CORS Security**
   - Configure allowed origins for production
   - Credentials handling configured properly

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Kill process using port 8080
lsof -ti:8080 | xargs kill -9  # Unix/Linux/Mac
netstat -ano | findstr :8080   # Windows
```

### Database Connection Failed
- Verify PostgreSQL is running
- Check connection string in `application.properties`
- Ensure database name is correct: `smart_task_manager`

### JWT Token Invalid
- Verify token is passed in `Authorization: Bearer <token>` header
- Check token hasn't expired (default 24 hours)
- Ensure JWT secret in config matches the one used to generate token

### CORS Errors
- Configure allowed origins in `SecurityConfig.java`
- Ensure frontend URL matches CORS configuration

## 📈 Performance Optimization

- **Lazy Loading**: Relationships use `FetchType.LAZY` to avoid N+1 queries
- **Query Optimization**: Custom `@Query` annotations for complex queries
- **Connection Pooling**: HikariCP configured by default
- **Database Indexing**: Consider adding indexes on frequently queried columns

## 🚀 Next Steps

1. **Frontend Development**
   - Integrate React frontend (available separately)
   - Configure API base URL in frontend environment

2. **Enhanced Features**
   - Real-time updates with WebSockets
   - Activity logging and audit trails
   - Advanced search and filtering
   - Team collaboration features

3. **Testing**
   - Add comprehensive unit tests
   - Integration tests with TestContainers
   - Load testing with JMeter

4. **Deployment**
   - Deploy to cloud (AWS, GCP, Azure)
   - Set up CI/CD pipeline with GitHub Actions
   - Configure production environment variables

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📧 Support

For support, email support@example.com or create an issue in the GitHub repository.

---

**Made with ❤️ by Aman Tripathi**
