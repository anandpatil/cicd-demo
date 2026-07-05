const winston = require('winston');
const { combine, timestamp, printf, colorize, json } = winston.format;

/**
 * Logger utility for structured logging
 * Supports multiple transports and formats
 */
class Logger {
  constructor(options = {}) {
    const {
      service = 'unknown',
      level = 'info',
      format = 'json',
      output = 'stdout',
      includeTimestamp = true,
      includeService = true,
      includeLevel = true,
      fileTransport = false
    } = options;

    // Define custom format
    const customFormat = printf(({ level, message, timestamp, service: serviceName, ...meta }) => {
      let logMessage = `${timestamp} [${level}]`;
      
      if (includeService && serviceName) {
        logMessage += ` [${serviceName}]`;
      }
      
      logMessage += ` ${message}`;
      
      if (Object.keys(meta).length > 0) {
        logMessage += ` ${JSON.stringify(meta)}`;
      }
      
      return logMessage;
    });

    // Create transports array
    const transports = [];
    
    // Console transport
    if (output === 'stdout' || output === 'both') {
      transports.push(
        new winston.transports.Console({
          format: format === 'json' 
            ? combine(
                timestamp(),
                json()
              )
            : combine(
                colorize(),
                timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
                customFormat
              )
        })
      );
    }

    // File transport
    if (fileTransport || output === 'file' || output === 'both') {
      const DailyRotateFile = require('winston-daily-rotate-file');
      
      transports.push(
        new DailyRotateFile({
          filename: `logs/${service}-%DATE%.log`,
          datePattern: 'YYYY-MM-DD',
          maxSize: '20m',
          maxFiles: '14d',
          format: combine(
            timestamp(),
            json()
          )
        })
      );
    }

    // Create logger instance
    this.logger = winston.createLogger({
      level,
      format: combine(
        timestamp(),
        includeService ? winston.format((info) => {
          info.service = service;
          return info;
        })() : winston.format.uncolorize()
      ),
      transports,
      exitOnError: false
    });

    // Add stream for morgan if needed
    this.stream = {
      write: (message) => {
        this.logger.info(message.trim());
      }
    };
  }

  /**
   * Log debug message
   */
  debug(message, meta = {}) {
    this.logger.debug(message, meta);
  }

  /**
   * Log info message
   */
  info(message, meta = {}) {
    this.logger.info(message, meta);
  }

  /**
   * Log warning message
   */
  warn(message, meta = {}) {
    this.logger.warn(message, meta);
  }

  /**
   * Log error message
   */
  error(message, meta = {}) {
    this.logger.error(message, meta);
  }

  /**
   * Log fatal message
   */
  fatal(message, meta = {}) {
    this.logger.error(message, { ...meta, level: 'fatal' });
  }

  /**
   * Create child logger with additional context
   */
  child(context = {}) {
    return new Logger({
      ...this.options,
      ...context
    });
  }

  /**
   * Get the underlying winston logger
   */
  getLogger() {
    return this.logger;
  }
}

/**
 * Create a logger instance
 * @param {Object} options - Logger options
 * @returns {Logger} Logger instance
 */
function createLogger(options = {}) {
  return new Logger(options);
}

// Default logger instance
const defaultLogger = createLogger({
  service: 'cicd-demo',
  level: process.env.LOG_LEVEL || 'info',
  format: process.env.LOG_FORMAT || 'json',
  output: process.env.LOG_OUTPUT || 'stdout'
});

module.exports = {
  Logger,
  createLogger,
  logger: defaultLogger
};

// Export for direct usage
module.exports.default = defaultLogger;