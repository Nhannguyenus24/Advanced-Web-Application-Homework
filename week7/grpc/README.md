# gRPC CRUD Service with Spring Boot

This is a complete implementation of a gRPC-based Product CRUD service using Spring Boot, JPA persistence, and H2 database.

## Project Structure

```
grpc/
├── src/
│   ├── main/
│   │   ├── java/com/example/grpc/
│   │   │   ├── GrpcApplication.java          # Main application entry point
│   │   │   ├── model/
│   │   │   │   └── Product.java              # JPA Entity
│   │   │   ├── repository/
│   │   │   │   └── ProductRepository.java    # JPA Repository
│   │   │   ├── service/
│   │   │   │   └── ProductServiceImpl.java   # Business logic service
│   │   │   ├── grpc/
│   │   │   │   └── ProductGrpcService.java   # gRPC service implementation
│   │   │   └── client/
│   │   │       └── ProductGrpcClient.java    # gRPC client for testing
│   │   ├── proto/
│   │   │   └── product.proto                 # Protocol Buffers definition
│   │   └── resources/
│   │       └── application.properties        # Application configuration
│   └── test/
│       └── java/com/example/grpc/
│           ├── service/
│           │   └── ProductServiceImplTest.java      # Service unit tests
│           └── grpc/
│               └── ProductGrpcServiceTest.java      # gRPC service unit tests
└── pom.xml
```

## Technologies Used

- **Spring Boot 3.5.7** - Application framework
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database
- **gRPC** - Remote procedure call framework
- **Protocol Buffers** - Data serialization
- **Lombok** - Reduce boilerplate code
- **JUnit 5 & Mockito** - Testing

## Features

The service provides a complete CRUD API for Product management:

1. **Create Product** - Add a new product to the database
2. **Get Product** - Retrieve a product by ID
3. **Update Product** - Update an existing product
4. **Delete Product** - Remove a product from the database
5. **List Products** - Get all products

## Protocol Buffers Definition

The `product.proto` file defines the gRPC service contract:

```protobuf
service ProductService {
  rpc CreateProduct (ProductRequest) returns (ProductResponse);
  rpc GetProduct (ProductIdRequest) returns (ProductResponse);
  rpc UpdateProduct (ProductRequest) returns (ProductResponse);
  rpc DeleteProduct (ProductIdRequest) returns (DeleteResponse);
  rpc ListProducts (Empty) returns (ProductListResponse);
}
```

## Prerequisites

- Java 21 or higher
- Maven 3.6+

## Getting Started

### 1. Clone and Navigate to Project

```bash
cd week7/grpc
```

### 2. Build the Project

This will generate the gRPC stubs from the `.proto` file:

```bash
./mvnw clean install
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The gRPC server will start on port **9090** by default.

### 4. Run the Test Client

In a new terminal, run the standalone client to test the CRUD operations:

```bash
./mvnw exec:java -Dexec.mainClass="com.example.grpc.client.ProductGrpcClient"
```

## Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# gRPC Server Configuration
grpc.server.port=9090

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:productdb
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console (for debugging)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

## Testing

### Run Unit Tests

```bash
./mvnw test
```

### Run Integration Tests

```bash
./mvnw verify
```

### Test Coverage

The project includes:
- **Service layer tests** (`ProductServiceImplTest.java`) - Tests business logic with mocked repository
- **gRPC service tests** (`ProductGrpcServiceTest.java`) - Tests gRPC endpoint behavior

## API Examples

### Using the Java Client

```java
ProductGrpcClient client = new ProductGrpcClient("localhost", 9090);

// Create a product
ProductResponse product = client.createProduct(
    "Laptop", 
    "High-performance laptop", 
    1299.99
);

// Get a product
ProductResponse retrieved = client.getProduct(product.getId());

// Update a product
ProductResponse updated = client.updateProduct(
    product.getId(),
    "Gaming Laptop",
    "High-performance gaming laptop",
    1499.99
);

// Delete a product
DeleteResponse deleted = client.deleteProduct(product.getId());

// List all products
ProductListResponse list = client.listProducts();

client.shutdown();
```

### Using grpcurl (Command Line)

Install grpcurl: https://github.com/fullstorydev/grpcurl

```bash
# List services
grpcurl -plaintext localhost:9090 list

# Create a product
grpcurl -plaintext -d '{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 1299.99
}' localhost:9090 ProductService/CreateProduct

# Get a product
grpcurl -plaintext -d '{"id": 1}' localhost:9090 ProductService/GetProduct

# List all products
grpcurl -plaintext -d '{}' localhost:9090 ProductService/ListProducts

# Update a product
grpcurl -plaintext -d '{
  "id": 1,
  "name": "Gaming Laptop",
  "description": "Updated description",
  "price": 1499.99
}' localhost:9090 ProductService/UpdateProduct

# Delete a product
grpcurl -plaintext -d '{"id": 1}' localhost:9090 ProductService/DeleteProduct
```

## Architecture

### Layered Architecture

```
Client Request
    ↓
ProductGrpcService (gRPC Layer)
    ↓
ProductServiceImpl (Business Logic Layer)
    ↓
ProductRepository (Data Access Layer)
    ↓
H2 Database
```

### Key Components

1. **Product Entity** - JPA entity representing the database table
2. **ProductRepository** - Spring Data JPA repository for database operations
3. **ProductServiceImpl** - Service layer containing business logic
4. **ProductGrpcService** - gRPC service implementation that handles RPC calls
5. **ProductGrpcClient** - Client application to test the gRPC service

## Database Access

While the application is running, you can access the H2 console:

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:productdb`
- Username: `sa`
- Password: (leave empty)

## Advantages of gRPC

1. **Performance** - Uses HTTP/2 and Protocol Buffers for efficient binary serialization
2. **Strongly Typed** - Contract-first API with `.proto` files
3. **Multi-language Support** - Generate clients and servers in multiple languages
4. **Streaming** - Supports bidirectional streaming (not used in this example)
5. **Code Generation** - Automatic generation of client and server stubs

## Common Issues

### Issue: Generated code not found
**Solution**: Run `./mvnw clean install` to generate gRPC stubs from `.proto` file

### Issue: Port 9090 already in use
**Solution**: Change `grpc.server.port` in `application.properties`

### Issue: Connection refused
**Solution**: Make sure the gRPC server is running before starting the client

## Future Enhancements

- [ ] Add authentication and authorization
- [ ] Implement streaming operations
- [ ] Add Swagger/OpenAPI documentation
- [ ] Deploy using Docker
- [ ] Add MySQL/PostgreSQL support
- [ ] Implement pagination for list operations
- [ ] Add validation and error handling
- [ ] Implement caching with Redis

## License

This project is for educational purposes.

## References

- [gRPC Official Documentation](https://grpc.io/docs/)
- [Spring Boot gRPC](https://docs.spring.io/spring-grpc/reference/)
- [Protocol Buffers](https://protobuf.dev/)
