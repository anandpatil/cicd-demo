// CI/CD Demo Shared Libraries
// Main entry point

const utils = require('./utils');
const middleware = require('./middleware');
const services = require('./services');
const config = require('./config');

module.exports = {
  // Utilities
  ...utils,
  
  // Middleware
  ...middleware,
  
  // Services
  ...services,
  
  // Configuration
  ...config,
  
  // Library metadata
  name: '@cicd-demo/libs',
  version: '1.0.0',
  description: 'Shared libraries for CI/CD Demo project'
};

// Export individual modules for tree-shaking
module.exports.utils = utils;
module.exports.middleware = middleware;
module.exports.services = services;
module.exports.config = config;