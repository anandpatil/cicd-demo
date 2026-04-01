# DevSecOps CI/CD Demo - README

<div align="center">

![DevSecOps](https://img.shields.io/badge/DevSecOps-CI%2FCD-blue)
![Jenkins](https://img.shields.io/badge/Jenkins-Pipeline-red)
![Docker](https://img.shields.io/badge/Docker-Container-blue)
![GitHub Actions](https://img.shields.io/badge/GitHub-Actions-green)
![Java](https://img.shields.io/badge/Java-17-orange)

**A complete DevSecOps CI/CD pipeline demo for microservices**

[📋 Documentation](docs/CDAC-DevSecOps-Slides.md) | [🎬 Live Demo](#-quick-start) | [🐳 Docker Setup](#docker) | [⚙️ GitHub Actions](#github-actions)

</div>

---

## 🎯 Overview

This is a comprehensive **DevSecOps CI/CD Pipeline** demo project showcasing:

- **3 Microservices**: User, Order, and Inventory services
- **GitHub Actions**: Full CI/CD workflow with security scanning
- **Jenkins Pipeline**: Alternative Jenkins-based pipeline
- **Docker**: Containerization with multi-stage builds
- **Kubernetes**: Production-ready manifests with HPA
- **DevSecOps**: SAST, SCA, and Container scanning

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        GitHub                               │
│              (Source Control & CI/CD)                       │
└─────────────────────────┬───────────────────────────────────┘
                          │
                    ┌─────▼─────┐
                    │  GitHub   │
                    │  Actions  │
                    └─────┬─────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
   ┌────▼────┐      ┌─────▼─────┐     ┌─────▼─────┐
   │  Build  │      │  Security │     │  Deploy   │
   │ Maven   │      │  Scan     │     │ Staging   │
   └────┬────┘      └─────┬─────┘     └─────┬─────┘
        │                 │                 │
   ┌────▼─────────────────▼─────────────────▼────┐
   │           Docker Images                       │
   │    user-service | order-service | inventory  │
   └────────────────────┬────────────────────────┘
                        │
                   ┌────▼────┐
                   │ Deploy  │
                   │ K8s/Docker│
                   └─────────┘
```

## 🚀 Quick Start

### Option 1: GitHub Codespaces (🌟 Recommended - No Setup!)

```bash
# Click "Code" → "Codespaces" → "Create codespace on main"
# Wait for environment to load (~2 minutes)

# Then run:
make dashboard
# Open http://localhost:5000 in browser

# Or try interactive demo:
make start-demo
```

**[📖 Full Codespaces Guide →](CODESPACES-GUIDE.md)**

### Option 2: GitHub Actions (Watch Pipeline)

```bash
# 1. Fork/Clone this repository
git clone https://github.com/anandpatil/cicd-demo.git
cd cicd-demo

# 2. Push to your GitHub repo
git remote add origin https://github.com/anandpatil/cicd-demo.git
git push -u origin main

# 3. Watch the pipeline run!
# Go to: https://github.com/anandpatil/cicd-demo/actions
```

### Option 3: Local Docker Compose

```bash
# Start all services
docker-compose up -d

# Check services
curl http://localhost:8081/api/users/health
curl http://localhost:8082/api/orders/health
curl http://localhost:8083/api/inventory/health

# View logs
docker-compose logs -f
```

## 📁 Project Structure

```
cicd-demo/
├── .github/
│   └── workflows/           # GitHub Actions CI/CD
│       ├── ci-cd-pipeline.yml
│       ├── security-scan.yml
│       └── demo-build.yml
├── microservices/
│   ├── user-service/        # User management API (8081)
│   ├── order-service/       # Order processing API (8082)
│   └── inventory-service/   # Inventory tracking API (8083)
├── jenkins/
│   └── Jenkinsfile          # Jenkins pipeline script
├── docker/
│   ├── docker-compose.yml   # Local development
│   ├── nginx.conf           # Load balancer
│   └── prometheus.yml       # Monitoring
├── k8s/                     # Kubernetes manifests
├── alerts/                  # Notification configs
├── docs/                    # Documentation & slides
└── README.md
```

## 🔄 CI/CD Pipeline Stages

| Stage | Description | Tool |
|-------|-------------|------|
| 🔍 **Checkout** | Clone code from GitHub | Git |
| 🔨 **Build** | Compile Java microservices | Maven |
| 🧪 **Test** | Run unit tests | JUnit |
| 🔒 **SAST** | Static code analysis | SonarQube/CodeQL |
| 📦 **SCA** | Dependency scanning | OWASP, Trivy |
| 🐳 **Docker Build** | Build container images | Docker |
| 🔐 **Container Scan** | Vulnerability scanning | Trivy |
| ☸️ **Deploy Staging** | Deploy to staging | Kubernetes |
| 💨 **Smoke Tests** | Verify deployment | curl |
| 🚀 **Deploy Prod** | Deploy to production | Kubernetes |

## 🔔 Alerting

### Slack Notification (on failure)
```
🚨 Build #123 FAILED!
Branch: main
Failed Stage: Test
[View Logs] [View GitHub]
```

### Email Alert (on failure)
- Subject: "🚨 CI/CD Build #123 - FAILED"
- Body: Build details, logs attachment, action items

## 🧪 Demo Scenarios

### Scenario 1: Normal Build (Success)
```bash
# Push a normal commit
git commit --allow-empty -m "Trigger build"
git push
```
**Expected**: All stages pass → Slack success notification

### Scenario 2: Failed Build (Security Alert)
```bash
# Add a file with secrets
echo "API_KEY=secret123" > app.properties
git add .
git commit -m "Add config"
git push
```
**Expected**: Build fails at secret detection → Slack alert

### Scenario 3: Failed Tests
```bash
# Break a test
# Edit microservices/user-service/src/test/...
git add . && git commit -m "Break tests" && git push
```
**Expected**: Build fails at test stage → Alert sent

### Scenario 4: Manual Trigger
```bash
# Trigger demo build manually
gh workflow run demo-build.yml
```

## 📊 Monitoring

| Service | URL | Purpose |
|---------|-----|---------|
| User Service | http://localhost:8081 | User management |
| Order Service | http://localhost:8082 | Order processing |
| Inventory | http://localhost:8083 | Inventory tracking |
| Nginx | http://localhost:80 | Load balancer |
| Prometheus | http://localhost:9090 | Metrics |
| Grafana | http://localhost:3000 | Dashboards |

## 🔧 Configuration

### GitHub Secrets Required

| Secret | Description |
|--------|-------------|
| `DOCKER_USERNAME` | Docker Hub username |
| `DOCKER_TOKEN` | Docker Hub access token |
| `SLACK_WEBHOOK_URL` | Slack webhook for alerts |
| `EMAIL_USERNAME` | SMTP email username |
| `EMAIL_PASSWORD` | SMTP app password |
| `ALERT_EMAIL` | Alert recipient email |

### Environment Variables

```yaml
DOCKER_REGISTRY: docker.io
DOCKER_ORG: cdacdemo
JAVA_VERSION: 17
SPRING_PROFILES_ACTIVE: production
```

## 📚 Documentation

- [🌐 Quick Start - GitHub Codespaces](CODESPACES-GUIDE.md)
- [📊 Presentation Slides](docs/CDAC-DevSecOps-Slides.md)
- [🌐 HTML Presentation](docs/CDAC-DevSecOps-Presentation.html) (open in browser)
- [🔧 Setup Guide](SETUP.md)
- [🔧 GitHub CICD Updates](github/GITHUB-CICD-UPDATES.md)
- [🐳 Docker Guide](docker-compose.yml)
- [☸️ Kubernetes Guide](k8s/)
- [🐙 GitHub Setup](github/SETUP.md)
- [⚙️ Makefile Commands](Makefile)

## 🎓 For CDAC Lecture

### Using GitHub Codespaces (Recommended)
1. Click "Code" button → "Codespaces" → "Create codespace on main"
2. Wait for environment setup (~2 minutes)
3. Run: `make dashboard`
4. Open http://localhost:5000 to show live demo
5. Trigger builds and show real-time monitoring
6. Open GitHub Actions tab to show workflow runs

### Using Local Setup
1. Clone repository: `git clone https://github.com/anandpatil/cicd-demo.git`
2. Run: `make services-start`
3. Open dashboard: `make dashboard`
4. Run demo script: `make start-demo`

### Using GitHub Actions Tab
1. Open Actions tab in GitHub repository
2. Trigger demo: Click "Build Demo" workflow
3. Watch pipeline execute in real-time
4. Show logs and artifacts

**[📖 Presentation Guide →](docs/CDAC-DevSecOps-Slides.md)**

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Push and create PR
5. Pipeline runs automatically

## 📄 License

MIT License - Educational use encouraged!

---

<div align="center">

**Built for DevSecOps Demo **

</div>
