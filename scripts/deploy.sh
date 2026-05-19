#!/bin/bash
set -e

ENVIRONMENT=$1
IMAGE_TAG=$2
APP_DIR="/opt/autolift"
APP_NAME="autolift-loyalty"
DOCKER_IMAGE="ghcr.io/${GITHUB_REPOSITORY_OWNER:-autolift}/autolift-loyalty"

echo "Deploying $APP_NAME to $ENVIRONMENT"
echo "Image tag: $IMAGE_TAG"

cd $APP_DIR

if [ ! -d ".git" ]; then
    echo "Initializing git repository..."
    git init
    git remote add origin https://github.com/${GITHUB_REPOSITORY:-autolift/autolift-loyalty}.git
fi

git fetch origin
git checkout main
git pull origin main

if [ -f docker-compose.prod.yml ]; then
    echo "Stopping existing containers..."
    docker-compose -f docker-compose.prod.yml down || true
fi

echo "Pulling new image..."
docker pull $DOCKER_IMAGE:$IMAGE_TAG

if [ -f docker-compose.prod.yml ]; then
    echo "Starting application..."
    IMAGE_TAG=$IMAGE_TAG docker-compose -f docker-compose.prod.yml up -d
else
    echo "Starting container directly..."
    docker run -d \
        --name $APP_NAME \
        --restart unless-stopped \
        -p 8080:8080 \
        -e SPRING_PROFILES_ACTIVE=prod \
        -e POSTGRES_HOST=localhost \
        -e POSTGRES_DB=${POSTGRES_DB} \
        -e POSTGRES_USER=${POSTGRES_USER} \
        -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
        -e REDIS_HOST=localhost \
        -e JWT_SECRET=${JWT_SECRET} \
        $DOCKER_IMAGE:$IMAGE_TAG
fi

echo "Waiting for application to be healthy..."
for i in {1..30}; do
    if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "Application is healthy!"
        exit 0
    fi
    echo "Waiting... ($i/30)"
    sleep 2
done

echo "Application health check failed!"
docker logs $APP_NAME
exit 1