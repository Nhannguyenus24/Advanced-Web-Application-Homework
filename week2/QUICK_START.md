# Quick Start Guide - Sakila CRUD Application

## ⚡ Bắt đầu nhanh trong 5 phút

### 1️⃣ Chuẩn bị Database

```sql
-- Tạo database
CREATE DATABASE sakila;

-- Import dữ liệu (trong terminal/cmd)
mysql -u root -p sakila < sakila-mysql.sql
```

### 2️⃣ Cấu hình Application

Mở `src/main/resources/application.properties` và thay đổi:

```properties
spring.datasource.username=root
spring.datasource.password=your_password  # <-- Đổi password của bạn
```

### 3️⃣ Chạy Application

**Windows:**
```cmd
run.bat
```

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

**Hoặc sử dụng Maven:**
```bash
./mvnw spring-boot:run
```

### 4️⃣ Test API

Ứng dụng chạy tại: `http://localhost:8080`

**Test ngay với cURL:**

```bash
# Lấy tất cả actors
curl http://localhost:8080/api/actors

# Tạo actor mới
curl -X POST http://localhost:8080/api/actors \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Tom","lastName":"Hanks"}'

# Lấy tất cả categories
curl http://localhost:8080/api/categories

# Lấy tất cả films
curl http://localhost:8080/api/films

# Lấy tất cả customers
curl http://localhost:8080/api/customers
```

## 📝 API Endpoints Chính

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/actors` | Lấy tất cả actors |
| POST | `/api/actors` | Tạo actor mới |
| PUT | `/api/actors/{id}` | Cập nhật actor |
| DELETE | `/api/actors/{id}` | Xóa actor |
| GET | `/api/categories` | Lấy tất cả categories |
| POST | `/api/categories` | Tạo category mới |
| GET | `/api/films` | Lấy tất cả films |
| POST | `/api/films` | Tạo film mới |
| GET | `/api/customers` | Lấy tất cả customers |
| POST | `/api/customers` | Tạo customer mới |

## 🔍 Kiểm tra Logs

Logs được lưu tại:
- **Console**: Hiển thị trực tiếp khi chạy
- **File**: `logs/week2.log`

## 📚 Tài liệu đầy đủ

- [README.md](./README.md) - Hướng dẫn chi tiết
- [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - API documentation đầy đủ
- [test-requests.http](./test-requests.http) - File test requests

## 🆘 Troubleshooting

### Lỗi: "Access denied for user"
➡️ Kiểm tra lại username/password trong `application.properties`

### Lỗi: "Unknown database 'sakila'"
➡️ Chạy lại lệnh tạo database và import dữ liệu

### Lỗi: Port 8080 đã được sử dụng
➡️ Thay đổi port trong `application.properties`:
```properties
server.port=8081
```

### Không thấy logs
➡️ Kiểm tra thư mục `logs/` trong project

## ✅ Checklist

- [ ] MySQL đã cài đặt và chạy
- [ ] Database `sakila` đã được tạo và import dữ liệu
- [ ] Java 21 đã cài đặt
- [ ] Đã cấu hình đúng username/password trong application.properties
- [ ] Application chạy thành công tại http://localhost:8080
- [ ] Test API thành công với cURL hoặc Postman

## 🎉 Hoàn thành!

Bạn đã có một ứng dụng Spring Boot CRUD hoàn chỉnh với:
- ✅ Full CRUD operations
- ✅ RESTful API
- ✅ Logging chuyên nghiệp với Logback + SLF4J
- ✅ MySQL database
- ✅ Validation và Exception handling
- ✅ Clean architecture

---

**Happy Coding! 🚀**

