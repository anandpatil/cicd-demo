/**
 * Error handling utilities
 * Provides consistent error handling across all services
 */

class AppError extends Error {
  constructor(message, statusCode, isOperational = true, details = null) {
    super(message);
    
    this.name = this.constructor.name;
    this.statusCode = statusCode;
    this.isOperational = isOperational;
    this.details = details;
    this.timestamp = new Date().toISOString();
    
    // Capture stack trace
    Error.captureStackTrace(this, this.constructor);
  }
}

/**
 * HTTP error classes
 */
class BadRequestError extends AppError {
  constructor(message = 'Bad Request', details = null) {
    super(message, 400, true, details);
  }
}

class UnauthorizedError extends AppError {
  constructor(message = 'Unauthorized', details = null) {
    super(message, 401, true, details);
  }
}

class ForbiddenError extends AppError {
  constructor(message = 'Forbidden', details = null) {
    super(message, 403, true, details);
  }
}

class NotFoundError extends AppError {
  constructor(message = 'Not Found', details = null) {
    super(message, 404, true, details);
  }
}

class ConflictError extends AppError {
  constructor(message = 'Conflict', details = null) {
    super(message, 409, true, details);
  }
}

class ValidationError extends AppError {
  constructor(message = 'Validation Error', details = null) {
    super(message, 400, true, details);
  }
}

class RateLimitError extends AppError {
  constructor(message = 'Rate Limit Exceeded', details = null) {
    super(message, 429, true, details);
  }
}

class InternalServerError extends AppError {
  constructor(message = 'Internal Server Error', details = null) {
    super(message, 500, false, details);
  }
}

class ServiceUnavailableError extends AppError {
  constructor(message = 'Service Unavailable', details = null) {
    super(message, 503, true, details);
  }
}

/**
 * Error handler middleware for Express
 */
function errorHandler(err, req, res, next) {
  // Log the error
  const logger = require('./logger').logger;
  
  if (err.isOperational) {
    logger.warn('Operational error occurred', {
      error: err.message,
      statusCode: err.statusCode,
      path: req.path,
      method: req.method
    });
  } else {
    logger.error('Unexpected error occurred', {
      error: err.message,
      stack: err.stack,
      path: req.path,
      method: req.method
    });
  }

  // Determine error details
  let statusCode = err.statusCode || 500;
  let message = err.message || 'Internal Server Error';
  let details = err.details || null;
  
  // Handle Mongoose validation errors
  if (err.name === 'ValidationError') {
    statusCode = 400;
    message = 'Validation Error';
    details = Object.values(err.errors).map(e => e.message);
  }
  
  // Handle MongoDB duplicate key errors
  if (err.code === 11000) {
    statusCode = 409;
    message = 'Duplicate key error';
    details = { field: Object.keys(err.keyPattern)[0] };
  }
  
  // Handle JWT errors
  if (err.name === 'JsonWebTokenError') {
    statusCode = 401;
    message = 'Invalid token';
  }
  
  if (err.name === 'TokenExpiredError') {
    statusCode = 401;
    message = 'Token expired';
  }

  // Send error response
  const errorResponse = {
    success: false,
    error: {
      message,
      code: statusCode,
      ...(details && { details }),
      ...(process.env.NODE_ENV === 'development' && { stack: err.stack })
    }
  };

  res.status(statusCode).json(errorResponse);
}

/**
 * Async error handler wrapper
 * Wraps async functions to catch and forward errors
 */
function asyncHandler(fn) {
  return (req, res, next) => {
    Promise.resolve(fn(req, res, next)).catch(next);
  };
}

/**
 * Handle promise rejections globally
 */
function setupUnhandledRejectionHandler() {
  process.on('unhandledRejection', (reason, promise) => {
    const logger = require('./logger').logger;
    logger.error('Unhandled Rejection at Promise', {
      promise,
      reason
    });
    
    // Application specific logging, throwing an error, or other logic
    throw reason;
  });
}

/**
 * Handle uncaught exceptions globally
 */
function setupUncaughtExceptionHandler() {
  process.on('uncaughtException', (err) => {
    const logger = require('./logger').logger;
    logger.error('Uncaught Exception thrown', {
      error: err.message,
      stack: err.stack
    });
    
    // Graceful shutdown
    process.exit(1);
  });
}

/**
 * Setup global error handlers
 */
function setupErrorHandlers() {
  setupUnhandledRejectionHandler();
  setupUncaughtExceptionHandler();
}

module.exports = {
  // Error classes
  AppError,
  BadRequestError,
  UnauthorizedError,
  ForbiddenError,
  NotFoundError,
  ConflictError,
  ValidationError,
  RateLimitError,
  InternalServerError,
  ServiceUnavailableError,
  
  // Error handling functions
  errorHandler,
  asyncHandler,
  setupErrorHandlers,
  setupUnhandledRejectionHandler,
  setupUncaughtExceptionHandler
};

// Export for direct usage
module.exports.default = errorHandler;