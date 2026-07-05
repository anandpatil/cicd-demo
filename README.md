# CI/CD Demo - Monorepo

A comprehensive CI/CD demo project with **microservices architecture**, **hybrid CI/CD pipelines** (GitHub Actions + Jenkins), and **separation of concerns** in a monorepo structure.

## 🎯 Project Structure

```
cicd-demo/
├── services/                    # Microservices
│   ├── service-a/              # First microservice
│   │   ├── src/
│   │   │   └── index.js       # Express service
│   │   ├── package.json
│   │   ├── Dockerfile
│   │   └── README.md
│   ├── service-b/              # Second microservice
│   │   ├── src/
│   │   │   └── index.js       # Express service with inter-service calls
│   │   ├── package.json
│   │   ├── Dockerfile
│   │   └── README.md
│   └── .github/
│       └── workflows/          # Service-specific workflows
│           ├── code-quality.yml
│           └── build-test.yml
│
├── infra/                      # Infrastructure as Code
│   ├── docker/
│   │   ├── Dockerfile.base    # Base Docker image
│   │   └── README.md
│   ├── k8s/
│   │   ├── staging/
│   │   │   ├── namespace.yml
│   │   │   ├── service-a-deployment.yml
│   │   │   └── service-b-deployment.yml
│   │   └── production/
│   │       ├── namespace.yml
│   │       ├── service-a-deployment.yml
│   │       └── service-b-deployment.yml
│   ├── terraform/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   └── Jenkinsfile              # Infrastructure pipeline
│
├── cicd/                       # CI/CD Pipelines
│   ├── github/
│   │   └── workflows/
│   │       └── shared-workflows/
│   │           ├── code-quality.yml
│   │           └── build-test.yml
│   ├── jenkins/
│   │   └── pipelines/
│   │       ├── services.groovy
│   │       └── infra.groovy
│   └── README.md
│
├── config/                     # Configuration Management
│   ├── environments/
│   │   ├── staging/
│   │   │   ├── config.json
│   │   │   ├── .env
│   │   │   └── secrets.example
│   │   └── production/
│   │       ├── config.json
│   │       └── .env
│   └── .github/
│       └── workflows/
│           └── config-validation.yml
│
├── libs/                       # Shared Libraries
│   ├── src/
│   │   ├── utils/
│   │   │   ├── logger.js
│   │   │   └── errorHandler.js
│   │   ├── middleware/
│   │   ├── services/
│   │   ├── config/
│   │   └── index.js
│   ├── package.json
│   ├── .gitignore
│   └── .github/
│       └── workflows/
│           └── test.yml
│
├── .github/                    # Root GitHub Actions
│   └── workflows/
│       └── ci-cd-pipeline.yml  # Original workflow (kept for reference)
│
├── package.json                # Root package.json
├── docker-compose.test.yml    # Test environment
├── Jenkinsfile                 # Root Jenkins pipeline
└── README.md                   # This file
```

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/anandpatil/cicd-demo.git
cd cicd-demo
```

### 2. Install Dependencies

```bash
# Install Node.js dependencies for all services
cd services/service-a && npm install
cd ../service-b && npm install
cd ../../libs && npm install
```

### 3. Start Services with Docker Compose

```bash
# Start all services for local development
docker-compose -f docker-compose.test.yml up -d

# Test the services
curl http://localhost:3000/health
curl http://localhost:3001/health
curl http://localhost:3001/call-service-a
```

### 4. Deploy to Kubernetes (Minikube)

```bash
# Start Minikube
minikube start --driver=docker --cpus=4 --memory=8g

# Deploy infrastructure
cd infra
kubectl apply -f k8s/staging/

# Wait for deployments
kubectl wait --for=condition=available --timeout=300s deployment/service-a -n cicd-demo-staging
kubectl wait --for=condition=available --timeout=300s deployment/service-b -n cicd-demo-staging
```

## 🌐 Services

### Service A
- **Port**: 3000
- **Endpoints**:
  - `GET /` - Service information
  - `GET /health` - Health check
- **Technology**: Express.js, Node.js 18

### Service B
- **Port**: 3001
- **Endpoints**:
  - `GET /` - Service information
  - `GET /health` - Health check
  - `GET /call-service-a` - Calls Service A (inter-service communication)
- **Technology**: Express.js, Node.js 18, Axios

## 🔧 CI/CD Architecture

### Hybrid Approach: GitHub Actions + Jenkins

```
Developer Push → GitHub Actions (Quality Gate) → Jenkins (Deployment)
                     ↓
               (Build & Test) → (Staging Deployment)
                     ↓
               (Security Scan) → (Production Approval)
                     ↓
               (Trigger Jenkins) → (Production Deployment)
