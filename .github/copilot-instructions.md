# Copilot Instructions for Spring Auth App

## Project Overview

This is a Spring Boot 3.2.2 authentication application built with Java 21. It provides user sign-up, sign-in, and session management with a simple dashboard. The application uses Tomcat (embedded), Thymeleaf for templates, and supports both PostgreSQL and MSSQL databases.

## Architecture

### Layered Structure
- **Controller Layer** (`src/main/java/com/auth/app/controller/`): Handles HTTP requests, form submissions, and session management
- **Service Layer** (`src/main/java/com/auth/app/service/`): Business logic for authentication (signup, login, password validation)
- **Repository Layer** (`src/main/java/com/auth/app/repository/`): JPA data access using Spring Data
- **Entity Layer** (`src/main/java/com/auth/app/entity/`): JPA `@Entity` classes (User entity with username, email, password, enabled flag)
- **Configuration** (`src/main/java/com/auth/app/config/`): BCryptPasswordEncoder bean definition

### Data Flow
1. User submits form on `/login` or `/signup` → AuthController
2. AuthController calls AuthService methods (signUp, findByUsername, validatePassword)
3. AuthService uses UserRepository for database operations
4. AuthService handles password encoding/validation with injected PasswordEncoder
5. Session attributes stored on successful login; checked on `/dashboard` access

## Key Conventions

### Password Handling
- **Always** use `PasswordEncoder.encode()` during signup (see `AuthService.signUp()`)
- **Never** store plaintext passwords; validation uses `PasswordEncoder.matches()`
- BCryptPasswordEncoder is configured in `SecurityConfig.java`

### Session Management
- Session attributes set: `userId` (Long), `username` (String)
- Logout invalidates entire session: `session.invalidate()`
- Dashboard checks `session.getAttribute("username")` and redirects to `/login` if null

### Database Configuration
- PostgreSQL is default in `application.properties`
- Switch databases by uncommenting MSSQL section in `application.properties` or setting environment variables
- Schema auto-created via `spring.jpa.hibernate.ddl-auto=update`

### Form Processing
- Forms use Thymeleaf template syntax: `th:action="@{/path}"` for URL handling
- POST endpoints return view names (e.g., `"auth/login"`) for form redisplay with errors
- Redirects use `"redirect:/path"` for successful operations

## Essential Commands

### Build and Run
```bash
mvn clean package                    # Build JAR
mvn spring-boot:run                  # Run locally
mvn test                             # Run integration tests
```

### Docker
```bash
docker-compose up -d                 # Start with PostgreSQL
docker-compose down                  # Stop containers
```

### Integration Tests
- Located in `src/test/java/com/auth/app/`
- Use `@SpringBootTest` with `MockMvc` for HTTP testing
- Use `@BeforeEach` to set up test users
- Cover: form validation, duplicate users, password mismatch, session redirects

## Common Patterns

### Adding a New Endpoint
1. Add method in `AuthController` with `@GetMapping` or `@PostMapping`
2. Return view name (e.g., `"auth/newpage"`) or redirect (e.g., `"redirect:/other"`)
3. Add corresponding Thymeleaf template in `src/main/resources/templates/`
4. Add integration test in `AuthControllerIntegrationTest`

### Adding User Fields
1. Add field to `User` entity with `@Column` annotation
2. Update signup form in `signup.html`
3. Update `AuthController.signup()` to accept new parameter
4. Update `AuthService.signUp()` method signature
5. Database schema auto-migrates via Hibernate

### Switching Databases
- Edit `application.properties`: uncomment MSSQL block, comment PostgreSQL block
- Or set environment variables: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, etc.
- No code changes needed; drivers available via Maven

## Testing Strategy

- **Integration Tests** validate HTTP interactions, form processing, and session behavior
- Use `mockMvc.perform(post/get(...))` for request simulation
- Assert response status, redirects, and HTML content with `.andExpect()`
- Service-level tests verify business logic (signup validation, password checking)

## Files to Know

| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies and build config |
| `application.properties` | Database and server configuration |
| `AuthController.java` | HTTP endpoints for login/signup/dashboard |
| `AuthService.java` | Password encoding, user lookup, signup validation |
| `User.java` | JPA entity with database schema |
| `UserRepository.java` | Custom queries (findByUsername, findByEmail, exists methods) |
| `login.html`, `signup.html`, `dashboard.html` | Thymeleaf templates |
| `AuthControllerIntegrationTest.java` | HTTP behavior tests |
| `AuthServiceIntegrationTest.java` | Service logic tests |
| `docker-compose.yml` | PostgreSQL and MSSQL service definitions |

## Debugging Tips

- Enable debug logging in `application.properties`: `logging.level.com.auth.app=DEBUG`
- Check session creation in browser dev tools (Cookies tab)
- Database issues: verify `application.properties` datasource URL matches running service
- Test endpoints manually: POST to `/login` or `/signup` with form parameters
