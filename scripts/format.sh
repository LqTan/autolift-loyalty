#!/bin/bash
cd "$(dirname "$0")/.."

case "$1" in
  check)
    ./mvnw spotless:check
    ;;
  fix)
    ./mvnw spotless:apply
    ;;
  "")
    echo "Usage: format.sh [check|fix]"
    echo "  check - Check code format violations"
    echo "  fix   - Auto-fix code format"
    ;;
  *)
    echo "Unknown option: $1"
    echo "Usage: format.sh [check|fix]"
    exit 1
    ;;
esac