# CI/CD Demo - Configuration

This repository contains all shared configuration for the CI/CD demo project.

## Structure

```
cicd-demo-config/
├── environments/
│   ├── staging/
│   │   ├── config.json
│   │   ├── .env
│   │   └── secrets.example
│   ├── production/
│   │   ├── config.json
│   │   ├── .env
│   │   └── secrets.example
│   └── development/
│       ├── config.json
│       ├── .env
│       └── secrets.example
├── services/
│   ├── service-a/
│   │   ├── config.json
│   │   └── .env.example
│   ├── service-b/
│   │   ├── config.json
│   │   └── .env.example
│   └── shared/
│       ├── database.json
│       ├── cache.json
│       └── messaging.json
├── ci-cd/
│   ├── github-actions/
│   │   ├── secrets.example
│   │   └── variables.example
│   ├── jenkins/
│   │   ├── credentials.example
│   │   └── config.example
│   └── shared/
│       ├── environment-variables.json
│       └── pipeline-config.json
├── kubernetes/
│   ├── configmaps/
│   └── secrets/
├── .github/
│   └── workflows/
│       └── config-validation.yml
├── Jenkinsfile
└── README.md
```

## Configuration Management

This repository follows the **12-factor app** configuration principles:

1. **Environment-specific**: Each environment has its own configuration
2. **Version controlled**: All non-sensitive configuration is in Git
3. **External secrets**: Sensitive data is managed externally
4. **Consistent structure**: Same structure across all environments

## Environments

### Development
- Local development configuration
- Default values for testing
- No sensitive data

### Staging
- Pre-production environment
- Similar to production but with test data
- Used for final validation

### Production
- Live environment configuration
- Production-grade settings
- All sensitive data externalized

## Usage

### For Services

```javascript
// In your service code
const config = require('@cicd-demo/config');

// Access configuration
const dbConfig = config.database;
const cacheConfig = config.cache;
```

### For CI/CD

```yaml
# In GitHub Actions workflows
- name: Load configuration
  run: |
    curl -s https://raw.githubusercontent.com/anandpatil/cicd-demo-config/main/environments/${{ github.event_name == 'push' && github.ref == 'refs/heads/main' ? 'production' : 'staging' }}/config.json > config.json
```

### For Kubernetes

```yaml
# In Kubernetes manifests
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: cicd-demo-staging
data:
  CONFIG_JSON: |
    {{ .Files.Get "environments/staging/config.json" | indent 4 }}
```

## Security

### Secrets Management

- **Never commit secrets** to this repository
- Use environment variables for sensitive data
- Use external secret managers (Vault, AWS Secrets Manager, etc.)
- Reference secrets in configuration files using placeholders

### Example Secrets Structure

```bash
# .env file (never commit this!)
DB_HOST=localhost
DB_PORT=5432
DB_USER=appuser
DB_PASSWORD=${DB_PASSWORD}  # This comes from external secret manager
API_KEY=${API_KEY}
```

### Secret Placeholders

Use placeholders in configuration files:

```json
{
  "database": {
    "host": "${DB_HOST}",
    "port": ${DB_PORT},
    "user": "${DB_USER}",
    "password": "${DB_PASSWORD}"
  }
}
```

## Cross-Repository Integration

This repository is referenced by:
- **cicd-demo-services**: Services consume configuration
- **cicd-demo-infra**: Infrastructure uses configuration values
- **cicd-demo-cicd**: CI/CD pipelines use configuration for deployment

## Versioning

Configuration files are versioned alongside the code that uses them. When services are updated, the corresponding configuration should be updated as well.

## Validation

All configuration files are validated in CI/CD pipelines to ensure:
- Valid JSON/YAML syntax
- Required fields are present
- Values are of correct types
- No sensitive data is committed

## License

MIT