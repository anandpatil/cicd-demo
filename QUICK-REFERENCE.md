# 🎯 GitHub Codespaces Demo - Quick Reference

## ⚡ One-Minute Quick Start

```bash
# In Codespaces terminal:
make dashboard

# Open browser: http://localhost:5000
```

---

## 🌐 Service URLs (After `make services-start`)

### Microservices
- **User Service:** http://localhost:8081
  - Health: http://localhost:8081/health
  - API: http://localhost:8081/api/users

- **Order Service:** http://localhost:8082
  - Health: http://localhost:8082/health
  - API: http://localhost:8082/api/orders

- **Inventory Service:** http://localhost:8083
  - Health: http://localhost:8083/health
  - API: http://localhost:8083/api/inventory

### Infrastructure
- **Nginx Load Balancer:** http://localhost:80
- **Prometheus Metrics:** http://localhost:9090
- **Grafana Dashboards:** http://localhost:3000 (admin/admin)

### Demo
- **Web Dashboard:** http://localhost:5000 (Primary UI)
- **Dashboard API:** http://localhost:5000/api

---

## 📋 Make Commands Quick Reference

```bash
# 🎬 Demo (Pick one)
make start-demo              # Interactive menu (9 scenarios)
make demo-build              # Show build demo
make dashboard               # Web dashboard

# 🚀 Services
make services-start          # Start Docker Compose
make services-stop           # Stop Docker Compose
make services-ps             # Show running services
make services-logs           # Tail logs

# 🔨 Building
make build                   # Build all microservices
make test                    # Run all tests
make docker-build            # Build Docker images
make clean                   # Clean artifacts

# 🔐 Security
make security-scan           # Trigger security scan
make trivy-scan              # Run Trivy scanner

# 📊 GitHub Actions
make workflow-run            # List workflows
make workflow-demo           # Trigger demo build
make workflow-watch          # Watch current run
make workflow-logs           # Show logs

# ℹ️ Info
make status                  # System status
make info                    # Project info
make help                    # Show all commands
```

---

## 🎬 Interactive Demo Scenarios

```bash
./scripts/demo.sh menu                # Interactive menu
./scripts/demo.sh structure           # 1. Pipeline structure
./scripts/demo.sh build               # 2. Build microservices
./scripts/demo.sh docker              # 3. Docker build
./scripts/demo.sh security            # 4. Security scanning
./scripts/demo.sh workflows           # 5. GitHub workflows
./scripts/demo.sh trigger             # 6. Trigger build
./scripts/demo.sh deploy              # 7. Deploy services
./scripts/demo.sh logs                # 8. View logs
./scripts/demo.sh all                 # 9. Run all demos
```

---

## 🎨 Dashboard Features

### On `http://localhost:5000`

**Left Column:**
- 🌐 Microservices - Health status (healthy/degraded/offline)
- 🔧 System Tools - Docker, Java, Maven availability
- ⚡ Quick Actions - Buttons to trigger builds

**Right Column:**
- 📊 Recent Workflows - Last 10 GitHub Actions runs
- 🔄 Pipeline Flow - Visual representation

**Auto-Updates:** Every 5 seconds

---

## 🔍 Health Check Commands

```bash
# Check individual services
curl http://localhost:8081/health
curl http://localhost:8082/health
curl http://localhost:8083/health

# Check all services (bash script)
for port in 8081 8082 8083; do
  echo "Port $port: $(curl -s -o /dev/null -w '%{http_code}' http://localhost:$port/health)"
done
```

---

## 📖 Documentation Files

| File | Purpose | Size |
|------|---------|------|
| CODESPACES-GUIDE.md | Complete guide with examples | 469 lines |
| CODESPACES-DEMO-READY.md | Feature summary | 454 lines |
| README.md | Main project readme | Updated |
| Makefile | Quick commands | 193 lines |
| .devcontainer/devcontainer.json | Codespaces config | 104 lines |
| scripts/demo.sh | Interactive demos | 414 lines |
| scripts/setup-codespaces.sh | Auto setup | 218 lines |
| scripts/dashboard.py | Flask backend | 238 lines |
| scripts/templates/dashboard.html | Dashboard UI | 525 lines |

