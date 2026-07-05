# DevSecOps CI/CD Pipeline - CDAC Lecture
## Presentation Slides (Markdown Format)

---

# Slide 1: Title

# 🚀 DevSecOps CI/CD Pipeline
## GitHub Actions + Docker + Jenkins

For Microservices Architecture

**CDAC - Advanced Computing**

Practical Demo & Implementation Guide

---

# Slide 2: Agenda

1. Introduction to DevSecOps & CI/CD
2. Microservices Architecture Overview
3. GitHub Actions Workflow
4. Jenkins Pipeline Setup
5. Docker Containerization
6. Security Scanning (DevSecOps)
7. Alerting & Notifications
8. Live Demo

---

# Slide 3: What is DevSecOps?

## 🔐 DevSecOps = Dev + Sec + Ops

Integrating Security at every stage of the pipeline

### Key Principles:
- ✅ **Shift-Left Security** - Security early in SDLC
- ✅ Automated Security Scanning
- ✅ Continuous Compliance
- ✅ Rapid Feedback Loops

---

# Slide 4: CI/CD Pipeline Flow

```
Code Commit → Build → Test → Security Scan → Docker Build → Deploy
(GitHub)      (Maven)  (JUnit) (Trivy)       (Docker)     (K8s)
```

### Pipeline Stages:
1. 📝 Code Commit (GitHub)
2. 🔨 Build (Maven)
3. 🧪 Unit Tests (JUnit)
4. 🔍 Code Quality (CodeQL)
5. 🔒 Security Scans (Trivy, OWASP)
6. 🐳 Docker Build (Containerize)
7. ☸️ Deploy (K8s/Docker)

---

# Slide 5: Microservices Architecture

```
┌────────────────────────────────────────┐
│           Load Balancer                │
└────────────────────────────────────────┘
      │              │              │
┌─────▼─────┐  ┌─────▼─────┐  ┌─────▼─────┐
│  User     │  │  Order    │  │ Inventory │
│ Service   │  │ Service   │  │ Service   │
│  :8081    │  │  :8082    │  │  :8083    │
└──────────┘  └───────────┘  └───────────┘
```

Each service: Independent Deployment, Scaling, Technology Stack

---

# Slide 6: GitHub Actions Overview

## 🐙 GitHub Actions - CI/CD Built-in

### Features:
- ✅ Free for public repos
- ✅ YAML-based workflows
- ✅ Marketplace for actions
- ✅ Native GitHub integration
- ✅ Matrix builds

### Workflows in this project:
```yaml
ci-cd-pipeline.yml   # Main CI/CD workflow
security-scan.yml     # Scheduled security scans  
demo-build.yml        # Quick demo build
```

---

# Slide 7: GitHub Actions Workflow

```yaml
name: DevSecOps CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
      - run: mvn clean package
      - run: mvn test
      
  security:
    runs-on: ubuntu-latest
    steps:
      - uses: aquasecurity/trivy-action@master
        with:
          scan-type: 'fs'
          severity: 'CRITICAL,HIGH'
```

---

# Slide 8: Jenkinsfile Structure

## ⚙️ Jenkins Alternative Pipeline

```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Build') {
            steps { sh 'mvn clean package' }
        }
        stage('Test') {
            steps { sh 'mvn test' }
        }
        stage('Security Scan') {
            steps { sh 'trivy fs --severity HIGH .' }
        }
        stage('Docker Build') {
            steps { sh 'docker build -t app:latest .' }
        }
        stage('Deploy') {
            steps { sh 'docker push' }
        }
    }
    
    post {
        failure {
            slackSend(message: "❌ Build Failed!")
            emailext(to: "devops@cdac.com")
        }
    }
}
```

---

# Slide 9: Docker Best Practices

## 🐳 Dockerfile Multi-stage Build

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage - minimal image
FROM eclipse-temurin:17-jre-alpine

# Security: Non-root user
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

