#!/bin/bash
cd "$(dirname "$0")/.." && ./mvnw flyway:clean flyway:migrate