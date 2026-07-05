# Apps Layout

This folder is the target application grouping for the repository.

Current source-of-truth application code remains in `microservices/` to keep existing build, CI/CD, and local scripts stable.

Planned consolidation path:
- Move `microservices/` into `apps/` only after all pipeline and automation references are updated.
- Validate Jenkins, GitHub Actions, Docker Compose, and Makefile paths end-to-end before switching.

For now, continue using:
- `microservices/user-service`
- `microservices/order-service`
- `microservices/inventory-service`
