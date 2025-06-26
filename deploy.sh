#!/bin/bash

# Book Tracker Application Deployment Script
# This script deploys the backend services using Docker Compose and starts the frontend

set -e  # Exit on any error

echo "🚀 Starting Book Tracker Application Deployment..."

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

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose >/dev/null 2>&1 && ! docker compose version >/dev/null 2>&1; then
    print_error "Docker Compose is not available. Please install Docker Compose and try again."
    exit 1
fi

# Use docker compose if available, otherwise fall back to docker-compose
DOCKER_COMPOSE_CMD="docker compose"
if ! docker compose version >/dev/null 2>&1; then
    DOCKER_COMPOSE_CMD="docker-compose"
fi

# Function to cleanup on exit
cleanup() {
    print_warning "Deployment interrupted. Cleaning up..."
    cd book-backend
    $DOCKER_COMPOSE_CMD down
    exit 1
}

# Set trap for cleanup on script exit
trap cleanup INT TERM

# Step 1: Build the backend services
print_status "Building backend services..."
cd book-backend

# Clean and build all services
print_status "Cleaning and building Maven projects..."
./mvnw clean package -DskipTests

if [ $? -eq 0 ]; then
    print_success "Maven build completed successfully"
else
    print_error "Maven build failed"
    exit 1
fi

# Step 2: Stop any existing containers
print_status "Stopping existing containers..."
$DOCKER_COMPOSE_CMD down --remove-orphans

# Step 3: Build and start the containers
print_status "Building and starting Docker containers..."
$DOCKER_COMPOSE_CMD up --build -d

if [ $? -eq 0 ]; then
    print_success "Backend services started successfully"
else
    print_error "Failed to start backend services"
    exit 1
fi

# Step 4: Wait for services to be healthy
print_status "Waiting for services to be healthy..."
sleep 10

# Check if services are running
SERVICES=("postgres" "zookeeper" "kafka" "api-gateway" "auth-service" "book-service" "library-service" "notification-service" "analytics-service")

for service in "${SERVICES[@]}"; do
    if $DOCKER_COMPOSE_CMD ps $service | grep -q "Up\|running"; then
        print_success "$service is running"
    else
        print_warning "$service might not be running properly"
    fi
done

# Step 5: Display running services
print_status "Backend services status:"
$DOCKER_COMPOSE_CMD ps

# Step 6: Start the frontend
cd ../book-frontend

# Check if Node.js is installed
if ! command -v node >/dev/null 2>&1; then
    print_error "Node.js is not installed. Please install Node.js and try again."
    exit 1
fi

# Check if npm is installed
if ! command -v npm >/dev/null 2>&1; then
    print_error "npm is not installed. Please install npm and try again."
    exit 1
fi

print_status "Installing frontend dependencies..."
npm install

if [ $? -eq 0 ]; then
    print_success "Frontend dependencies installed successfully"
else
    print_error "Failed to install frontend dependencies"
    exit 1
fi

print_status "Starting frontend development server..."

# Create .env.local if it doesn't exist
if [ ! -f .env.local ]; then
    print_status "Creating .env.local file..."
    cat > .env.local << EOF
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=Book Tracker
VITE_APP_VERSION=1.0.0
EOF
    print_success ".env.local file created"
fi

# Start the frontend in the background
npm run dev &
FRONTEND_PID=$!

# Function to cleanup frontend on exit
cleanup_frontend() {
    print_warning "Shutting down application..."
    
    # Stop frontend
    if [ ! -z "$FRONTEND_PID" ]; then
        print_status "Stopping frontend..."
        kill $FRONTEND_PID 2>/dev/null || true
    fi
    
    # Stop backend services
    print_status "Stopping backend services..."
    cd ../book-backend
    $DOCKER_COMPOSE_CMD down
    
    print_success "Application shutdown complete"
    exit 0
}

# Update trap to include frontend cleanup
trap cleanup_frontend INT TERM

# Wait a bit for frontend to start
sleep 5

print_success "🎉 Book Tracker Application is now running!"
echo ""
echo "📍 Application URLs:"
echo "   Frontend: http://localhost:5173"
echo "   API Gateway: http://localhost:8080"
echo "   Auth Service: http://localhost:8081"
echo "   Book Service: http://localhost:8082"
echo "   Library Service: http://localhost:8083"
echo "   Notification Service: http://localhost:8084"
echo "   Analytics Service: http://localhost:8085"
echo ""
echo "📊 Monitoring:"
echo "   Docker containers: docker-compose ps"
echo "   Backend logs: docker-compose logs -f [service-name]"
echo "   Frontend logs: Check the terminal output above"
echo ""
echo "🛑 To stop the application, press Ctrl+C"
echo ""

# Keep the script running and show logs
print_status "Showing backend service logs (press Ctrl+C to stop)..."
cd ../book-backend
$DOCKER_COMPOSE_CMD logs -f
