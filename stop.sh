#!/bin/bash

# Book Tracker Application Stop Script
# This script stops all running services

set -e

echo "🛑 Stopping Book Tracker Application..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Use docker compose if available, otherwise fall back to docker-compose
DOCKER_COMPOSE_CMD="docker compose"
if ! docker compose version >/dev/null 2>&1; then
    DOCKER_COMPOSE_CMD="docker-compose"
fi

# Stop frontend (kill any npm/node processes running on port 5173)
print_status "Stopping frontend..."
pkill -f "vite" 2>/dev/null || true
pkill -f "npm run dev" 2>/dev/null || true

# Stop backend services
print_status "Stopping backend services..."
cd book-backend
$DOCKER_COMPOSE_CMD down --remove-orphans

# Optional: Remove volumes (uncomment if you want to clear all data)
# print_warning "Removing volumes and data..."
# $DOCKER_COMPOSE_CMD down -v

print_success "All services stopped successfully!"
echo "To start again, run: ./deploy.sh"
