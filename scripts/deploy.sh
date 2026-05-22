#!/bin/bash
set -euo pipefail

ENVIRONMENT="${1:-}"
APP_IMAGE_REF="${2:-}"

APP_DIR="/opt/autolift"
APP_NAME="autolift-loyalty"

echo "=========================================="
echo "Deploying ${APP_NAME} to ${ENVIRONMENT}"
echo "App image ref: ${APP_IMAGE_REF}"
echo "=========================================="

if [ -z "${ENVIRONMENT}" ] || [ -z "${APP_IMAGE_REF}" ]; then
  echo "Usage: $0 production ghcr.io/lqtan/autolift-loyalty:<tag>"
  exit 1
fi

if [ "${ENVIRONMENT}" != "production" ]; then
  echo "Only production environment is supported."
  exit 1
fi

cd "${APP_DIR}"

echo "[0/7] Checking required files..."
if [ ! -f ".env" ]; then
  echo "ERROR: .env file not found in ${APP_DIR}"
  exit 1
fi
if [ ! -f "docker-compose.prod.yml" ]; then
  echo "ERROR: docker-compose.prod.yml not found in ${APP_DIR}"
  exit 1
fi

echo "[1/7] Updating docker-compose.prod.yml with image ref..."
python3 -c "
import yaml
with open('docker-compose.prod.yml', 'r') as f:
    compose = yaml.safe_load(f)
compose['services']['app']['image'] = '${APP_IMAGE_REF}'
if 'build' in compose['services']['app']:
    del compose['services']['app']['build']
with open('docker-compose.prod.yml', 'w') as f:
    yaml.dump(compose, f)
"

echo "[2/7] Login to GHCR if credentials are provided..."
if [ -n "${GHCR_USERNAME:-}" ] && [ -n "${GHCR_TOKEN:-}" ]; then
  echo "${GHCR_TOKEN}" | docker login ghcr.io -u "${GHCR_USERNAME}" --password-stdin
else
  echo "GHCR_USERNAME/GHCR_TOKEN not set. Skipping docker login."
  echo "This is OK only if the GHCR package is public or already logged in."
fi

echo "[3/7] Ensure ML data directories exist..."
mkdir -p ml/data ml/artifacts ml/scripts

echo "[4/7] Stopping existing containers..."
docker compose -f docker-compose.prod.yml down 2>/dev/null || true

echo "[5/7] Starting full stack with docker-compose..."
docker compose -f docker-compose.prod.yml up -d

echo "[6/7] Waiting for application to be healthy..."
for i in {1..30}; do
  if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "Application is healthy!"
    echo "[7/7] Deployment complete!"
    exit 0
  fi

  echo "Waiting... (${i}/30)"
  sleep 2
done

echo "ERROR: Application health check failed!"
docker compose -f docker-compose.prod.yml logs
exit 1