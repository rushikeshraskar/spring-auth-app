# Spring Auth App

A Spring Boot authentication application with sign up and sign in functionality. Built with Java 21, Tomcat, and database support for PostgreSQL and MSSQL.

## Features

- User Registration (Sign Up)
- User Authentication (Sign In)
- Session Management
- Dashboard with personalized greeting
- Database support for PostgreSQL and MSSQL
- Docker containerization
- Integration tests

## Prerequisites

- Java 21 or later
- Maven 3.9+
- Docker and Docker Compose (optional)
- PostgreSQL 16+ or MSSQL 2022+ (if not using Docker)

## Quick Start with Docker

### Using PostgreSQL (Default)

```bash
docker-compose up -d
```

The application will be available at `http://localhost:8080`

### Using MSSQL

1. Update `docker-compose.yml` to use mssql service instead of postgres
2. Update `application.properties` MSSQL configuration
3. Run: `docker-compose up -d`

## Local Development Setup

### PostgreSQL Setup

1. Install and start PostgreSQL
2. Create database:
```sql
CREATE DATABASE authdb;
```

3. Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### MSSQL Setup

1. Install and start MSSQL Server
2. Create database:
```sql
CREATE DATABASE authdb;
```

3. Update `application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=authdb
spring.datasource.username=sa
spring.datasource.password=testPassword123
```

## Building and Running

### Build the project:
```bash
mvn clean package
```

### Run the application:
```bash
mvn spring-boot:run
```

### Run integration tests:
```bash
mvn test
```

## Project Structure

```
spring-auth-app/
├── src/
│   ├── main/
│   │   ├── java/com/auth/app/
│   │   │   ├── config/          # Security and application configuration
│   │   │   ├── controller/      # REST and Web controllers
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── service/         # Business logic
│   │   │   └── SpringAuthAppApplication.java
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf HTML templates
│   │       └── application.properties
│   └── test/
│       └── java/com/auth/app/   # Integration tests
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Redirects to login |
| GET | `/login` | Login page |
| POST | `/login` | Process login form |
| GET | `/signup` | Sign up page |
| POST | `/signup` | Process sign up form |
| GET | `/dashboard` | User dashboard (requires login) |
| GET | `/logout` | Logout user |

## Testing

The application includes comprehensive integration tests covering:

- Page load tests (login, signup)
- User registration (success, duplicate username, duplicate email)
- User authentication (success, invalid password, invalid username)
- Session management (dashboard access, logout)

Run tests with:
```bash
mvn test
```

## Technologies

- **Framework**: Spring Boot 3.2.2
- **Language**: Java 21
- **Database**: PostgreSQL / MSSQL
- **Server**: Apache Tomcat (embedded)
- **ORM**: JPA/Hibernate
- **Security**: Spring Security with BCrypt password encoding
- **Template Engine**: Thymeleaf
- **Testing**: JUnit 5, Mockito, Spring Test

## Configuration

Key properties in `application.properties`:

- `spring.datasource.url` - Database connection URL
- `spring.datasource.username` - Database username
- `spring.datasource.password` - Database password
- `server.port` - Server port (default: 8080)
- `server.servlet.session.timeout` - Session timeout in seconds (default: 1800)

## License

MIT
