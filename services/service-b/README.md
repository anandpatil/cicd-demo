# Service B

Second microservice in the CI/CD demo project.

## Endpoints

- `GET /` - Service information
- `GET /health` - Health check
- `GET /call-service-a` - Calls Service A (demonstrates inter-service communication)

## Environment Variables

- `PORT` - Port to run the service (default: 3001)
- `SERVICE_A_URL` - URL of Service A (default: http://service-a:3000)

## Development

```bash
npm install
npm start
```

## Docker

```bash
docker build -t service-b:latest .
docker run -p 3001:3001 service-b:latest
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