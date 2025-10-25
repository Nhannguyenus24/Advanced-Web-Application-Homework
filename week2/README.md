# Spring Boot REST API - Actor & Film Management

## 📋 Overview

REST API application for managing Actor and Film information using Spring Boot and MySQL Database (Sakila).

## 🛠️ Technologies Used

- **Java 21**: Programming language
- **Spring Boot 3.5.6**: Main framework
  - Spring Boot Starter Web: Building REST APIs
  - Spring Boot Starter Data JDBC: Database connection and operations
  - Spring Boot Starter Validation: Input data validation
- **MySQL 8.4**: Database management system
- **Maven**: Dependency and build management
- **Docker & Docker Compose**: MySQL database containerization
- **Lombok**: Reduce boilerplate code
- **SpringDoc OpenAPI (Swagger)**: Automatic API documentation
- **JUnit 5, Mockito**: Testing framework

## 📖 OpenAPI Documentation in Source Code

This project uses **SpringDoc OpenAPI 3** to automatically generate interactive API documentation. Here's how OpenAPI annotations are used in the source code:

### 1. Controller-Level Documentation

Add `@Tag` annotation at the class level to group and describe API endpoints:

```java
@Tag(name = "Actors", description = "CRUD operations for actor management")
@RestController
@RequestMapping("/api/actors")
public class ActorController {
    // ...
}
```

### 2. Operation-Level Documentation

Use `@Operation` annotation to document each endpoint:

```java
@Operation(
    summary = "Get all actors",
    description = "Returns a list of all actors, sorted by actor_id in ascending order",
    responses = {
        @ApiResponse(responseCode = "200",
            description = "Successfully retrieved actors",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @ApiResponse(responseCode = "404", 
            description = "Actor not found")
    }
)
@GetMapping
public ResponseEntity<ApiResponse<List<ActorResponse>>> getAllActors() {
    // ...
}
```

### 3. Parameter Documentation

Document path variables and request parameters:

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<ActorResponse>> getActorById(
    @Parameter(description = "Actor ID", example = "1")
    @PathVariable int id) {
    // ...
}
```

### 4. Request Body Documentation

Document request body with examples:

```java
@Operation(
    summary = "Create new actor",
    requestBody = @RequestBody(
        required = true,
        description = "Actor information to create",
        content = @Content(
            schema = @Schema(implementation = ActorRequest.class),
            examples = @ExampleObject(
                name = "NewActor",
                value = """
                {
                  "firstName": "Tom",
                  "lastName": "Cruise"
                }
                """
            )
        )
    )
)
@PostMapping
public ResponseEntity<ApiResponse<ActorResponse>> createActor(
    @Valid @RequestBody ActorRequest request) {
    // ...
}
```

### 5. DTO/Model Documentation

Use `@Schema` annotation to document DTOs and their fields:

```java
@Schema(description = "Actor creation/update request")
public class ActorRequest {
    
    @Schema(description = "Actor's first name", example = "Tom", required = true)
    @NotBlank(message = "First name is required")
    @Size(max = 45, message = "First name must not exceed 45 characters")
    private String firstName;
    
    @Schema(description = "Actor's last name", example = "Cruise", required = true)
    @NotBlank(message = "Last name is required")
    @Size(max = 45, message = "Last name must not exceed 45 characters")
    private String lastName;
}
```

### 6. Enum Documentation

Document enum values in models:

```java
public enum Rating {
    G, PG, PG_13, R, NC_17
}

@Schema(description = "Film rating (G, PG, PG_13, R, NC_17)", 
        example = "PG_13", 
        defaultValue = "G")