```

### GitHub Actions Responsibilities
- ✅ Code linting and formatting (ESLint)
- ✅ Unit and integration testing (Jest)
- ✅ Security scanning (CodeQL, Trivy, Gitleaks)
- ✅ SonarQube analysis
- ✅ Docker image building
- ✅ Configuration validation
- ✅ Trigger Jenkins deployment

### Jenkins Responsibilities
- ✅ Docker image registry management
- ✅ Kubernetes deployments
- ✅ Terraform infrastructure management
- ✅ Manual approval gates for production
- ✅ Rollback procedures
- ✅ Production deployments

## 📁 Repository Structure Details

### services/
Contains the microservices application code.

- **service-a/**: Simple Express.js service
- **service-b/**: Express.js service that calls Service A
- **Dockerfiles**: Container configuration for each service
- **GitHub Actions**: Service-specific workflows

### infra/
Contains Infrastructure as Code configurations.

- **docker/**: Base Docker images with security hardening
- **k8s/**: Kubernetes manifests for staging and production
- **terraform/**: Terraform configurations for cloud infrastructure
- **Jenkinsfile**: Infrastructure deployment pipeline

### cicd/
Contains CI/CD pipeline configurations.

- **github/workflows/**: Reusable GitHub Actions workflows
- **jenkins/pipelines/**: Jenkins pipeline scripts (Groovy)
- Shared CI/CD utilities and best practices

### config/
Contains centralized configuration management.

- **environments/**: Environment-specific configurations
- **secrets.example**: Template for secrets (never commit actual secrets)
- **GitHub Actions**: Configuration validation workflows

### libs/
Contains shared libraries and utilities.

- **utils/**: Logger, error handling, validation
- **middleware/**: Authentication, rate limiting
- **services/**: Database, cache services
- **config/**: Configuration loader

## 🛡️ Security Features

### Across All Components
- ✅ Non-root Docker containers
- ✅ Read-only filesystems
- ✅ Dropped Linux capabilities
- ✅ Resource limits
- ✅ Secrets management patterns
- ✅ Network policies
- ✅ TLS encryption
- ✅ Rate limiting

### Repository-Specific
- **services/**: Security-hardened containers, health checks
- **infra/**: Terraform state encryption, Kubernetes RBAC
- **cicd/**: Pipeline isolation, credential management
- **config/**: No actual secrets committed, placeholder files
- **libs/**: Dependency scanning, code review requirements

## 📊 Monitoring and Observability

### Built-in Features
- ✅ Structured JSON logging
- ✅ Health check endpoints
- ✅ Prometheus metrics (ready for integration)
- ✅ Distributed tracing (ready for integration)
- ✅ Error tracking

### Recommended Stack
- **Prometheus**: Metrics collection
- **Grafana**: Visualization
- **ELK Stack**: Logging
- **Jaeger**: Distributed tracing

## 🎯 Development Workflow

### 1. Create a New Service

```bash
# Create new service directory
mkdir -p services/service-c/src
cd services/service-c

# Initialize package.json
npm init -y

# Create service files
# src/index.js, Dockerfile, package.json, README.md

# Add to docker-compose.test.yml
# Add Kubernetes manifests to infra/k8s/
```

### 2. Update Configuration

```bash
# Add new environment configuration
mkdir -p config/environments/new-env
cp config/environments/staging/config.json config/environments/new-env/
# Edit the new config file
```

### 3. Add to CI/CD

```bash
# Add GitHub Actions workflow for new service
cp services/.github/workflows/code-quality.yml services/service-c/.github/workflows/
# Customize as needed

# Add to Jenkins pipeline
# Edit cicd/jenkins/pipelines/services.groovy
```

### 4. Test Locally

```bash
# Test the new service
cd services/service-c
npm install
npm test

# Test with Docker
cd services/service-c
docker build -t service-c:latest .
docker run -p 3002:3002 service-c:latest

# Test with Docker Compose
# Add to docker-compose.test.yml
cd /workspace
docker-compose -f docker-compose.test.yml up -d service-c
```

## 🚀 Deployment

### Local (Minikube)

```bash
# Start Minikube
minikube start --driver=docker --cpus=4 --memory=8g

