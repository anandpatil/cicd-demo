# CI/CD Demo - Shared Libraries

This repository contains shared libraries and utilities for the CI/CD demo project.

## Structure

```
cicd-demo-libs/
├── src/
│   ├── utils/
│   │   ├── logger.js
│   │   ├── errorHandler.js
│   │   ├── validator.js
│   │   └── index.js
│   ├── middleware/
│   │   ├── auth.js
│   │   ├── rateLimiter.js
│   │   └── index.js
│   ├── services/
│   │   ├── database.js
│   │   ├── cache.js
│   │   └── index.js
│   ├── config/
│   │   ├── index.js
│   │   └── loader.js
│   └── index.js
├── test/
│   ├── utils.test.js
│   ├── middleware.test.js
│   └── services.test.js
├── package.json
├── .github/
│   └── workflows/
│       ├── test.yml
│       ├── publish.yml
│       └── code-quality.yml
├── Jenkinsfile
└── README.md
```

## Shared Libraries

This repository provides reusable components for all microservices:

### Utilities
- **Logger**: Structured logging with levels and formats
- **Error Handler**: Consistent error handling and formatting
- **Validator**: Input validation and sanitization
- **Helpers**: Common utility functions

### Middleware
- **Authentication**: JWT and API key authentication
- **Rate Limiter**: Request rate limiting
- **Request Logger**: Request/response logging
- **Error Handler**: Centralized error handling middleware

### Services
- **Database**: Database connection pooling and query helpers
- **Cache**: Redis cache service with TTL and prefix support
- **Messaging**: Message queue service for inter-service communication

### Configuration
- **Config Loader**: Loads and validates configuration from multiple sources
- **Environment**: Environment-specific configuration handling

## Usage

### Install the library

```bash
npm install @cicd-demo/libs
```

### Use in your service

```javascript
const { logger, errorHandler, database } = require('@cicd-demo/libs');

// Use the logger
logger.info('Service started', { service: 'service-a', version: '1.0.0' });

// Use the database service
database.query('SELECT * FROM users')
  .then(users => {
    logger.debug('Users retrieved', { count: users.length });
  })
  .catch(errorHandler.handle);
```

### Import specific modules

```javascript
const { authMiddleware, rateLimiter } = require('@cicd-demo/libs/middleware');
const { Logger } = require('@cicd-demo/libs/utils');

const logger = new Logger({ service: 'service-a' });
```

## Development

### Install dependencies

```bash
npm install
```

### Run tests

```bash
npm test
```

### Build the library

```bash
npm run build
```

### Publish to registry

```bash
npm publish
```

## Versioning

This library follows **Semantic Versioning** (SemVer):
- **Major**: Breaking changes
- **Minor**: New features (backward compatible)
- **Patch**: Bug fixes (backward compatible)

## CI/CD Integration

- **GitHub Actions**: Runs tests, code quality checks, and publishes new versions
- **Jenkins**: Handles deployment and version management

## Cross-Repository Dependencies

This repository is used by:
- **cicd-demo-services**: All services depend on this library
- **cicd-demo-config**: Configuration structure is designed to work with this library

## Security

- **Dependency Scanning**: All dependencies are scanned for vulnerabilities
- **Code Review**: All changes require approval
- **Testing**: All changes must pass existing tests
- **Semantic Versioning**: Breaking changes require major version bump

## License

MIT