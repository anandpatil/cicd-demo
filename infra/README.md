# CI/CD Demo - Infrastructure

This repository contains all infrastructure as code for the CI/CD demo project.

## Structure

```
cicd-demo-infra/
├── docker/
│   ├── Dockerfile.base
│   └── README.md
├── k8s/
│   ├── staging/
│   │   ├── service-a-deployment.yml
│   │   ├── service-b-deployment.yml
│   │   └── namespace.yml
│   ├── production/
│   │   ├── service-a-deployment.yml
│   │   ├── service-b-deployment.yml
│   │   └── namespace.yml
│   └── README.md
├── terraform/
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   └── modules/
├── .github/
│   └── workflows/
│       └── infra-validation.yml
├── Jenkinsfile
└── README.md
```

## Components

### Docker
- Base Docker images
- Multi-stage build configurations
- Security hardening

### Kubernetes
- Deployment manifests for staging and production
- Service definitions
- Ingress configurations
- ConfigMaps and Secrets

### Terraform
- Cloud infrastructure provisioning
- Networking setup
- Load balancers
- Monitoring resources

## CI/CD Integration

- **GitHub Actions**: Validates infrastructure code, runs Terraform plan
- **Jenkins**: Applies Terraform changes, manages Kubernetes deployments

## Usage

### Docker

```bash
# Build base image
docker build -t cicd-demo-base:latest -f docker/Dockerfile.base .
```

### Kubernetes

```bash
# Deploy to staging
kubectl apply -f k8s/staging/

# Deploy to production
kubectl apply -f k8s/production/
```

### Terraform

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

## Cross-Repository Dependencies

- **cicd-demo-services**: Uses Docker images and Kubernetes manifests from here
- **cicd-demo-cicd**: CI/CD pipelines reference infrastructure definitions
- **cicd-demo-config**: Shared configuration values

## Security

- All secrets are managed through external secret managers
- Kubernetes secrets are encrypted at rest
- IAM roles follow principle of least privilege

## License

MIT