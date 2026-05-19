#!/bin/bash
set -euo pipefail

ENVIRONMENT="${1:-}"
IMAGE_REF="${2:-}"

APP_DIR="/opt/autolift"
APP_NAME="autolift-loyalty"

echo "=========================================="
echo "Deploying ${APP_NAME} to ${ENVIRONMENT}"
echo "Image ref: ${IMAGE_REF}"
echo "=========================================="

if [ -z "${ENVIRONMENT}" ] || [ -z "${IMAGE_REF}" ]; then
  echo "Usage: $0 production ghcr.io/lqtan/autolift-loyalty:<tag>"
  exit 1
fi

if [ "${ENVIRONMENT}" != "production" ]; then
  echo "Only production environment is supported."
  exit 1
fi

cd "${APP_DIR}"

echo "[0/6] Checking required files..."
if [ ! -f ".env" ]; then
  echo "ERROR: .env file not found in ${APP_DIR}"
  exit 1
fi

echo "[1/6] Login to GHCR if credentials are provided..."
if [ -n "${GHCR_USERNAME:-}" ] && [ -n "${GHCR_TOKEN:-}" ]; then
  echo "${GHCR_TOKEN}" | docker login ghcr.io -u "${GHCR_USERNAME}" --password-stdin
else
  echo "GHCR_USERNAME/GHCR_TOKEN not set. Skipping docker login."
  echo "This is OK only if the GHCR package is public or already logged in."
fi

echo "[2/6] Pulling Docker image..."
docker pull "${IMAGE_REF}"

echo "[3/6] Stopping existing container..."
docker stop "${APP_NAME}" 2>/dev/null || true
docker rm "${APP_NAME}" 2>/dev/null || true

echo "[4/6] Starting new container..."
docker run -d \
  --name "${APP_NAME}" \
  --restart unless-stopped \
  --network host \
  --env-file .env \
  -v /var/log/autolift:/var/log/autolift \
  "${IMAGE_REF}"

echo "[5/6] Waiting for application to be healthy..."
for i in {1..30}; do
  if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "Application is healthy!"
    echo "[6/6] Deployment complete!"
    exit 0
  fi

  echo "Waiting... (${i}/30)"
  sleep 2
done

echo "ERROR: Application health check failed!"
docker logs "${APP_NAME}" || true
exit 1