---

## 🎯 Demo Scenarios (Recommended Order)

### For 5-Minute Demo
```bash
make dashboard
# Show service status
# Trigger "Demo Build"
# Watch workflow complete
```

### For 10-Minute Demo
```bash
./scripts/demo.sh menu
# Select: 1. Pipeline Structure
# Select: 3. Docker Build
# Select: 2. Build Microservices
# Select: 6. Trigger GitHub Action
```

### For 15-Minute Demo
```bash
make services-start
sleep 5
make dashboard
# In another terminal:
./scripts/demo.sh all
```

### For Lecture
```bash
# Terminal 1: Dashboard
make dashboard

# Show in browser: http://localhost:5000

# Terminal 2: Services
make services-start

# Terminal 3: Demos
./scripts/demo.sh menu
```

---

## 🚨 Troubleshooting Commands

```bash
# Check if containers are running
docker-compose ps

# View container logs
docker-compose logs -f

# Restart services
docker-compose restart

# Check specific service
docker-compose logs user-service

# Remove and restart
docker-compose down
docker-compose up -d

# Check system status
make status

# Verify all tools installed
make setup

# Check which processes use ports
lsof -i :5000
lsof -i :8081
```

---

## 💾 File Locations

```
In Codespaces terminal:

cd /workspaces/cicd-demo

Key directories:
  .devcontainer/          # Codespaces config
  .github/workflows/      # GitHub Actions
  microservices/          # Source code
  scripts/                # Demo scripts
  docker/                 # Docker configs
  k8s/                    # Kubernetes configs
  docs/                   # Documentation
```

---

## 🔗 Important URLs

### GitHub
- Repository: https://github.com/your-org/cicd-demo
- Actions Tab: https://github.com/your-org/cicd-demo/actions
- Create Codespace: https://github.com/your-org/cicd-demo/codespaces

### Local (In Codespaces)
- Dashboard: http://localhost:5000
- User Service: http://localhost:8081
- Order Service: http://localhost:8082
- Inventory Service: http://localhost:8083
- Grafana: http://localhost:3000
- Prometheus: http://localhost:9090

---

## ⚙️ Environment Variables (.env)

```bash
# Auto-created by setup script
CODESPACES=true
CODESPACE_NAME=cicd-demo
USER_SERVICE_PORT=8081
ORDER_SERVICE_PORT=8082
INVENTORY_SERVICE_PORT=8083
DOCKER_REGISTRY=docker.io
DOCKER_ORG=cdacdemo
JAVA_VERSION=17
SPRING_PROFILES_ACTIVE=demo
DATABASE_URL=jdbc:h2:mem:testdb
DATABASE_USER=sa
DEMO_MODE=true
ENABLE_MOCK_DATA=true
```

---

## 📱 For Mobile/Tablet

The dashboard is responsive! Open http://localhost:5000 on any device.

---

## 🎓 Teaching Tips

1. **Start with Dashboard** - Visual impact
2. **Trigger a Build** - Shows CI/CD in action
3. **Show Logs** - Real-time pipeline execution
4. **Deploy Services** - Interactive experience
5. **Explain Code** - Open GitHub Actions tab
6. **Q&A** - Stop and answer questions

---

## ✨ Pro Tips

1. **Faster Demos:** Pre-start services with `make services-start`
2. **Multiple Terminals:** Use multiple terminal tabs in VS Code
3. **Keyboard Shortcuts:** `Ctrl+Shift+` grave for terminal
4. **Port Access:** Click Ports tab in VS Code to see URLs
5. **Share:** Share Codespace URL with "Share to Web"

---

## 🆘 Getting Help

- Read: CODESPACES-GUIDE.md
- Check: Makefile for all commands
- View: github/GITHUB-CICD-UPDATES.md for CICD details
- Run: `make help` for command list
- Try: `./scripts/demo.sh menu` for guided demo

---

**Last Updated:** March 22, 2026  
**Status:** Ready for Production  
**Maintenance:** Update as needed  

🚀 **Ready to Demo!**