# Copy artifact from builder
COPY --from=builder /app/target/*.jar app.jar
RUN chown -R appuser:appgroup /app
USER app

EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s CMD wget --spider http://localhost:8081/health

ENTRYPOINT ["java", "-jar", "-Xms256m", "-Xmx512m", "app.jar"]
```

---

# Slide 10: Docker Compose

```yaml
version: '3.8'

services:
  user-service:
    build: ./user-service
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    networks:
      - microservices

  order-service:
    build: ./order-service
    ports:
      - "8082:8082"
    depends_on:
      user-service:
        condition: service_healthy
    networks:
      - microservices

networks:
  microservices:
    driver: bridge
```

---

# Slide 11: GitHub Integration

## 🔗 GitHub Actions + Jenkins

### GitHub Webhook Events:
- `push` - Trigger on push to repository
- `pull_request` - Pull request events
- `workflow_dispatch` - Manual trigger

### Setup:
1. Go to Repository → Actions tab
2. Add GitHub Secrets
3. Push code → Pipeline runs automatically

### Secrets Configuration:
| Secret | Purpose |
|--------|---------|
| `DOCKER_USERNAME` | Docker Hub login |
| `DOCKER_TOKEN` | Docker Hub token |
| `SLACK_WEBHOOK_URL` | Slack notifications |

---

# Slide 12: DevSecOps Security Pipeline

## 🔒 Security Scanning Stages

### 1. SAST - Static Application Security Testing
```
GitHub CodeQL - Static code analysis
- Security queries
- Code scanning
```

### 2. SCA - Software Composition Analysis
```
Trivy / OWASP Dependency Check
- Vulnerable dependencies
- License issues
```

### 3. Container Scanning
```
Trivy - CVE Scanning for Docker Images
- Base image vulnerabilities
- Application packages
```

---

# Slide 13: Alerting & Notifications

## 🚨 Alert Configuration

### Notification Channels:
| Channel | Use Case | Priority |
|---------|----------|----------|
| Slack | Real-time team alerts | High |
| Email | Detailed reports | Medium |
| PagerDuty | Critical escalation | Critical |

### GitHub Actions Alert:
```yaml
- name: Send Slack Notification
  if: failure()
  uses: slackapi/slack-github-action@v1
  with:
    payload: |
      {
        "text": "❌ Build Failed: ${{ github.workflow }}"
      }
```

---

# Slide 14: Alert Thresholds

```yaml
ALERT_THRESHOLDS:
  BUILD_FAILURE_THRESHOLD: 3        # Alert after 3 consecutive failures
  BUILD_DURATION_WARNING: 30m       # Warn if build exceeds 30 minutes
  BUILD_DURATION_CRITICAL: 1h        # Critical alert after 1 hour

  # Security Alerts
  CRITICAL_VULNERABILITIES: 0       # Fail on any critical CVEs
  HIGH_VULNERABILITIES: 5            # Warn on 5+ high severity CVEs
  MEDIUM_VULNERABILITIES: 20        # Warn on 20+ medium severity CVEs

ESCALATION_RULES:
  level_1: Developers (immediate)
  level_2: DevOps Lead (15 min delay)
  level_3: On-Call Engineer (30 min delay)
```

---

# Slide 15: Project Structure

```
cicd-demo/
├── .github/
│   └── workflows/              # GitHub Actions
│       ├── ci-cd-pipeline.yml
│       ├── security-scan.yml
│       └── demo-build.yml
├── microservices/
│   ├── user-service/          # Java Spring Boot
│   │   ├── src/main/java/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   ├── order-service/
│   └── inventory-service/
├── jenkins/
│   └── Jenkinsfile             # Jenkins pipeline
├── docker/
│   ├── docker-compose.yml
│   └── nginx.conf
├── alerts/                      # Notification configs
└── README.md
```

---

# Slide 16: Live Demo

## 🎬 Demo Flow

1. **Push code to GitHub** → Trigger pipeline
2. **GitHub Actions runs** → Pipeline starts
3. **Pipeline executes stages** → Build, Test, Scan
4. **Docker images built** → Containerization
5. **Security scans run** → Trivy
6. **Deploy to staging** → Kubernetes/Docker
7. **Alert on success/failure** → Slack/Email

### Commands:
```bash
# Push to trigger
git add . && git commit -m "demo" && git push

# Manual trigger
gh workflow run demo-build.yml

# View status
gh run status
```

---

# Slide 17: Key Commands

```bash
# GitHub CLI
gh workflow list
gh run watch
gh run download

# Docker Commands
docker build -t user-service:latest ./user-service
docker-compose up -d
docker-compose logs -f

# Security Scanning
trivy image user-service:latest
trivy fs --severity HIGH,CRITICAL ./project

# Kubernetes
kubectl apply -f deployment.yaml
kubectl get pods
```

---

# Slide 18: Benefits

## ✨ CI/CD Pipeline Benefits

| Development | Operations | Security |
|-------------|------------|----------|
| Faster iterations | Reduced deployment time | Automated security scans |
| Early bug detection | Zero-downtime deploys | Compliance monitoring |
| Consistent builds | Easy rollback | Vulnerability detection |
| Automated testing | Infrastructure as code | Secure by default |

---

# Slide 19: Key Takeaways

## 🎯 Remember

1. **DevSecOps** = Development + Security + Operations integrated
2. **GitHub Actions** = Free, built-in CI/CD for GitHub repos
3. **Jenkins** = Alternative for enterprise CI/CD
4. **Docker** = Consistent microservice deployment
5. **Security** = Must be integrated throughout the pipeline
6. **Alerting** = Ensures rapid response to failures

---

# Slide 20: Resources

## 📚 Learning Resources

### Documentation:
- docs.github.com/actions - GitHub Actions
- docs.docker.com - Docker Best Practices
- jenkins.io - Jenkins Documentation
- aquasecurity.github.io/trivy - Trivy Scanner

### Practice:
- Demo Repository: github.com/anandpatil/cicd-demo
- Hands-on Labs
- Assignment: Build your own CI/CD pipeline

---

# Slide 21: Thank You

## 🙏 Questions?

**Contact:**
- 📧 Email: devops@cdac.com
- 💬 Slack: #cdac-devsecops
- 📦 Repo: github.com/anandpatil/cicd-demo

**Happy Learning! 🚀**
