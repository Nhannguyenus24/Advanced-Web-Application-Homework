#!/bin/bash

# Week1 Test Runner Script
# This script runs all tests for the Actor Management API

echo "🚀 Starting Week1 Actor Management API Tests..."
echo "================================================"

# Change to week1 directory
cd "$(dirname "$0")"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Maven wrapper exists
if [ ! -f "./mvnw" ]; then
    print_error "Maven wrapper not found. Please ensure you're in the week1 directory."
    exit 1
fi

# Make Maven wrapper executable
chmod +x mvnw

# Clean previous builds
print_status "Cleaning previous builds..."
./mvnw clean

# Compile the project
print_status "Compiling project..."
if ./mvnw compile; then
    print_success "Compilation successful"
else
    print_error "Compilation failed"
    exit 1
fi

# Run unit tests
print_status "Running unit tests..."
if ./mvnw test -Dtest=!*IntegrationTest; then
    print_success "Unit tests passed"
else
    print_error "Unit tests failed"
    exit 1
fi

# Run integration tests (if MySQL is available)
print_status "Checking MySQL availability..."
if command -v mysql &> /dev/null; then
    if mysql -h127.0.0.1 -uroot -proot123 -e "SELECT 1;" 2>/dev/null; then
        print_status "Running integration tests..."
        if ./mvnw test -Dtest=*IntegrationTest; then
            print_success "Integration tests passed"
        else
            print_warning "Integration tests failed (this may be due to database configuration)"
        fi
    else
        print_warning "MySQL not accessible with default credentials. Skipping integration tests."
        print_warning "To run integration tests, ensure MySQL is running with:"
        print_warning "  - Host: localhost:3306"
        print_warning "  - Username: root"
        print_warning "  - Password: root123"
        print_warning "  - Database: mydb"
    fi
else
    print_warning "MySQL not found. Skipping integration tests."
fi

# Generate test reports
print_status "Generating test reports..."
./mvnw surefire-report:report

# Generate code coverage
print_status "Generating code coverage report..."
./mvnw jacoco:report

# Summary
echo ""
echo "📊 Test Summary"
echo "==============="

if [ -f "target/surefire-reports/TEST-*.xml" ]; then
    # Count tests
    TOTAL_TESTS=$(grep -h "tests=" target/surefire-reports/TEST-*.xml | sed 's/.*tests="\([0-9]*\)".*/\1/' | awk '{sum += $1} END {print sum}')
    FAILED_TESTS=$(grep -h "failures=" target/surefire-reports/TEST-*.xml | sed 's/.*failures="\([0-9]*\)".*/\1/' | awk '{sum += $1} END {print sum}')
    ERROR_TESTS=$(grep -h "errors=" target/surefire-reports/TEST-*.xml | sed 's/.*errors="\([0-9]*\)".*/\1/' | awk '{sum += $1} END {print sum}')
    
    echo "Total tests: $TOTAL_TESTS"
    echo "Failed tests: $FAILED_TESTS"
    echo "Error tests: $ERROR_TESTS"
    
    if [ "$FAILED_TESTS" -eq 0 ] && [ "$ERROR_TESTS" -eq 0 ]; then
        print_success "All tests passed! 🎉"
    else
        print_error "Some tests failed. Check the reports for details."
    fi
else
    print_warning "No test results found."
fi

# Show report locations
echo ""
echo "📋 Generated Reports"
echo "===================="
echo "Test Report: target/site/surefire-report.html"
echo "Coverage Report: target/site/jacoco/index.html"

print_success "Test execution completed!"

# Open reports if on macOS/Linux with desktop environment
if command -v xdg-open &> /dev/null; then
    read -p "Do you want to open the test report? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        xdg-open target/site/surefire-report.html
    fi
elif command -v open &> /dev/null; then
    read -p "Do you want to open the test report? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        open target/site/surefire-report.html
    fi
fi