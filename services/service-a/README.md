# Service A

First microservice in the CI/CD demo project.

## Endpoints

- `GET /` - Service information
- `GET /health` - Health check

## Environment Variables

- `PORT` - Port to run the service (default: 3000)

## Development

```bash
npm install
npm start
```

## Docker

```bash
docker build -t service-a:latest .
docker run -p 3000:3000 service-a:latest
```

## Testing

```bash
npm test
```

## CI/CD

This service uses:
- GitHub Actions for code quality and testing
- Jenkins for deployment

See the main repository README for CI/CD details.