private Film.Rating rating = Film.Rating.G;
```

### Key OpenAPI Annotations Used:
- `@Tag`: Groups related endpoints together
- `@Operation`: Describes an API endpoint
- `@ApiResponse`: Documents response codes and types
- `@Parameter`: Describes path/query parameters
- `@RequestBody`: Documents request body
- `@Schema`: Describes data models and fields
- `@ExampleObject`: Provides example values
- `@Content`: Specifies content type and schema

### Benefits:
✅ Auto-generated interactive documentation at `/swagger-ui.html`  
✅ JSON API documentation at `/v3/api-docs`  
✅ Try-it-out feature for testing APIs directly  
✅ Always up-to-date with code changes  
✅ Clear examples for API consumers  

## ✅ Validation Implementation

This application uses **Jakarta Bean Validation (JSR-380)** to validate client parameters:

### 1. Validate Number of Fields
- Use annotations like `@NotNull`, `@NotBlank` to ensure required fields are present
- Example in `ActorRequest`:
  ```java
  @NotBlank(message = "First name is required")
  private String firstName;
  ```

### 2. Validate Datatype
- Spring Boot automatically validates data types when parsing JSON
- If client sends wrong data type (e.g., string for integer field), returns `400 Bad Request`
- Uses specific data types: `String`, `Short`, `Integer`, `double`, `enum`

### 3. Validate Field Values
- **@Size**: Limit string length
  ```java
  @Size(max = 45, message = "First name must not exceed 45 characters")
  private String firstName;
  ```
- **@Min, @Max**: Limit numeric values
  ```java
  @Min(value = 1900, message = "Release year must be >= 1900")
  @Max(value = 2100, message = "Release year must be <= 2100")
  private Short releaseYear;
  ```
- **@Positive, @PositiveOrZero**: Ensure positive numbers
  ```java
  @Positive(message = "Language ID must be positive")
  private Short languageId;
  ```
- **@DecimalMin**: Minimum decimal value
  ```java
  @DecimalMin(value = "0.00", message = "Rental rate must be non-negative")
  private double rentalRate;
  ```
- **@NotNull**: Field cannot be null
  ```java
  @NotNull
  private Film.Rating rating;
  ```

### How It Works
- `@Valid` annotation in Controller triggers validation
- If validation fails, returns `400 Bad Request` with detailed error messages
- `GlobalExceptionHandler` processes and formats validation errors

## 📥 Installation Guide

### 1. Install Java 21
- Download Java 21 JDK from: https://www.oracle.com/java/technologies/downloads/
- Or use OpenJDK: https://jdk.java.net/21/
- Verify installation:
  ```bash
  java -version
  ```

### 2. Install Maven
**Windows:**
- Download Maven from: https://maven.apache.org/download.cgi
- Extract to directory (e.g., `C:\Program Files\Apache\maven`)
- Add to PATH: `C:\Program Files\Apache\maven\bin`
- Verify:
  ```bash
  mvn -version
  ```

**Linux/Mac:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install maven

# MacOS
brew install maven

# Verify
mvn -version
```

### 3. Install Docker & Docker Compose
**Windows:**
- Download Docker Desktop: https://www.docker.com/products/docker-desktop
- Install and start Docker Desktop
- Docker Compose is already integrated

**Linux:**
```bash
# Install Docker
sudo apt update
sudo apt install docker.io
sudo systemctl start docker
sudo systemctl enable docker

# Install Docker Compose
sudo apt install docker-compose

# Verify
docker --version
docker-compose --version
```

**MacOS:**
```bash
# Using Homebrew
brew install --cask docker
# Or download Docker Desktop from website
```

## 🚀 How to Run the Application

### Step 1: Start MySQL with Docker
```bash
# Navigate to project root directory (contains docker-compose.yml)
cd D:\submission\week2

# Start MySQL container
docker-compose up -d

# Check running containers
docker ps
```

MySQL will run at `localhost:3306` with credentials:
- Database: `mydb`
- Username: `root`
- Password: `root123`
- Sakila data will be automatically imported from `sakila-mysql.sql`

### Step 2: Build and Run Spring Boot Application
```bash
# Navigate to Spring Boot project directory
cd week2

# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

Or run from JAR file:
```bash
# Build JAR file
mvn clean package

# Run JAR
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

Application will run at: **http://localhost:8080**

### Step 3: Access Swagger UI
Open browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

## 📡 Available APIs

### Actor APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/actors` | Get list of all actors |
| GET | `/api/actors/{id}` | Get actor by ID |
| POST | `/api/actors` | Create new actor |
| PATCH | `/api/actors` | Update actor information |
| DELETE | `/api/actors/{id}` | Delete actor by ID |

### Film APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/films` | Get list of all films |
| GET | `/api/films/{id}` | Get film by ID |
| POST | `/api/films` | Create new film |
| PUT | `/api/films/{id}` | Update film information |
| DELETE | `/api/films/{id}` | Delete film by ID |

## 🧪 cURL Commands for Testing APIs

### Actor APIs

#### 1. Get all actors
```bash
curl -X GET "http://localhost:8080/api/actors" -H "accept: application/json"
```

#### 2. Get actor by ID
```bash
curl -X GET "http://localhost:8080/api/actors/1" -H "accept: application/json"
```

#### 3. Create new actor
```bash
curl -X POST "http://localhost:8080/api/actors" \
  -H "Content-Type: application/json" \
  -d "{\"firstName\":\"Tom\",\"lastName\":\"Cruise\"}"
```

**Test validation error (missing firstName):**
```bash
curl -X POST "http://localhost:8080/api/actors" \
  -H "Content-Type: application/json" \
  -d "{\"lastName\":\"Cruise\"}"
```

