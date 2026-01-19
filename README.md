# Spring Auth App

A Spring Boot authentication application with sign up and sign in functionality. Built with Java 21, Tomcat, and database support for PostgreSQL and MSSQL.

## Features

- User Registration (Sign Up) with comprehensive input validation
- User Authentication (Sign In) with BCrypt password hashing
- Session Management with secure session attributes
- Dashboard with personalized greeting
- Database support for PostgreSQL and MSSQL
- Docker containerization with automated setup
- Comprehensive integration tests (22 tests, all passing)
- Environment-based configuration for production deployment
- Thymeleaf template rendering

## Prerequisites

- Java 21 or later
- Maven 3.9+
- Docker and Docker Compose (optional)
- PostgreSQL 16+ or MSSQL 2022+ (if not using Docker)

## Quick Start with Docker

### Using MSSQL (Default)

The Docker Compose setup is pre-configured to use MSSQL Server with the following environment variables:

```bash
docker-compose up -d
```

The application will be available at `http://localhost:8080`

**Fresh Build and Clean Restart:**

To do a clean build with no cache and remove all volumes before starting:

```bash
docker-compose down -v ; docker-compose build --no-cache ; docker-compose up -d
```

This command:
- Stops all containers and removes volumes (`down -v`)
- Rebuilds all images without cache (`build --no-cache`)
- Starts all services in detached mode (`up -d`)

**Credentials:**
- MSSQL SA Username: `sa`
- MSSQL SA Password: `testPassword123`
- Database: `authdb`

### Using PostgreSQL

To use PostgreSQL instead of MSSQL, set environment variables before starting:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/authdb
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
docker-compose up -d
```

Or create a `.env` file in the project root:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/authdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
```

## Local Development Setup

### PostgreSQL Setup

1. Install and start PostgreSQL
2. Create database:
```sql
CREATE DATABASE authdb;
```

3. Update environment variables or `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

4. Run the application:
```bash
mvn spring-boot:run
```

### MSSQL Setup

1. Install and start MSSQL Server
2. Create database:
```sql
CREATE DATABASE authdb;
```

3. Update environment variables or `application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=authdb;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=testPassword123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

4. Run the application:
```bash
mvn spring-boot:run
```

## Environment Variables

The application uses the following environment variables for configuration:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/authdb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `postgres` |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | JDBC driver class | `org.postgresql.Driver` |

This allows easy deployment to different environments without modifying code.

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

- **Controller Tests (10 tests)**
  - Page load tests (login, signup, dashboard)
  - User registration (success, duplicate username, duplicate email)
  - User authentication (success, invalid password, invalid username)
  - Session management (dashboard access, logout)

- **Service Tests (12 tests)**
  - User signup with validation
  - Duplicate username/email detection
  - User lookup by username
  - Password validation with BCrypt
  - Input validation (empty/short username, invalid email, short password)

**Test Results:** ✅ All 22 tests passing

Run tests with:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=AuthControllerIntegrationTest
mvn test -Dtest=AuthServiceIntegrationTest
```

## Jenkins & GitHub Webhook Setup

### Exposing Local Jenkins with LocalTunnel

When using Docker Jenkins on your PC, the local IP cannot be accessed from outside (e.g., GitHub webhooks). Use **LocalTunnel** to expose Jenkins publicly:

#### Prerequisites
- Node.js and npm installed
- To install Node.js on Windows, use [nvm-windows](https://github.com/coreybutler/nvm-windows/blob/master/README.md)

#### Setup LocalTunnel

1. **Install LocalTunnel globally:**
```bash
npm install -g localtunnel
```

2. **Expose Jenkins (running on port 8081):**
```bash
lt --port 8081
```

This will output a public URL like: `https://xxxx-xx-xxx-xxx-xx.loca.lt`

3. **Configure GitHub Webhook:**
   - Go to your GitHub repository → **Settings** → **Webhooks**
   - Click **Add webhook**
   - **Payload URL:** `https://xxxx-xx-xxx-xxx-xx.loca.lt/github-webhook/`
   - **Content type:** `application/json`
   - **Events:** Select `Just the push event`
   - Click **Add webhook**

4. **Configure Jenkinsfile:**
   - Ensure your Jenkinsfile has the GitHub trigger:
   ```groovy
   triggers {
       githubPush()
   }
   ```

5. **Enable GitHub Plugin in Jenkins:**
   - Jenkins Dashboard → **Manage Jenkins** → **Manage Plugins**
   - Search for and install **GitHub Plugin** if not present

Now commits to your GitHub repository will automatically trigger the Jenkins pipeline!

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

### Database Configuration

The application supports multiple databases through environment variables:

#### PostgreSQL
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/authdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
```

#### MSSQL
```properties
SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=authdb
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=testPassword123
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### Application Properties

Edit `src/main/resources/application.properties` to modify:
- Server port: `server.port=8080`
- Thymeleaf caching: `spring.thymeleaf.cache=true` (production)
- JPA hibernate: `spring.jpa.hibernate.ddl-auto=update` (or `validate` in production)
- Debug logging: `logging.level.com.auth.app=DEBUG`

### Production Deployment

For production, always:
1. Set all database credentials via environment variables
2. Disable Thymeleaf cache: `SPRING_THYMELEAF_CACHE=false`
3. Use `validate` DDL mode: `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`
4. Enable HTTPS: Set `server.ssl.*` properties
5. Disable debug logging: Remove `DEBUG` level configuration

## Release Notes

### Recent Improvements (Session 1)

- **Input Validation**: Added comprehensive validation for username (3-100 chars), email (regex pattern), and password (6-255 chars)
- **Security**: Externalized database credentials to environment variables for production deployment
- **Template Engine**: Added Thymeleaf dependency for proper HTML view rendering
- **Test Coverage**: Added 6 new validation tests; all 22 tests passing
- **Docker Support**: Full Docker Compose setup with MSSQL 2022 and PostgreSQL options
- **Configuration**: Environment-based configuration system for multi-environment deployment

**Bug Fixes:**
1. Fixed TestDataSourceConfig missing DataSource bean
2. Externalized hardcoded database credentials
3. Added comprehensive input validation layer to AuthService
4. Added login endpoint input validation
5. Fixed signup redirect behavior for proper HTTP flow
6. Refactored DataSourceConfig to use @Value property injection
7. Fixed SPRING_DATASOURCE_DRIVER property naming convention
8. Added missing spring-boot-starter-thymeleaf dependency

## License

MIT
