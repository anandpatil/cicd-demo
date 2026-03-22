# DevSecOps CI/CD Pipeline - CDAC Lecture
## Presentation Slides (Markdown Format)

---

# Slide 1: Title

# 🚀 DevSecOps CI/CD Pipeline
## Jenkins + Docker + Bitbucket

For Microservices Architecture

**CDAC - Advanced Computing**

Practical Demo & Implementation Guide

---

# Slide 2: Agenda

1. Introduction to DevSecOps & CI/CD
2. Microservices Architecture Overview
3. Jenkins Pipeline Setup
4. Docker Containerization
5. Bitbucket Integration
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
(Bitbucket)  (Maven)  (JUnit) (SonarQube)    (Docker)     (K8s)
```

### Pipeline Stages:
1. 📝 Code Commit (Bitbucket)
2. 🔨 Build (Maven/Gradle)
3. 🧪 Unit Tests (JUnit)
4. 🔍 Code Quality (SonarQube)
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

# Slide 6: Jenkins Overview

## ⚙️ Jenkins - The Automation Server

### Features:
- Open Source
- Plugin Ecosystem (1800+)
- Pipeline as Code
- Distributed Builds
- Blue/Green Deployments

### Key Plugins:
- `Pipeline` - DSL for pipelines
- `Docker Pipeline`
- `Slack Notification`
- `Email Extension`
- `SonarQube Scanner`

---

# Slide 7: Jenkinsfile Structure

```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Security Scan') {
            steps {
                sh 'trivy image --severity HIGH,CRITICAL app:latest'
            }
        }
        stage('Deploy') {
            steps {
                sh 'docker build && docker push'
            }
        }
    }
    
    post {
        failure {
            slackSend(message: "Build Failed!")
            emailext(to: "devops@cdac.com", subject: "Build Failed")
        }
        success {
            slackSend(message: "✅ Build Passed")
        }
    }
}
```

---

# Slide 8: Docker Best Practices

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

# Slide 9: Docker Compose

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

# Slide 10: Bitbucket Integration

## 🔗 Webhook Configuration

### Bitbucket Webhook Events:
- `repo:push` - Trigger on push to repository
- `pullrequest:created` - New pull request
- `pullrequest:merged` - PR merged
- `issue:created` - New issue

### Jenkins Configuration:
1. Install "Bitbucket Branch Source" Plugin
2. Create Multibranch Pipeline
3. Configure:
   - Source: Bitbucket Cloud
   - Owner: workspace-name
   - Repository: cicd-microservices
   - Credentials: Bitbucket App Password

---

# Slide 11: DevSecOps Security Pipeline

## 🔒 Security Scanning Stages

### 1. SAST - Static Application Security Testing
```
SonarQube - Code Quality & Security Hotspots
- Checkmarx
- Fortify
```

### 2. SCA - Software Composition Analysis
```
OWASP Dependency Check - Vulnerable Dependencies
- Snyk
- Black Duck
```

### 3. Container Scanning
```
Trivy - CVE Scanning for Docker Images
- Clair
- Anchore
```

---

# Slide 12: Alerting & Notifications

## 🚨 Alert Configuration

### Notification Channels:
| Channel | Use Case | Priority |
|---------|----------|----------|
| Slack | Real-time team alerts | High |
| Email | Detailed reports | Medium |
| PagerDuty | Critical escalation | Critical |
| Teams | Microsoft integration | Medium |

### Jenkins Post Block:
```groovy
post {
    success {
        slackSend(channel: "#cicd-alerts", 
                  color: "good", 
                  message: "✅ Build #${BUILD_NUMBER} Success")
    }
    failure {
        slackSend(channel: "#cicd-alerts",
                  color: "danger",
                  message: "❌ Build #${BUILD_NUMBER} FAILED!")
        emailext(to: "devops@cdac.com",
                subject: "ALERT: Build Failed",
                body: "Check logs immediately")
    }
}
```

---

# Slide 13: Alert Thresholds

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

# Slide 14: Project Structure

```
cicd-demo/
├── microservices/
│   ├── user-service/       # Java Spring Boot
│   │   ├── src/main/java/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── application.properties
│   ├── order-service/
│   └── inventory-service/
├── jenkins/
│   └── Jenkinsfile         # Main pipeline
├── docker/
│   ├── docker-compose.yml   # Local dev environment
│   ├── nginx.conf          # Reverse proxy
│   └── prometheus.yml      # Monitoring config
├── alerts/                  # Notification configurations
│   ├── alert-config.yaml
│   ├── send-alert.sh
│   └── slack-notification.groovy
├── bitbucket/              # Webhook handlers
└── README.md
```

---

# Slide 15: Live Demo

## 🎬 Demo Flow

1. **Push code to Bitbucket** → Trigger pipeline
2. **Webhook notifies Jenkins** → Pipeline starts
3. **Pipeline executes stages** → Build, Test, Scan
4. **Docker images built** → Containerization
5. **Security scans run** → Trivy, SonarQube
6. **Deploy to staging** → Kubernetes/Docker
7. **Alert on success/failure** → Slack/Email

### Commands:
```bash
# Start environment
docker-compose up -d

# View logs
docker-compose logs -f

# Run pipeline
curl -X POST JENKINS_URL/job/pipeline/build
```

---

# Slide 16: Key Commands

```bash
# Docker Commands
docker build -t user-service:latest ./user-service
docker-compose up -d
docker-compose logs -f user-service
docker inspect --format='{{.State.Health.Status}}' user-service

# Security Scanning
trivy image user-service:latest
trivy fs --severity HIGH,CRITICAL ./project

# Jenkins
jenkins-cli.jar build job-name -s
curl -X POST JENKINS_URL/job/pipeline/build

# Kubernetes
kubectl apply -f deployment.yaml
kubectl rollout status deployment/user-service
kubectl get pods -l app=user-service
```

---

# Slide 17: Benefits

## ✨ CI/CD Pipeline Benefits

| Development | Operations | Security |
|-------------|------------|----------|
| Faster iterations | Reduced deployment time | Automated security scans |
| Early bug detection | Zero-downtime deploys | Compliance monitoring |
| Consistent builds | Easy rollback | Vulnerability detection |
| Automated testing | Infrastructure as code | Secure by default |

---

# Slide 18: Key Takeaways

## 🎯 Remember

1. **DevSecOps** = Development + Security + Operations integrated
2. **Jenkins Pipeline** as Code for version-controlled CI/CD
3. **Docker** enables consistent microservice deployment
4. **Security** must be integrated throughout the pipeline
5. **Alerting** ensures rapid response to failures
6. **Automation** reduces human error and speeds delivery

---

# Slide 19: Resources

## 📚 Learning Resources

### Documentation:
- Jenkins.io - Pipeline Documentation
- Docker Docs - Best Practices
- Bitbucket API - Webhooks
- SonarQube - Code Analysis
- Trivy - Container Scanning

### Practice:
- Demo Repository: bitbucket.org/cdac-demo/cicd-demo
- Hands-on Labs
- Assignment: Build your own CI/CD pipeline

---

# Slide 20: Thank You

## 🙏 Questions?

**Contact:**
- 📧 Email: devops@cdac.com
- 💬 Slack: #cdac-devsecops
- 📦 Repo: bitbucket.org/cdac-demo/cicd-microservices

**Happy Learning! 🚀**