**Test validation error (firstName too long):**
```bash
curl -X POST "http://localhost:8080/api/actors" \
  -H "Content-Type: application/json" \
  -d "{\"firstName\":\"ThisIsAVeryLongFirstNameThatExceedsTheMaximumLengthAllowed\",\"lastName\":\"Cruise\"}"
```

#### 4. Update actor
```bash
curl -X PATCH "http://localhost:8080/api/actors" \
  -H "Content-Type: application/json" \
  -d "{\"actorId\":1,\"firstName\":\"Thomas\",\"lastName\":\"Cruise\"}"
```

#### 5. Delete actor
```bash
curl -X DELETE "http://localhost:8080/api/actors/1" -H "accept: application/json"
```

### Film APIs

#### 1. Get all films
```bash
curl -X GET "http://localhost:8080/api/films" -H "accept: application/json"
```

#### 2. Get film by ID
```bash
curl -X GET "http://localhost:8080/api/films/1" -H "accept: application/json"
```

#### 3. Create new film
```bash
curl -X POST "http://localhost:8080/api/films" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"The Matrix\",
    \"description\": \"A computer hacker learns about the true nature of reality.\",
    \"releaseYear\": 1999,
    \"languageId\": 1,
    \"originalLanguageId\": 2,
    \"rentalDuration\": 3,
    \"rentalRate\": 4.99,
    \"length\": 136,
    \"replacementCost\": 19.99,
    \"rating\": \"R\",
    \"specialFeatures\": [\"TRAILERS\", \"DELETED_SCENES\"]
  }"
```

**Test validation error (missing title):**
```bash
curl -X POST "http://localhost:8080/api/films" \
  -H "Content-Type: application/json" \
  -d "{
    \"description\": \"A computer hacker learns about the true nature of reality.\",
    \"releaseYear\": 1999,
    \"languageId\": 1
  }"
```

**Test validation error (invalid releaseYear):**
```bash
curl -X POST "http://localhost:8080/api/films" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"The Matrix\",
    \"releaseYear\": 1800,
    \"languageId\": 1
  }"
```

**Test validation error (invalid languageId - negative number):**
```bash
curl -X POST "http://localhost:8080/api/films" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"The Matrix\",
    \"languageId\": -1
  }"
```

#### 4. Update film
```bash
curl -X PUT "http://localhost:8080/api/films/1" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"The Matrix Reloaded\",
    \"description\": \"Sequel description...\",
    \"releaseYear\": 2003,
    \"languageId\": 1,
    \"originalLanguageId\": 2,
    \"rentalDuration\": 5,
    \"rentalRate\": 5.99,
    \"length\": 138,
    \"replacementCost\": 24.99,
    \"rating\": \"PG_13\",
    \"specialFeatures\": [\"TRAILERS\", \"COMMENTARIES\"]
  }"
```

#### 5. Delete film
```bash
curl -X DELETE "http://localhost:8080/api/films/1" -H "accept: application/json"
```

## 🧪 Run Unit Tests
```bash
cd week2
mvn test
```

## 📊 View Code Coverage Report
```bash
mvn test
# Report will be generated at: target/site/jacoco/index.html
```

## 🛑 Stop the Application

### Stop Spring Boot
- Press `Ctrl + C` in the terminal running the application

### Stop MySQL Docker
```bash
docker-compose down
```

### Stop and remove data
```bash
docker-compose down -v
```

## 📚 Additional Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/v3/api-docs

## ⚠️ Important Notes

1. Ensure ports 8080 and 3306 are not being used by other applications
2. Docker must be started before running `docker-compose`
3. Wait for MySQL container to fully start (~10-20 seconds) before running Spring Boot
4. If you encounter database connection errors, check MySQL container: `docker logs mysql-container`

## 📝 Response Structure

### Success Response
```json
{
  "success": true,
  "message": "Successfully retrieved all actors",
  "data": [...]
}
```

### Error Response
```json
{
  "success": false,
  "message": "Actor not found with id: 999",
  "data": null
}
```

### Validation Error Response
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "firstName": "First name is required",
    "lastName": "Last name must not exceed 45 characters"
  }
}
```

## 🎯 Valid Rating Values for Film
- `G` - General Audiences
- `PG` - Parental Guidance Suggested
- `PG_13` - Parents Strongly Cautioned
- `R` - Restricted
- `NC_17` - Adults Only

## 🎬 Valid Special Features for Film
- `TRAILERS`
- `COMMENTARIES`
- `DELETED_SCENES`
- `BEHIND_THE_SCENES`
