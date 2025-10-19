# Sakila API Documentation

## Overview
This is a Spring Boot REST API application for managing the Sakila database with full CRUD operations. The application uses proper logging with Logback + SLF4J and stores data in MySQL.

## Technology Stack
- **Spring Boot 3.5.6**
- **Java 21**
- **MySQL Database**
- **Spring Data JPA**
- **Lombok**
- **Logback + SLF4J** for logging
- **Jakarta Validation**

## Setup Instructions

### Prerequisites
1. Java 21 or higher
2. MySQL Server
3. Maven

### Database Setup
1. Create a MySQL database named `sakila`
2. Import the `sakila-mysql.sql` file:
```bash
mysql -u root -p sakila < sakila-mysql.sql
```

### Application Configuration
Update `src/main/resources/application.properties` with your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sakila
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Running the Application
```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Actor Management

#### Get All Actors
```
GET /api/actors
```
Response:
```json
{
  "success": true,
  "message": "Actors retrieved successfully",
  "data": [
    {
      "actorId": 1,
      "firstName": "PENELOPE",
      "lastName": "GUINESS"
    }
  ],
  "timestamp": "2024-10-19T10:30:00"
}
```

#### Get Actor by ID
```
GET /api/actors/{id}
```

#### Create Actor
```
POST /api/actors
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Update Actor
```
PUT /api/actors/{id}
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Smith"
}
```

#### Delete Actor
```
DELETE /api/actors/{id}
```

#### Search Actors
```
GET /api/actors/search?keyword=john
```

---

### Category Management

#### Get All Categories
```
GET /api/categories
```

#### Get Category by ID
```
GET /api/categories/{id}
```

#### Create Category
```
POST /api/categories
Content-Type: application/json

{
  "name": "Documentary"
}
```

#### Update Category
```
PUT /api/categories/{id}
Content-Type: application/json

{
  "name": "Action"
}
```

#### Delete Category
```
DELETE /api/categories/{id}
```

---

### Film Management

#### Get All Films
```
GET /api/films
```

#### Get Film by ID
```
GET /api/films/{id}
```

#### Create Film
```
POST /api/films
Content-Type: application/json

{
  "title": "New Movie",
  "description": "An exciting new film",
  "releaseYear": 2024,
  "languageId": 1,
  "rentalDuration": 3,
  "rentalRate": 2.99,
  "length": 120,
  "replacementCost": 19.99,
  "rating": "PG-13",
  "specialFeatures": "Trailers,Commentaries"
}
```

#### Update Film
```
PUT /api/films/{id}
Content-Type: application/json

{
  "title": "Updated Movie Title",
  "description": "Updated description",
  "releaseYear": 2024,
  "languageId": 1,
  "rentalDuration": 5,
  "rentalRate": 3.99,
  "length": 130,
  "replacementCost": 22.99,
  "rating": "R"
}
```

#### Delete Film
```
DELETE /api/films/{id}
```

#### Search Films by Title
```
GET /api/films/search?title=academy
```

#### Get Films by Year
```
GET /api/films/year/2006
```

---

### Customer Management

#### Get All Customers
```
GET /api/customers
```

#### Get Customer by ID
```
GET /api/customers/{id}
```

#### Create Customer
```
POST /api/customers
Content-Type: application/json

{
  "storeId": 1,
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "addressId": 5,
  "active": true
}
```

#### Update Customer
```
PUT /api/customers/{id}
Content-Type: application/json

{
  "storeId": 1,
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "addressId": 5,
  "active": true
}
```

#### Delete Customer
```
DELETE /api/customers/{id}
```

#### Search Customers
```
GET /api/customers/search?keyword=john
```

#### Get Active Customers
```
GET /api/customers/active
```

---

## Error Handling

The API uses a consistent error response format:

```json
{
  "success": false,
  "message": "Error message here",
  "data": null,
  "timestamp": "2024-10-19T10:30:00"
}
```

### HTTP Status Codes
- `200 OK` - Successful GET, PUT, DELETE
- `201 Created` - Successful POST
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server errors

## Logging

The application uses Logback with SLF4J for comprehensive logging:

### Log Configuration
- Console output for development
- File logging with rotation (logs/week2.log)
- Maximum file size: 10MB
- Maximum history: 14 days
- Total size cap: 2GB
- Async logging for better performance

### Log Levels
- `week2` package: DEBUG
- `org.springframework`: INFO
- `org.hibernate.SQL`: DEBUG (when enabled)
- Root: INFO

### Log Format
```
LEVEL|TIMESTAMP|THREAD|LOGGER|MESSAGE
```

Example:
```
INFO |2024-10-19 10:30:00|http-nio-8080-exec-1|week2.controller.ActorController|GET /api/actors - Fetching all actors
DEBUG|2024-10-19 10:30:00|http-nio-8080-exec-1|week2.service.ActorService|Found 200 actors
```

## Validation

The API includes comprehensive validation:

### Actor
- `firstName`: Required, max 45 characters
- `lastName`: Required, max 45 characters

### Category
- `name`: Required, max 25 characters

### Film
- `title`: Required, max 255 characters
- `releaseYear`: Between 1888 and 2100
- `languageId`: Required
- `rentalDuration`: Required, minimum 1
- `rentalRate`: Required, greater than 0
- `replacementCost`: Required, greater than 0
- `rating`: Max 10 characters

### Customer
- `storeId`: Required
- `firstName`: Required, max 45 characters
- `lastName`: Required, max 45 characters
- `email`: Valid email format, max 50 characters
- `addressId`: Required
- `active`: Required

## Database Schema

The application works with the following main tables:
- `actor` - Actor information
- `category` - Film categories
- `film` - Film details
- `language` - Available languages
- `customer` - Customer information

## Testing with cURL

### Create an Actor
```bash
curl -X POST http://localhost:8080/api/actors \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Tom","lastName":"Hanks"}'
```

### Get All Actors
```bash
curl http://localhost:8080/api/actors
```

### Update an Actor
```bash
curl -X PUT http://localhost:8080/api/actors/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Thomas","lastName":"Hanks"}'
```

### Delete an Actor
```bash
curl -X DELETE http://localhost:8080/api/actors/1
```

## Development Notes

- All timestamps are automatically managed by JPA
- The application uses `@Transactional` for database operations
- DTOs are used to separate API contracts from entity models
- Global exception handling provides consistent error responses
- All endpoints support CORS for frontend integration

