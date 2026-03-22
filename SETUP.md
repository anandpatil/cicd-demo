# DevSecOps CI/CD Demo - Setup Guide

## Overview
This is a complete CI/CD pipeline demo for microservices using Jenkins, Docker, and Bitbucket.

## Prerequisites
- Docker & Docker Compose
- Jenkins 2.x or higher
- Java 17+
- Maven 3.9+
- Git

## Quick Start

### 1. Clone the Repository
```bash
git clone https://bitbucket.org/cdac-demo/cicd-microservices.git
cd cicd-microservices
```

### 2. Start Local Environment
```bash
docker-compose up -d
```

This starts:
- User Service (port 8081)
- Order Service (port 8082)
- Inventory Service (port 8083)
- Nginx Proxy (port 80)
- Prometheus (port 9090)
- Grafana (port 3000)

### 3. Verify Services
```bash
# Check health
curl http://localhost:8081/api/users/health
curl http://localhost:8082/api/orders/health
curl http://localhost:8083/api/inventory/health
```

## Jenkins Setup

### 1. Install Jenkins
```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts
```

### 2. Install Required Plugins
- Pipeline
- Docker Pipeline
- Slack Notification
- Email Extension
- SonarQube Scanner
- Bitbucket Branch Source

### 3. Configure Jenkins Credentials
1. Go to Jenkins > Manage Jenkins > Manage Credentials
2. Add:
   - Docker Hub credentials
   - Bitbucket App Password
   - Slack Webhook URL
   - SonarQube Token

### 4. Create Pipeline Job
1. New Item > Pipeline
2. Select "Pipeline script from SCM"
3. Configure Bitbucket repository
4. Add Jenkinsfile path

## Bitbucket Webhook Setup

### 1. Go to Repository Settings > Webhooks
### 2. Add Webhook:
- Title: Jenkins CI/CD Pipeline
- URL: `https://your-jenkins-url/bitbucket-hook/`
- Triggers:
  - Repository > Push
  - Pull Request > Created
  - Pull Request > Updated

## Security Scanning

### Trivy Installation
```bash
# Linux
curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh

# Docker
docker run aquasec/trivy:latest image your-image:latest
```

### SonarQube Setup
```bash
docker run -d --name sonarqube \
  -p 9000:9000 \
  sonarqube:latest
```

## Alert Configuration

### Slack Webhook
1. Create Slack App > Incoming Webhooks
2. Add webhook URL to Jenkins credentials
3. Configure channel in Jenkinsfile

### Email Configuration
1. Jenkins > Manage Jenkins > Configure System
2. Configure SMTP server
3. Set up Extended Email Notification

## Pipeline Stages

| Stage | Description | Tools |
|-------|-------------|-------|
| Checkout | Clone code from Bitbucket | Git |
| Build | Compile and package | Maven |
| Test | Run unit tests | JUnit, Maven |
| Security Scan | Code & container scanning | SonarQube, Trivy |
| Docker Build | Create container images | Docker |
| Deploy | Deploy to environment | Kubernetes/Docker |

## Troubleshooting

### Build Fails
```bash
# Check logs
docker-compose logs -f

# Rebuild without cache
docker-compose build --no-cache
```

### Container Not Starting
```bash
# Check container status
docker ps -a

# View logs
docker logs container-name

# Restart service
docker-compose restart service-name
```

## Project Structure
```
cicd-demo/
├── microservices/
│   ├── user-service/
│   ├── order-service/
│   └── inventory-service/
├── jenkins/
│   └── Jenkinsfile
├── docker/
│   ├── docker-compose.yml
│   ├── nginx.conf
│   └── prometheus.yml
├── alerts/
│   ├── alert-config.yaml
│   └── send-alert.sh
└── docs/
    ├── CDAC-DevSecOps-Presentation.html
    └── CDAC-DevSecOps-Slides.md
```

## License
This is a demo project for educational purposes.
