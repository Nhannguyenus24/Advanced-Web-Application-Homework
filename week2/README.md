# Sakila CRUD Spring Boot Application

Ứng dụng backend Spring Boot với full CRUD operations cho cơ sở dữ liệu Sakila. Ứng dụng sử dụng Logback + SLF4J để logging đàng hoàng và lưu trữ dữ liệu trong MySQL.

## 🚀 Tính năng

- **Full CRUD Operations** cho Actor, Category, Film, Customer
- **Logging chuyên nghiệp** với Logback + SLF4J
  - Console logging với màu sắc
  - File logging với rotation tự động
  - Async logging để tăng performance
  - Log levels có thể cấu hình
- **RESTful API** với response format chuẩn
- **Validation** đầy đủ cho tất cả input
- **Exception Handling** tập trung và nhất quán
- **JPA/Hibernate** để quản lý database
- **MySQL** làm database chính

## 📋 Yêu cầu

- **Java 21** hoặc cao hơn
- **Maven 3.6+**
- **MySQL 8.0+**
- IDE: IntelliJ IDEA, Eclipse, hoặc VS Code

## 🔧 Cài đặt và Chạy

### Bước 1: Clone Repository

```bash
git clone <repository-url>
cd week2
```

### Bước 2: Cấu hình MySQL

1. Tạo database `sakila`:
```sql
CREATE DATABASE sakila;
```

2. Import dữ liệu mẫu:
```bash
mysql -u root -p sakila < sakila-mysql.sql
```

### Bước 3: Cấu hình Application

Mở file `src/main/resources/application.properties` và điều chỉnh thông tin kết nối MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sakila?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
```

### Bước 4: Build và Run

```bash
# Sử dụng Maven wrapper (khuyến nghị)
./mvnw clean install
./mvnw spring-boot:run

# Hoặc trên Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run

# Hoặc sử dụng Maven đã cài đặt
mvn clean install
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: `http://localhost:8080`

## 📚 API Endpoints

### Actor APIs
- `GET /api/actors` - Lấy tất cả actors
- `GET /api/actors/{id}` - Lấy actor theo ID
- `POST /api/actors` - Tạo actor mới
- `PUT /api/actors/{id}` - Cập nhật actor
- `DELETE /api/actors/{id}` - Xóa actor
- `GET /api/actors/search?keyword={keyword}` - Tìm kiếm actor

### Category APIs
- `GET /api/categories` - Lấy tất cả categories
- `GET /api/categories/{id}` - Lấy category theo ID
- `POST /api/categories` - Tạo category mới
- `PUT /api/categories/{id}` - Cập nhật category
- `DELETE /api/categories/{id}` - Xóa category

### Film APIs
- `GET /api/films` - Lấy tất cả films
- `GET /api/films/{id}` - Lấy film theo ID
- `POST /api/films` - Tạo film mới
- `PUT /api/films/{id}` - Cập nhật film
- `DELETE /api/films/{id}` - Xóa film
- `GET /api/films/search?title={title}` - Tìm kiếm film theo title
- `GET /api/films/year/{year}` - Lấy films theo năm phát hành

### Customer APIs
- `GET /api/customers` - Lấy tất cả customers
- `GET /api/customers/{id}` - Lấy customer theo ID
- `POST /api/customers` - Tạo customer mới
- `PUT /api/customers/{id}` - Cập nhật customer
- `DELETE /api/customers/{id}` - Xóa customer
- `GET /api/customers/search?keyword={keyword}` - Tìm kiếm customer
- `GET /api/customers/active` - Lấy customers đang active

## 🧪 Testing

File `test-requests.http` chứa các request mẫu để test API. Bạn có thể sử dụng:
- **IntelliJ IDEA** - HTTP Client built-in
- **VS Code** - REST Client extension
- **Postman** - Import các request từ file
- **cURL** - Command line testing

### Ví dụ Test với cURL:

