# Microservices Demo Application for CI/CD Pipeline

This is a demo e-commerce microservices application showcasing DevSecOps practices with Jenkins CI/CD pipeline.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Load Balancer                            │
└─────────────────────────────────────────────────────────────────┘
                    ┌─────────┴─────────┐
                    │                   │
            ┌───────▼───────┐   ┌───────▼───────┐
            │  User Service │   │ Order Service │
            │    :8081      │   │    :8082      │
            └───────────────┘   └───────────────┘
                                        │
                                ┌───────▼───────┐
                                │Inventory Svc  │
                                │    :8083      │
                                └───────────────┘
```

## Services

1. **user-service** - Handles user authentication and management
2. **order-service** - Manages orders and checkout
3. **inventory-service** - Tracks product inventory

## Tech Stack

- Java 17 / Spring Boot
- Docker & Docker Compose
- Jenkins for CI/CD
- Bitbucket for Source Control
- Kubernetes for Orchestration
- SonarQube for Code Quality
- Trivy for Container Scanning
