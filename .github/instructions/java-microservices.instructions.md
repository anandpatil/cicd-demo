---
description: "Use when editing Spring Boot microservices, Maven pom.xml files, Java controllers/services/models, or service-level tests in this repo. Covers Java 17, Maven verification, and preserving user-service quality gates."
applyTo: microservices/**/pom.xml,microservices/**/src/**
---

# Java Microservice Guidance

- Preserve Java 17 compatibility and existing Spring Boot conventions used by the services.
- Favor small service-local changes over cross-service abstractions unless duplication is clearly harmful.
- Keep health endpoints, service ports, and manifest expectations consistent with Kubernetes and workflow configuration.
- When changing `user-service`, preserve test execution under `mvn clean verify` and do not weaken existing JaCoCo or quality-gate checks unless explicitly requested.
- Add tests for behavior changes when the service already has a testable seam.
