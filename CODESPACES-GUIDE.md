# 🚀 GitHub Codespaces Quick Start Guide

## Welcome to the DevSecOps CI/CD Demo!

This guide will help you get started with the interactive CI/CD demo in GitHub Codespaces.

---

## ⚡ Quick Start (1 minute)

### Step 1: Start the Dashboard
```bash
make dashboard
```
The dashboard will be available at: `http://localhost:5000`

### Step 2: Start Services
In a new terminal:
```bash
make services-start
```

### Step 3: Trigger a Demo Build
From the dashboard, click **"Demo Build"** or run:
```bash
gh workflow run demo-build.yml
```

### Step 4: Watch Progress
```bash
gh run watch
```

---

## 📚 Available Commands

### 🎯 Quick Access
```bash
make help              # Show all available commands
make start-demo        # Start interactive demo menu
make status            # Show system status
make info              # Show project information
```

### 🏗️ Building & Testing
```bash
make build             # Build all microservices
make test              # Run all tests
make docker-build      # Build Docker images
make clean             # Clean build artifacts
```

### 🚀 Services Management
```bash
make services-start    # Start Docker Compose services
make services-stop     # Stop Docker Compose services
make services-ps       # Show running services
make services-logs     # Tail service logs
```

### 🔍 Demos & Scanning
```bash
make demo-build        # Show build demo
make demo-logs         # Show logs demo
make security-scan     # Trigger security scan
make trivy-scan        # Run Trivy scanner
```

### 🔗 GitHub Actions
```bash
make workflow-run      # List available workflows
make workflow-demo     # Trigger demo build
make workflow-watch    # Watch current workflow
make workflow-logs     # Show workflow logs
```

### 📊 Dashboard
```bash
make dashboard         # Start web dashboard (http://localhost:5000)
```

---

## 🎬 Demo Scenarios

### Scenario 1: See the Pipeline Structure
```bash
./scripts/demo.sh structure
```
Shows YAML workflow files and pipeline stages.

### Scenario 2: Build Microservices
```bash
./scripts/demo.sh build
```
Builds all 3 Java microservices locally.

### Scenario 3: Docker Build
```bash
./scripts/demo.sh docker
```
Creates Docker images for each service.

### Scenario 4: Security Scanning
```bash
./scripts/demo.sh security
```
Shows available security scanning capabilities.

### Scenario 5: Run All Demos
```bash
./scripts/demo.sh all
```
Runs all demos in sequence.

### Scenario 6: Interactive Menu
```bash
./scripts/demo.sh menu
```
Choose which demo to run interactively.

---

## 🌐 Accessing Forwarded Ports

GitHub Codespaces automatically forwards these ports:

| Service | Port | URL |
|---------|------|-----|
| User Service | 8081 | http://localhost:8081 |
| Order Service | 8082 | http://localhost:8082 |
| Inventory Service | 8083 | http://localhost:8083 |
| Nginx | 80 | http://localhost:80 |
| Prometheus | 9090 | http://localhost:9090 |
| Grafana | 3000 | http://localhost:3000 |
| Dashboard | 5000 | http://localhost:5000 |

**In VS Code:** Click the ports icon or use the **Ports** tab to see all forwarded ports.

---

## 📊 Web Dashboard

### Starting the Dashboard
```bash
make dashboard
```

### Dashboard Features
- **🌐 Microservices** - Real-time health status of all services
- **🔧 System Tools** - Availability of Docker, Java, Maven, Git
- **⚡ Quick Actions** - Trigger builds and scans
- **📊 Recent Workflows** - Last 10 GitHub Actions runs
- **🔄 Pipeline Flow** - Visual representation of CI/CD pipeline

### Dashboard Controls
- **Start/Stop Services** - Deploy or shutdown Docker Compose
- **Trigger Builds** - Run demo, full pipeline, or security scans
- **View Logs** - See real-time logs from services
- **Monitor Status** - Auto-refreshing service health checks

---

## 🏗️ Building Microservices

### Local Build
```bash
make build
```

### Build Individual Service
```bash
cd microservices/user-service
mvn clean package
cd ../../
```

### Build with Docker
```bash
make docker-build
```

### View Built Artifacts
```bash
docker images | grep -E "user-service|order-service|inventory-service"
```

---

## 🐳 Docker Compose

### Start All Services
```bash
make services-start
```

Services include:
- User Service (8081)
- Order Service (8082)
- Inventory Service (8083)
- Nginx Load Balancer (80)
- Prometheus (9090)
- Grafana (3000)

### Stop All Services
```bash
make services-stop
```

### View Running Services
```bash
make services-ps
```

### View Logs
```bash
make services-logs

# Or specific service
docker-compose logs -f user-service
```

---

## 🔐 Security & Scanning

### Run Security Scan
```bash
make security-scan
```

### Trivy Vulnerability Scan
```bash
make trivy-scan
```

### CodeQL Analysis
Automatically triggered on pull requests and pushes to main.

---

## 📈 GitHub Actions Workflows

### View Available Workflows
```bash
make workflow-run
```

### Trigger Demo Build
```bash
make workflow-demo
```

