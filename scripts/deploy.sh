#!/bin/bash
set -e

ENVIRONMENT=$1
IMAGE_TAG=$2
APP_DIR="/opt/autolift"
APP_NAME="autolift-loyalty"
DOCKER_IMAGE="ghcr.io/${GITHUB_REPOSITORY_OWNER}/autolift-loyalty"

echo "=========================================="
echo "Deploying $APP_NAME to $ENVIRONMENT"
echo "Image tag: $IMAGE_TAG"
echo "=========================================="

cd $APP_DIR

deploy_production() {
    echo "[1/5] Pulling Docker image..."
    docker pull ${DOCKER_IMAGE}:${IMAGE_TAG}

    echo "[2/5] Stopping existing container..."
    docker stop ${APP_NAME} 2>/dev/null || true
    docker rm ${APP_NAME} 2>/dev/null || true

    echo "[3/5] Starting new container..."
    docker run -d \
        --name ${APP_NAME} \
        --restart unless-stopped \
        -p 8080:8080 \
        --env-file .env \
        -v /var/log/autolift:/var/log/autolift \
        ${DOCKER_IMAGE}:${IMAGE_TAG}

    echo "[4/5] Waiting for application to be healthy..."
    for i in {1..30}; do
        if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo "Application is healthy!"
            echo "[5/5] Deployment complete!"
            return 0
        fi
        echo "Waiting... ($i/30)"
        sleep 2
    done

    echo "ERROR: Application health check failed!"
    docker logs ${APP_NAME}
    return 1
}

case $ENVIRONMENT in
    production) deploy_production ;;
    *)
        echo "Usage: $0 {production} {image_tag}"
        exit 1
        ;;
esac