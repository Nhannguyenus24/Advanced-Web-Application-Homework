# SECURE REST API - SPRING BOOT

## 🎯 FEATURES
- JWT Authentication (HS512, 24h expiration)
- Role-based Authorization (USER, ADMIN)
- BCrypt Password Hashing (12 rounds)
- HTTPS/SSL (Port 8443)
- XSS Protection (OWASP Sanitizer)
- SQL Injection Prevention (JPA)
- Rate Limiting (100 req/min per IP)
- Input Validation & CORS

## 🛠 TECH STACK
**Backend:** Spring Boot 3.5.7, Spring Security, Spring Data JPA  
**Security:** JJWT 0.12.3, BCrypt, OWASP HTML Sanitizer, Bucket4j  
**Database:** H2 (in-memory)  
**Requirements:** Java 21, Maven 3.6+

---

## 🔐 SECURITY IMPLEMENTATION

### 1. JWT Authentication
**Files:** `JwtUtil.java`, `JwtAuthenticationFilter.java`, `SecurityConfig.java`

**Explanation:**  
JWT (JSON Web Token) provides stateless authentication - no need to store sessions on server. Each request sends token in `Authorization: Bearer {token}` header. Server validates token and authenticates user without querying database every time.

**Flow:**
```
Login → Validate credentials → Generate JWT → Return token
Request → Extract token from header → Validate → Authenticate
```

**Key Implementation:**
```java
// JwtUtil.java - Token generation
public String generateJwtToken(Authentication auth) {
    return Jwts.builder()
        .subject(userPrincipal.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 86400000))
        .signWith(getSigningKey())
        .compact();
}

// JwtAuthenticationFilter.java - Token validation
String jwt = parseJwt(request);
if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
    String username = jwtUtil.getUsernameFromJwtToken(jwt);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
}
```

### 2. Password Security (BCrypt)
**Files:** `SecurityConfig.java`, `AuthService.java`

**Explanation:**  
BCrypt is a one-way hashing algorithm (cannot be decoded). Each hash of the same password produces different results thanks to random salt. Work factor 12 = 2^12 = 4096 iterations, making brute-force attacks significantly slower.

```java
// BCrypt with 12 rounds (4096 iterations)
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}

// Usage in AuthService.java
user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
```

**Example:** `password123` → `$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi...`

### 3. HTTPS/SSL Configuration
**Files:** `application.properties`, `keystore.p12`

**Explanation:**  
HTTPS encrypts all data between client-server using SSL/TLS. Prevents Man-in-the-Middle attacks, protects JWT tokens and sensitive data during network transmission. Self-signed certificate is for development only, production requires CA-signed certificate.

```properties
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
```

### 4. XSS Protection
**Files:** `XSSFilter.java`, `FilterConfig.java`

**Explanation:**  
XSS (Cross-Site Scripting) attacks occur when hackers inject malicious scripts into input. XSSFilter sanitizes all input, escapes special characters (`<`, `>`, `"`, `'`) into HTML entities so scripts cannot execute.

```java
// XSSFilter.java - Sanitizes all input
private String sanitize(String value) {
    return Sanitizers.FORMATTING
        .and(Sanitizers.LINKS)
        .and(Sanitizers.BLOCKS)
        .sanitize(value);
}
```
**Example:** `<script>alert('xss')</script>` → `&lt;script&gt;alert('xss')&lt;/script&gt;`

### 5. SQL Injection Prevention
**Files:** `UserRepository.java`

**Explanation:**  
SQL Injection occurs when hackers inject SQL commands into input to manipulate database. JPA/Hibernate automatically uses parameterized queries (PreparedStatement), treating input as data rather than SQL code. Never concatenate strings to build queries.

```java
// JPA automatically parameterizes queries
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Safe from SQL injection
}
```

### 6. Rate Limiting
**Files:** `RateLimitingFilter.java`

**Explanation:**  
Rate Limiting restricts the number of requests from each IP within a time period. Uses Token Bucket algorithm: each IP has 100 tokens, each request consumes 1 token, refills 100 tokens/minute. Prevents DDoS attacks and brute-force login attempts.

```java
// Bucket4j - Token bucket algorithm
private Bucket createNewBucket() {
    Bandwidth limit = Bandwidth.simple(100, Duration.ofMinutes(1));
    return Bucket.builder().addLimit(limit).build();
}
```
**Limit:** 100 requests/minute per IP → Returns 429 if exceeded

### 7. Input Validation
**Files:** `RegisterRequest.java`, `LoginRequest.java`, `GlobalExceptionHandler.java`

**Explanation:**  
Validation ensures data is in correct format before processing. Jakarta Bean Validation annotations (`@NotBlank`, `@Email`, `@Size`) validate automatically. GlobalExceptionHandler catches validation errors and returns 400 Bad Request with clear error messages.

```java
@NotBlank(message = "Username is required")
@Size(min = 3, max = 50, message = "Username must be 3-50 characters")
private String username;

@NotBlank @Email(message = "Email must be valid")
private String email;

@NotBlank @Size(min = 6, max = 100)
private String password;
```

### 8. Role-Based Authorization
**Files:** `SecurityConfig.java`, `UserController.java`

