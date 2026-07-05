# Kubernetes Configurations

This directory contains Kubernetes manifests for deploying the CI/CD demo services.

## Structure

```
k8s/
├── staging/
│   ├── namespace.yml           # Namespace and common resources
│   ├── service-a-deployment.yml
│   └── service-b-deployment.yml
├── production/
│   ├── namespace.yml           # Namespace and common resources
│   ├── service-a-deployment.yml
│   └── service-b-deployment.yml
└── README.md
```

## Environments

### Staging
- Used for testing and validation
- Automatically deployed on main branch updates
- Lower resource limits
- Debug logging enabled

### Production
- Used for live traffic
- Requires manual approval
- Higher resource limits
- Production logging level

## Deployment Strategy

- **Rolling Updates**: Zero downtime deployments
- **Replica Sets**: Multiple instances for high availability
- **Resource Limits**: Prevent resource exhaustion
- **Health Checks**: Automatic recovery from failures

## Security Features

1. **Non-root containers**: All containers run as non-root users
2. **Read-only filesystems**: Prevent unauthorized file modifications
3. **Capability dropping**: Remove unnecessary Linux capabilities
4. **Resource limits**: Prevent resource exhaustion attacks
5. **Network policies**: Restrict pod-to-pod communication
6. **Secrets management**: External secret management integration

## Usage

### Deploy to Staging

```bash
# Create namespace and common resources
kubectl apply -f k8s/staging/namespace.yml

# Deploy services
kubectl apply -f k8s/staging/service-a-deployment.yml
kubectl apply -f k8s/staging/service-b-deployment.yml

# Verify deployment
kubectl get pods -n cicd-demo-staging
kubectl get services -n cicd-demo-staging
```

### Deploy to Production

```bash
# Create namespace and common resources
kubectl apply -f k8s/production/namespace.yml

# Deploy services
kubectl apply -f k8s/production/service-a-deployment.yml
kubectl apply -f k8s/production/service-b-deployment.yml

# Verify deployment
kubectl get pods -n cicd-demo-production
kubectl get services -n cicd-demo-production
```

## Customization

### Environment Variables

Edit the ConfigMap in `namespace.yml` to customize environment variables.

### Resource Limits

Adjust CPU and memory requests/limits in each deployment file based on your workload requirements.

### Scaling

Modify the `replicas` field in each deployment to scale the number of instances.

## Monitoring

The deployments include liveness and readiness probes for automatic health monitoring.

## Troubleshooting

### Check pod logs
```bash
kubectl logs -n cicd-demo-staging <pod-name>
```

### Describe pod
```bash
kubectl describe pod -n cicd-demo-staging <pod-name>
```

### Check events
```bash
kubectl get events -n cicd-demo-staging
```