```bash
# Lấy tất cả actors
curl http://localhost:8080/api/actors

# Tạo actor mới
curl -X POST http://localhost:8080/api/actors \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Tom","lastName":"Cruise"}'

# Cập nhật actor
curl -X PUT http://localhost:8080/api/actors/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Tom","lastName":"Hanks"}'

# Xóa actor
curl -X DELETE http://localhost:8080/api/actors/201
```

## 📊 Logging

### Cấu hình Logging

Ứng dụng sử dụng Logback với các tính năng:
- **Console Appender**: Log ra console cho development
- **File Appender**: Log vào file `logs/week2.log`
- **Rolling Policy**: 
  - Max file size: 10MB
  - Max history: 14 days
  - Total size cap: 2GB
- **Async Appender**: Logging không đồng bộ để tăng performance

### Log Levels

```properties
# Application logs
logging.level.week2=DEBUG

# Spring Framework logs
logging.level.org.springframework.web=INFO

# Hibernate SQL logs
logging.level.org.hibernate.SQL=DEBUG
```

### Log Format

```
LEVEL|TIMESTAMP|THREAD|LOGGER|MESSAGE
```

Ví dụ:
```
INFO |2024-10-19 10:30:00|http-nio-8080-exec-1|week2.controller.ActorController|GET /api/actors - Fetching all actors
DEBUG|2024-10-19 10:30:01|http-nio-8080-exec-1|week2.service.ActorService|Found 200 actors
```

## 🏗️ Kiến trúc

```
week2/
├── controller/         # REST Controllers
│   ├── ActorController.java
│   ├── CategoryController.java
│   ├── FilmController.java
│   └── CustomerController.java
├── service/           # Business Logic Layer
│   ├── ActorService.java
│   ├── CategoryService.java
│   ├── FilmService.java
│   └── CustomerService.java
├── repository/        # Data Access Layer
│   ├── ActorRepository.java
│   ├── CategoryRepository.java
│   ├── FilmRepository.java
│   ├── CustomerRepository.java
│   └── LanguageRepository.java
├── entity/           # JPA Entities
│   ├── Actor.java
│   ├── Category.java
│   ├── Film.java
│   ├── Customer.java
│   └── Language.java
├── dto/              # Data Transfer Objects
│   ├── ActorDto.java
│   ├── CategoryDto.java
│   ├── FilmDto.java
│   ├── CustomerDto.java
│   └── ApiResponse.java
└── exception/        # Exception Handling
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java
```

## 🔐 Validation

Tất cả các DTOs đều có validation rules:

### Actor
- firstName: Required, max 45 characters
- lastName: Required, max 45 characters

### Category
- name: Required, max 25 characters

### Film
- title: Required, max 255 characters
- releaseYear: 1888-2100
- languageId: Required
- rentalDuration: Required, min 1
- rentalRate: Required, > 0
- replacementCost: Required, > 0

### Customer
- storeId: Required
- firstName: Required, max 45 characters
- lastName: Required, max 45 characters
- email: Valid email format, max 50 characters
- addressId: Required
- active: Required

## 📦 Dependencies

- **Spring Boot Starter Web** - RESTful APIs
- **Spring Boot Starter Data JPA** - Database access
- **Spring Boot Starter Validation** - Input validation
- **MySQL Connector J** - MySQL database driver
- **Lombok** - Reduce boilerplate code
- **Logback** - Logging framework (included in Spring Boot)

## 🛠️ Technologies

- **Spring Boot 3.5.6**
- **Java 21**
- **MySQL 8.0+**
- **Spring Data JPA / Hibernate**
- **Maven**
- **Logback + SLF4J**
- **Jakarta Bean Validation**

## 📝 Response Format

Tất cả API responses đều có format chuẩn:

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2024-10-19T10:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error message",
  "data": null,
  "timestamp": "2024-10-19T10:30:00"
}
```

## 📖 Documentation

Xem chi tiết API documentation tại [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

## 🤝 Contributing

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## 📄 License

This project is for educational purposes.

## 👨‍💻 Author

Week 2 - Advanced Web Development Homework

## 📞 Support

Nếu có vấn đề hoặc câu hỏi, vui lòng tạo issue trong repository.

---

**Happy Coding! 🚀**

