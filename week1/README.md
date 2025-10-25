# Week 1 - Actor Management API

## Framework and Technologies

- **Spring Boot** 3.5.7
- **Spring Data JPA** - ORM framework
- **MySQL** 8.4 - Database
- **Lombok** - Reduce boilerplate code
- **Java** 25
- **Maven** - Build tool

## Features

RESTful API for managing actors with full CRUD operations:

1. **Get all actors**
2. **Get actor by ID**
3. **Create new actor**
4. **Update actor information**
5. **Delete actor**

## How to Run

### Prerequisites

Install the following tools before running the application:

#### 1. Install Docker

**Windows:**
- Download and install [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)
- After installation, restart your computer
- Verify installation: `docker --version`

**Linux:**
```bash
sudo apt-get update
sudo apt-get install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
```

**Mac:**
- Download and install [Docker Desktop for Mac](https://www.docker.com/products/docker-desktop/)
- Verify installation: `docker --version`

#### 2. Install Maven

**Windows:**
- Download Maven from [Apache Maven](https://maven.apache.org/download.cgi)
- Extract to `C:\Program Files\Apache\maven`
- Add `C:\Program Files\Apache\maven\bin` to System PATH
- Verify installation: `mvn --version`

**Linux:**
```bash
sudo apt-get update
sudo apt-get install maven
```

**Mac:**
```bash
brew install maven
```

#### 3. Install Java 25 (or Java 17+)

- Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://jdk.java.net/)
- Set JAVA_HOME environment variable
- Verify installation: `java --version`

### Running the Application

#### 1. Start MySQL Database

```bash
docker-compose up -d
```

#### 2. Run Spring Boot Application

```bash
cd week1
mvn spring-boot:run
```

Or build and run JAR file:

```bash
cd week1
mvn clean package
java -jar target/week1-0.0.1-SNAPSHOT.jar
```

Application will run at: `http://localhost:8080`

## Test API with cURL

### 1. Get all actors

```bash
curl -X GET http://localhost:8080/api/actors
```

### 2. Get actor by ID

```bash
curl -X GET http://localhost:8080/api/actors/1
```

### 3. Create new actor

```bash
curl -X POST http://localhost:8080/api/actors ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"John\",\"lastName\":\"Doe\"}"
```

**Linux/Mac:**
```bash
curl -X POST http://localhost:8080/api/actors \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe"}'
```

### 4. Update actor

```bash
curl -X PUT http://localhost:8080/api/actors/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"Jane\",\"lastName\":\"Smith\"}"
```

**Linux/Mac:**
```bash
curl -X PUT http://localhost:8080/api/actors/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Smith"}'
```

### 5. Delete actor

```bash
curl -X DELETE http://localhost:8080/api/actors/1
```

## Database Configuration

- **Host:** localhost
- **Port:** 3306
- **Database:** mydb
- **Username:** root
- **Password:** root123

Database automatically imports data from `sakila-mysql.sql` file on first startup.

## Stop Application

Stop Spring Boot: `Ctrl + C`

Stop MySQL container:
```bash
docker-compose down
```

Remove volumes (data will be lost):
```bash
docker-compose down -v
```

