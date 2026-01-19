# 📇 Quick Reference Card

## File Organization at a Glance

```
📁 spring-auth-app
├── 🤖 .github/copilot-instructions.md     ← START HERE for AI agents
├── 📖 README.md                            ← User guide
├── 📊 PROJECT_SUMMARY.md                   ← This project overview
├── 📋 FILE_MANIFEST.md                     ← File descriptions
├── ✅ COMPLETION_CHECKLIST.md              ← Verification status
│
├── 🔧 pom.xml                              ← Maven dependencies
├── 🐳 Dockerfile                           ← Container image
├── 🐳 docker-compose.yml                   ← Multi-service setup
│
├── 📁 src/main/java/com/auth/app
│   ├── SpringAuthAppApplication.java
│   ├── config/SecurityConfig.java
│   ├── controller/AuthController.java
│   ├── entity/User.java
│   ├── repository/UserRepository.java
│   └── service/AuthService.java
│
├── 📁 src/main/resources
│   ├── application.properties
│   └── templates/
│       ├── auth/login.html
│       ├── auth/signup.html
│       └── dashboard.html
│
└── 📁 src/test/java/com/auth/app
    ├── AuthControllerIntegrationTest.java
    └── AuthServiceIntegrationTest.java
```

## Command Reference

### Build & Run
```bash
mvn clean package       # Build JAR
mvn spring-boot:run     # Run locally
mvn test                # Run all tests
docker-compose up -d    # Start with Docker
```

### Database Switch
**PostgreSQL** (default):
```bash
# Already configured in application.properties
mvn spring-boot:run
```

**MSSQL**:
```bash
# Edit application.properties: uncomment MSSQL block
mvn spring-boot:run
```

## Architecture Layers

```
HTTP Request
    ↓
[Controller]        → /login, /signup, /dashboard, /logout
    ↓
[Service]           → signUp(), findByUsername(), validatePassword()
    ↓
[Repository]        → JPA queries (findByUsername, findByEmail)
    ↓
[Entity]            → User (id, username, email, password)
    ↓
[Database]          → PostgreSQL or MSSQL
```

## Common Tasks

### Add New Endpoint
1. Add method in `AuthController.java`
2. Create template in `src/main/resources/templates/`
3. Add test in `AuthControllerIntegrationTest.java`

### Add User Field
1. Add field to `User.java` entity
2. Update signup form HTML
3. Update `AuthController.signup()` parameters
4. Database auto-migrates

### Switch Databases
Edit `application.properties`:
- Uncomment MSSQL block
- Comment PostgreSQL block
- Run application (no code changes!)

### Write New Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
public class MyTest {
    @Autowired MockMvc mockMvc;
    
    @Test
    public void testSomething() throws Exception {
        mockMvc.perform(get("/endpoint"))
            .andExpect(status().isOk());
    }
}
```

## Authentication Flow

### Sign Up
```
User Form → AuthController.signup()
         → AuthService.signUp(username, email, password)
         → Validate duplicates
         → Encode password (BCrypt)
         → Save User to DB
         → Redirect to login
```

### Sign In
```
User Form → AuthController.login(username, password)
         → AuthService.findByUsername(username)
         → AuthService.validatePassword(password)
         → Create session
         → Redirect to dashboard
```

### Dashboard
```
GET /dashboard → Check session.getAttribute("username")
              → If null: redirect to /login
              → If found: render dashboard.html with username
```

## Configuration Keys

```properties
# Database (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
spring.datasource.username=postgres
spring.datasource.password=postgres

# Server
server.port=8080
server.servlet.session.timeout=1800  # 30 minutes

# Logging
logging.level.com.auth.app=DEBUG
```

## Test Statistics

| Category | Count |
|----------|-------|
| Controller Tests | 10 |
| Service Tests | 6 |
| Total Tests | 16 |
| Lines of Test Code | 200+ |
| Coverage | Full flow |

### Test Files
- `AuthControllerIntegrationTest.java` - HTTP layer
- `AuthServiceIntegrationTest.java` - Business logic

## Docker Commands

```bash
# Start
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down

# Rebuild
docker-compose down && docker-compose up --build -d

# Reset data
docker-compose down -v
```

## Browser Testing Walkthrough

1. Visit: `http://localhost:8080`
2. Redirects to: `http://localhost:8080/login`
3. Click "Sign Up"
4. Fill: username, email, password
5. Submit → Redirects to login with success message
6. Fill login form with same credentials
7. Submit → Redirects to dashboard
8. See: "Hello World! Welcome, [username]"
9. Click "Logout" → Returns to login

## File Dependencies

```
AuthController.java
├── depends on: AuthService
├── depends on: HttpSession
└── uses: Thymeleaf templates

AuthService.java
├── depends on: UserRepository
├── depends on: PasswordEncoder
└── business logic

User.java (Entity)
└── JPA annotations

UserRepository.java
└── Spring Data JPA interface

SecurityConfig.java
└── BCryptPasswordEncoder bean
```

## Key Classes & Methods

### AuthService
```java
User signUp(String username, String email, String password)
Optional<User> findByUsername(String username)
boolean validatePassword(String raw, String encoded)
```

### AuthController
```java
String login(String username, String password, HttpSession session)
String signup(String username, String email, String password)
String dashboard(HttpSession session)
String logout(HttpSession session)
```

### UserRepository
```java
Optional<User> findByUsername(String username)
Optional<User> findByEmail(String email)
boolean existsByUsername(String username)
boolean existsByEmail(String email)
```

## Database Schema

```sql
users
├── id (BIGINT) - Primary Key
├── username (VARCHAR 100) - Unique
├── email (VARCHAR 255) - Unique
├── password (VARCHAR 255) - BCrypt hash
├── enabled (BOOLEAN) - true by default
└── created_at (BIGINT) - Timestamp
```

## Debugging Tips

### Enable Debug Logging
```properties
logging.level.com.auth.app=DEBUG
```

### Check Database Connection
```bash
# PostgreSQL
psql -h localhost -U postgres -d authdb -c "SELECT * FROM users;"

# MSSQL
sqlcmd -S localhost -U sa -P testPassword123 -Q "SELECT * FROM users;"
```

### View Session in Browser
1. Open DevTools (F12)
2. Go to Application → Cookies
3. Look for `JSESSIONID` cookie

### Test Endpoint Manually
```bash
# Sign Up
curl -X POST http://localhost:8080/signup \
  -d "username=test&email=test@example.com&password=pass123"

# Login
curl -X POST http://localhost:8080/login \
  -d "username=test&password=pass123" \
  -c cookies.txt

# Dashboard (with session cookie)
curl -b cookies.txt http://localhost:8080/dashboard
```

## Production Checklist

- [ ] Change default passwords (PostgreSQL, MSSQL)
- [ ] Enable HTTPS/SSL certificates
- [ ] Set secure session cookie flag
- [ ] Configure CORS if needed
- [ ] Enable rate limiting
- [ ] Set up monitoring/logging
- [ ] Review BCrypt strength settings
- [ ] Enable database backups
- [ ] Configure environment variables
- [ ] Run full test suite

---

**Quick Lookup**: 🤖 For AI development → See `.github/copilot-instructions.md`
