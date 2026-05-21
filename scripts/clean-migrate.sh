#!/bin/bash

set -a
source "$(dirname "$0")/../.env"
set +a

SCHEMA="${1:-all}"
cd "$(dirname "$0")/.."

if [ "$SCHEMA" = "all" ]; then
  echo "Cleaning and migrating ALL schemas..."
  export POSTGRES_HOST="${POSTGRES_HOST}"
  export POSTGRES_DB="${POSTGRES_DB}"
  export POSTGRES_USER="${POSTGRES_USER}"
  export POSTGRES_PASSWORD="${POSTGRES_PASSWORD}"
  ./mvnw flyway:clean flyway:migrate
else
  echo "Cleaning and migrating schema: $SCHEMA"
  docker exec autolift-loyalty-postgres-1 psql -U myuser -d mydatabase -c "DROP SCHEMA IF EXISTS ${SCHEMA} CASCADE;"
  export POSTGRES_HOST="${POSTGRES_HOST}"
  export POSTGRES_DB="${POSTGRES_DB}"
  export POSTGRES_USER="${POSTGRES_USER}"
  export POSTGRES_PASSWORD="${POSTGRES_PASSWORD}"
  ./mvnw flyway:migrate -Dschemas="${SCHEMA}"
fi