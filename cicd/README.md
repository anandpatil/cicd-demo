# CI/CD Demo - CI/CD Pipelines

This repository contains all CI/CD pipeline configurations for the CI/CD demo project.

## Structure

```
cicd-demo-cicd/
├── github/
│   ├── workflows/
│   │   ├── shared-workflows/
│   │   │   ├── code-quality.yml
│   │   │   ├── build-test.yml
│   │   │   └── security-scan.yml
│   │   └── repository-specific/
│   ├── actions/
│   │   └── setup-environment/
│   │       ├── action.yml
│   │       └── scripts/
│   └── README.md
├── jenkins/
│   ├── pipelines/
│   │   ├── services.groovy
│   │   ├── infra.groovy
│   │   └── shared.groovy
│   ├── scripts/
│   │   ├── deploy.sh
│   │   ├── rollback.sh
│   │   └── notify.sh
│   └── README.md
├── shared/
│   ├── workflows/
│   │   ├── common-steps.yml
│   │   └── environment-variables.yml
│   ├── scripts/
│   │   ├── build-docker.sh
│   │   ├── run-tests.sh
│   │   └── security-scan.sh
│   └── README.md
├── .github/
│   └── workflows/
│       └── cicd-validation.yml
├── Jenkinsfile
└── README.md
```

## Hybrid CI/CD Architecture

This repository implements a **hybrid CI/CD approach**:

- **GitHub Actions**: Primary for code quality, testing, and build validation
- **Jenkins**: Primary for deployment, infrastructure management, and production releases

### Workflow

```
Developer Push → GitHub Actions (Quality Gate) → Jenkins (Deployment)
                     ↓
               (Build & Test) → (Staging Deployment)
                     ↓
               (Security Scan) → (Production Approval)
                     ↓
               (Trigger Jenkins) → (Production Deployment)
```

## Components

### GitHub Actions

- **Shared Workflows**: Reusable workflows for common tasks
- **Repository-Specific**: Custom workflows for each repository
- **Custom Actions**: Composite actions for complex setup

### Jenkins

- **Pipeline Scripts**: Groovy scripts for Jenkins pipelines
- **Shared Libraries**: Common functions and utilities
- **Deployment Scripts**: Shell scripts for deployment tasks

### Shared Resources

- **Common Steps**: Reusable pipeline steps
- **Environment Configurations**: Environment-specific variables
- **Utility Scripts**: Common scripting utilities

## Cross-Repository Integration

This repository coordinates CI/CD across:
- **cicd-demo-services**: Microservices code
- **cicd-demo-infra**: Infrastructure as Code
- **cicd-demo-config**: Configuration management
- **cicd-demo-libs**: Shared libraries

## Usage

### GitHub Actions

1. **Import Shared Workflows**: Reference workflows from this repository
2. **Use Custom Actions**: Call composite actions in your workflows
3. **Trigger Downstream**: Use repository_dispatch to trigger other repos

### Jenkins

1. **Pipeline Templates**: Use the provided Groovy templates
2. **Shared Libraries**: Import common functions
3. **Deployment Scripts**: Call shell scripts for complex operations

## Security

- **Secrets Management**: All secrets are stored in external managers
- **Least Privilege**: Each pipeline has minimal required permissions
- **Audit Logging**: All CI/CD activities are logged
- **Approval Gates**: Manual approvals for production deployments

## Monitoring

- **Pipeline Metrics**: Track success/failure rates
- **Performance Monitoring**: Measure pipeline execution times
- **Alerting**: Notifications for pipeline failures

## License

MIT