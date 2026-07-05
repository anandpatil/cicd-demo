# Docker Configurations

This directory contains Docker-related configurations for the CI/CD demo project.

## Base Image

The `Dockerfile.base` provides a common base image for all services with:
- Node.js 18 on Alpine Linux
- System dependencies (curl, wget)
- Global npm packages
- Non-root user for security
- Health check configuration

## Usage

### Building the base image

```bash
docker build -t cicd-demo-base:latest -f Dockerfile.base .
```

### Using the base image

In your service Dockerfiles:

```dockerfile
FROM cicd-demo-base:latest

WORKDIR /app
COPY package*.json ./
RUN npm install --production
COPY src/ ./src/

EXPOSE 3000
CMD ["node", "src/index.js"]
```

## Security Best Practices

1. **Non-root user**: All containers run as non-root user
2. **Minimal base**: Alpine Linux for smaller attack surface
3. **Multi-stage builds**: Production images don't include build tools
4. **Health checks**: Built-in health monitoring
5. **Regular updates**: Base images are updated regularly

## Image Tags

- `latest`: Latest stable version
- `v1.0.0`: Specific version tags
- `sha-<commit>`: Git commit SHA tags