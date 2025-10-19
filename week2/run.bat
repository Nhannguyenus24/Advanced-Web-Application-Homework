@echo off
echo ========================================
echo   Sakila CRUD Spring Boot Application
echo ========================================
echo.

echo [INFO] Building the application...
call mvnw.cmd clean package -DskipTests

if errorlevel 1 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)

echo.
echo [INFO] Starting the application...
echo [INFO] Application will be available at http://localhost:8080
echo [INFO] Press Ctrl+C to stop the application
echo.

call mvnw.cmd spring-boot:run