### Watch Running Workflow
```bash
make workflow-watch
```

### View Logs
```bash
make workflow-logs
```

---

## 🧪 Testing

### Run All Tests
```bash
make test
```

### Run Tests for Specific Service
```bash
cd microservices/order-service
mvn test
cd ../../
```

---

## 🛠️ Development Environment

### Environment Setup
```bash
make setup
```

This configures:
- Git configuration
- Helpful aliases (demo-build, services-start, etc.)
- Environment variables (.env file)
- Dependencies and utilities

### Useful Aliases (after setup)
```bash
demo-build              # Shortcut to: gh workflow run demo-build.yml
demo-status             # Shortcut to: gh run list --limit 5
demo-watch              # Shortcut to: gh run watch
demo-logs               # Shortcut to: gh run view --log
services-start          # Shortcut to: docker-compose up -d
services-stop           # Shortcut to: docker-compose down
services-logs           # Shortcut to: docker-compose logs -f
services-ps             # Shortcut to: docker-compose ps
```

---

## 📁 Project Structure

```
cicd-demo/
├── .devcontainer/
│   └── devcontainer.json          # Codespaces configuration
├── .github/
│   ├── workflows/
│   │   ├── ci-cd-pipeline.yml     # Main CI/CD pipeline
│   │   ├── security-scan.yml      # Security scanning
│   │   └── demo-build.yml         # Quick demo
│   ├── dependabot.yml             # Dependency scanning
│   └── branch-protection.md       # Branch protection config
├── microservices/
│   ├── user-service/              # Java Spring Boot service
│   ├── order-service/             # Java Spring Boot service
│   └── inventory-service/         # Java Spring Boot service
├── scripts/
│   ├── setup-codespaces.sh        # Codespaces setup
│   ├── demo.sh                    # Interactive demo script
│   ├── dashboard.py               # Web dashboard
│   └── templates/
│       └── dashboard.html         # Dashboard UI
├── docker/
│   ├── nginx.conf                 # Load balancer config
│   ├── prometheus.yml             # Monitoring config
│   └── dockerfile                 # Custom images
├── k8s/                           # Kubernetes manifests
├── Makefile                       # Quick commands
├── docker-compose.yml             # Local environment
└── README.md                      # Documentation
```

---

## 🔑 Key Features for Demo

### ✨ No Setup Required
- Automatic environment configuration
- Pre-installed tools (Docker, Java, Maven, git, gh CLI)
- All ports pre-forwarded

### 🎯 Interactive Demos
- 9 different demo scenarios
- Step-by-step walkthroughs
- Real-time output visualization

### 📊 Real-Time Dashboard
- Service health monitoring
- Workflow status tracking
- One-click actions
- Auto-refreshing updates

### 🚀 Continuous Integration
- Push-to-deploy pipeline
- Automated security scanning
- CodeQL SAST analysis
- Trivy vulnerability scanning

### 📱 Responsive Design
- Works on desktop and mobile
- Optimized for browser and terminal
- Accessible port forwarding

---

## 🐛 Troubleshooting

### Docker not working
```bash
# Restart Docker
docker ps
```

### Services won't start
```bash
# Check if ports are available
lsof -i :8081
# Kill process if needed
kill -9 <PID>
```

### Maven build fails
```bash
# Clear cache
make clean
# Retry
make build
```

### GitHub CLI not authenticated
```bash
# Login to GitHub
gh auth login
```

### Dashboard not loading
```bash
# Check Python requirements
pip install flask flask-cors requests
# Restart dashboard
make dashboard
```

---

## 📖 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GitHub Codespaces Documentation](https://docs.github.com/en/codespaces)
- [Docker Documentation](https://docs.docker.com)
- [Maven Documentation](https://maven.apache.org/guides)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

---

## 💡 Pro Tips

1. **Use the Makefile** - All common tasks have convenient make commands
2. **Check Status First** - Run `make status` to verify your environment
3. **Use Aliases** - After setup, use `demo-build` instead of full command
4. **Monitor Dashboard** - Open dashboard to watch real-time updates
5. **Read Logs** - Use `make services-logs` to debug issues
6. **Try All Demos** - Run `./scripts/demo.sh all` for complete walkthrough

---

## 🎓 Learning Path

1. Start with `make start-demo` for interactive introduction
2. Run `./scripts/demo.sh structure` to understand the pipeline
3. Try `make services-start` to deploy locally
4. Explore `make dashboard` for real-time monitoring
5. Trigger `make workflow-demo` to see GitHub Actions
6. Read the workflow files to understand the CICD implementation

---

## 🎉 Ready to Demo!

You're all set! Here's how to get started:

```bash
# Option 1: Quick Dashboard Demo
make dashboard

# Option 2: Interactive Menu
make start-demo

# Option 3: Deploy & Monitor
make services-start
make workflow-demo
make workflow-watch
```

---

**Happy DevOps-ing! 🚀**

For questions or issues, check the main [README.md](../README.md) or [GITHUB-CICD-UPDATES.md](../github/GITHUB-CICD-UPDATES.md).
