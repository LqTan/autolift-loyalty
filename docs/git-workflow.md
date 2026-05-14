# Git Workflow

This project follows a simple Git workflow for personal development, academic demonstration, and AWS EC2 deployment.

## Main Branches

- `main`: Stable branch used for production/demo deployment.
- `develop`: Integration branch for completed features before merging to `main`.
- `feature/*`: Branches for new features or work items.
- `fix/*`: Branches for bug fixes.
- `docs/*`: Branches for documentation updates.
- `test/*`: Branches for adding or updating tests.
- `chore/*`: Branches for maintenance tasks.
- `build/*`: Branches for build configuration changes.
- `ci/*`: Branches for CI/CD configuration.

## Branch Naming

Use the Plane work item ID in the branch name.

Examples:

```text
feature/8-setup-postgresql-docker-compose
fix/12-fix-flyway-migration-error
docs/20-update-readme
test/21-add-modulith-verification-test
build/22-add-maven-dependencies
ci/23-add-github-actions-deploy