**Explanation:**  
RBAC (Role-Based Access Control) controls access based on roles. USER has basic permissions, ADMIN has full access. `@PreAuthorize` checks role before executing method. Returns 403 Forbidden if user lacks permission.

```java
// SecurityConfig.java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)

// UserController.java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> getAllUsers() { ... }
```

### 9. CORS Configuration
**Files:** `SecurityConfig.java`

**Explanation:**  
CORS (Cross-Origin Resource Sharing) allows frontend from different domains to call API. Configures allowed origins (domains), methods (GET/POST/etc), headers (Authorization/Content-Type). Protects API from unauthorized cross-origin requests.

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://localhost:8443", "https://localhost:3000"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    return source;
}
```

---

## � PROJECT STRUCTURE

```
demo/
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java                    # Main application
│   ├── config/
│   │   └── FilterConfig.java                   # Filter configuration
│   ├── controller/
│   │   ├── AuthController.java                 # Auth endpoints
│   │   └── UserController.java                 # User endpoints
│   ├── dto/
│   │   ├── AuthResponse.java                   # Login response
│   │   ├── LoginRequest.java                   # Login request
│   │   ├── MessageResponse.java                # Message response
│   │   ├── RegisterRequest.java                # Registration request
│   │   └── UserResponse.java                   # User info response
│   ├── entity/
│   │   └── User.java                           # User entity
│   ├── exception/
│   │   └── GlobalExceptionHandler.java         # Exception handling
│   ├── filter/
│   │   ├── RateLimitingFilter.java             # Rate limiting
│   │   └── XSSFilter.java                      # XSS protection
│   ├── repository/
│   │   └── UserRepository.java                 # JPA repository
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java        # JWT filter
│   │   ├── JwtUtil.java                        # JWT utilities
│   │   ├── SecurityConfig.java                 # Security config
│   │   └── UserDetailsServiceImpl.java         # User details
│   └── service/
│       └── AuthService.java                    # Authentication service
├── src/main/resources/
│   ├── application.properties                  # App configuration
│   └── keystore.p12                            # SSL certificate
├── pom.xml                                     # Maven dependencies
├── README.md                                   # Documentation
└── Secure-API.postman_collection.json          # Postman tests
```

---

## 🚀 INSTALLATION & SETUP

### Prerequisites
- Java 21
- Maven 3.6+

### Run Application
```bash
# Build & run
mvn clean install
mvn spring-boot:run

# Or run JAR
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

**Access:** `https://localhost:8443`  
**H2 Console:** `https://localhost:8443/h2-console` (JDBC URL: `jdbc:h2:mem:securedb`, User: `sa`, Password: blank)

---

## 🧪 API TESTING WITH POSTMAN

### Setup
1. Import `Secure-API.postman_collection.json` into Postman
2. Disable SSL verification: **Settings → SSL certificate verification → OFF**

### Test Flow

**1. Register Users**
```
POST /api/auth/register
Body: {"username": "user1", "email": "user@test.com", "password": "pass123", "roles": ["USER"]}

POST /api/auth/register  
Body: {"username": "admin1", "email": "admin@test.com", "password": "admin123", "roles": ["USER", "ADMIN"]}
```

**2. Login & Get Token**
```
POST /api/auth/login (Login User) → Saves token to {{token}}
POST /api/auth/login (Login Admin) → Saves token to {{adminToken}}
```

**3. Test Protected Endpoints**
```
GET /api/users/me (with {{token}}) → Returns current user
GET /api/users/all (with {{adminToken}}) → Returns all users (ADMIN only)
GET /api/users/hello (with {{token}}) → "Hello User" or "Hello Admin"
GET /api/users/admin/hello (with {{adminToken}}) → "Hello Admin" (ADMIN only)
```

**4. Test Security**
```
- Login with wrong password → 401
- Access /api/users/me without token → 401
- Access /api/users/all with USER token → 403
- Make 100+ requests in 1 minute → 429 Rate Limited
```

---

## 📡 API ENDPOINTS

### Authentication (Public)
| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register user | `{username, email, password, roles}` |
| POST | `/api/auth/login` | Login | `{username, password}` |

### User Endpoints (Protected)
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/api/users/me` | USER | Get current user |
| GET | `/api/users/all` | ADMIN | Get all users |
| GET | `/api/users/hello` | USER | Greeting based on role |
| GET | `/api/users/admin/hello` | ADMIN | Admin greeting |

**Authorization Header:** `Bearer {token}`

---

## 🔍 REQUEST/RESPONSE EXAMPLES

### Register
```json
Request: POST /api/auth/register
{"username": "john", "email": "john@test.com", "password": "pass123", "roles": ["USER"]}

Response: 201
{"message": "User registered successfully!"}
```

### Login
```json
Request: POST /api/auth/login
{"username": "john", "password": "pass123"}

Response: 200
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "john",
  "roles": ["USER"]
}
```

### Get Current User
```json
Request: GET /api/users/me
Header: Authorization: Bearer {token}

Response: 200
{
  "id": 1,
  "username": "john",
  "email": "john@test.com",
  "roles": ["USER"],
  "active": true
}
```

### Hello Endpoint
```json
Request: GET /api/users/hello
Header: Authorization: Bearer {token}

Response: 200
{"message": "Hello User john"}  // or "Hello Admin john"
```

---