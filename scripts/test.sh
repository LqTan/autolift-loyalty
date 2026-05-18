#!/bin/bash
cd "$(dirname "$0")/.."
if [ "$1" = "-q" ]; then
  ./mvnw test 2>&1 | grep "Tests run"
else
  ./mvnw test
fi