#!/bin/bash

cd "$(dirname "$0")"

echo "Checking Docker Compose installation..."
if ! command -v docker compose &> /dev/null; then
    echo "Error: docker-compose is not installed. Please install it first."
    echo "You can install it using: sudo apt-get update && sudo apt-get install -y docker-compose"
    exit 1
fi

echo "Building all services..."
mvn clean install -DskipTests

echo "Building and starting Docker containers..."
docker compose up --build -d

echo "Build completed successfully!"