# Deploy
cd infra
kubectl apply -f k8s/staging/

# Verify
kubectl get pods -n cicd-demo-staging
kubectl get services -n cicd-demo-staging
```

### Production (Cloud Kubernetes)

```bash
# Update Kubernetes manifests for production
cd infra/k8s/production
# Edit deployment files as needed

# Apply Terraform for infrastructure
cd infra/terraform
terraform init
terraform plan
terraform apply

# Deploy to production
kubectl apply -f k8s/production/
```

## 📚 Documentation

Each directory contains comprehensive documentation:

- **services/README.md**: Microservices development guide
- **infra/README.md**: Infrastructure as Code guide
- **cicd/README.md**: CI/CD pipeline documentation
- **config/README.md**: Configuration management guide
- **libs/README.md**: Shared libraries documentation

## 🔗 Cross-Repository Dependencies

```
services/ ─────┐
              ├───▶ libs/ (uses shared libraries)
              │
infra/ ───────┼───▶ config/ (uses configuration)
              │
              ▼
cicd/ ─────────────────────────────────────────────────▶ GitHub/Jenkins
```

## 🎓 Learning Path

### Beginner
1. Understand the monorepo structure
2. Run services locally with Docker Compose
3. Test the endpoints
4. Explore the code

### Intermediate
1. Modify a service (service-a or service-b)
2. Add a new endpoint
3. Update Kubernetes manifests
4. Configure GitHub Actions secrets

### Advanced
1. Add a new microservice
2. Implement database integration
3. Setup monitoring with Prometheus and Grafana
4. Configure Terraform for cloud deployment

### Expert
1. Setup multi-cluster Kubernetes
2. Implement service mesh (Istio/Linkerd)
3. Configure advanced security policies
4. Setup CI/CD for multiple environments
5. Implement canary deployments

## 📞 Support

### Troubleshooting

| Issue | Solution |
|-------|----------|
| Docker permission denied | `sudo usermod -aG docker $USER`, logout/login |
| Minikube fails to start | Increase resources: `--cpus=4 --memory=8g` |
| Images not found in Minikube | `eval $(minikube docker-env)` before building |
| Services not accessible | Check port forwarding: `kubectl port-forward` |
| Jenkins setup issues | Check logs: `docker logs jenkins` |

### Useful Commands

```bash
# View all Docker containers
docker ps -a

# View Docker logs
docker logs <container_name>

# View Kubernetes pods
kubectl get pods -A

# View pod logs
kubectl logs <pod_name> -n <namespace>

# View Minikube status
minikube status

# Access Minikube dashboard
minikube dashboard

# View GitHub Actions workflows
gh workflow list

# View Jenkins jobs
# Access http://localhost:8080
```

## 🏆 Success Metrics

By using this monorepo, you get:

✅ **Separation of Concerns**: Clear directory structure for each concern
✅ **Microservices Architecture**: Independent services with inter-service communication
✅ **Hybrid CI/CD**: Best of both GitHub Actions and Jenkins
✅ **Local Development**: Easy testing with Docker Compose and Minikube
✅ **Production Ready**: Scalable infrastructure with Kubernetes and Terraform
✅ **Comprehensive Documentation**: Guides for every aspect
✅ **Security Best Practices**: Built-in security features
✅ **Observability**: Monitoring and logging ready

## 📝 Version History

| Version | Date | Description |
|---------|------|-------------|
| 1.0.0 | 2026-01-05 | Initial monorepo structure with microservices, infra, cicd, config, libs |

## 🎉 Next Steps

1. **Explore the structure**: Understand how everything is organized
2. **Run locally**: Test the services with Docker Compose
3. **Deploy to Minikube**: Experience Kubernetes deployment
4. **Setup CI/CD**: Configure GitHub Actions and Jenkins
5. **Add new features**: Extend the services or add new ones
6. **Deploy to cloud**: Move from Minikube to production Kubernetes

## 🏆 Achievements

By using this monorepo, you will:

- ✅ Learn microservices architecture
- ✅ Implement containerization with Docker
- ✅ Deploy to Kubernetes
- ✅ Configure hybrid CI/CD pipelines
- ✅ Automate testing and deployment
- ✅ Establish monitoring and observability
- ✅ Create production-ready infrastructure

---

*This monorepo provides a solid foundation for building, testing, and deploying microservices with modern CI/CD practices.*
*Feel free to customize, extend, and adapt it to your specific needs.*