#!/bin/bash

echo "========================================"
echo "  Sakila CRUD Spring Boot Application"
echo "========================================"
echo ""

echo "[INFO] Building the application..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "[ERROR] Build failed!"
    exit 1
fi

echo ""
echo "[INFO] Starting the application..."
echo "[INFO] Application will be available at http://localhost:8080"
echo "[INFO] Press Ctrl+C to stop the application"
echo ""

./mvnw spring-boot:run

