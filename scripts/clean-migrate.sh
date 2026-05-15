#!/bin/bash
set -a
source "$(dirname "$0")/../.env"
set +a
cd "$(dirname "$0")/.." && ./mvnw \
  -DPOSTGRES_HOST="${POSTGRES_HOST}" \
  -DPOSTGRES_DB="${POSTGRES_DB}" \
  -DPOSTGRES_USER="${POSTGRES_USER}" \
  -DPOSTGRES_PASSWORD="${POSTGRES_PASSWORD}" \
  flyway:clean flyway:migrate