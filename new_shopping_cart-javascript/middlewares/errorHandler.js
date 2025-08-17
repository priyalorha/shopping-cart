// middlewares/errorHandler.js
export class AppError extends Error {
  constructor(message, statusCode, type = 'OPERATIONAL_ERROR', details = null) {
    super(message);
    this.statusCode = statusCode;
    this.type = type;
    this.details = details;
    this.isOperational = true;
    Error.captureStackTrace(this, this.constructor);
  }

  toJSON() {
    return {
      error: {
        code: this.statusCode,
        type: this.type,
        message: this.message,
        ...(this.details && { details: this.details })
      },
      timestamp: new Date().toISOString()
    };
  }
}

// Predefined error types
export const Errors = {
  badRequest: (message = 'Bad request', details = null) => 
    new AppError(message, 400, 'BAD_REQUEST', details),
  
  unauthenticated: (message = 'Authentication required', details = null) => 
    new AppError(message, 401, 'AUTHENTICATION_ERROR', details),
  
  unauthorized: (message = 'Not authorized', details = null) => 
    new AppError(message, 403, 'AUTHORIZATION_ERROR', details),
  
  notFound: (message = 'Resource not found', details = null) => 
    new AppError(message, 404, 'NOT_FOUND', details),
  
  conflict: (message = 'Resource conflict', details = null) => 
    new AppError(message, 409, 'CONFLICT', details),
  
  validation: (errors, message = 'Validation failed') => 
    new AppError(message, 400, 'VALIDATION_ERROR', { errors }),
  
  internal: (message = 'Internal server error') => 
    new AppError(message, 500, 'INTERNAL_ERROR')
};

export const errorHandler = (err, req, res, next) => {
  // Log error information
  console.error(`[${new Date().toISOString()}] ${err.type || 'UNKNOWN_ERROR'}:`, {
    message: err.message,
    status: err.statusCode || 500,
    path: req.originalUrl,
    method: req.method,
    ...(process.env.NODE_ENV === 'development' && { stack: err.stack }),
    ...(err.details && { details: err.details })
  });

  // Handle non-AppError instances
  if (!(err instanceof AppError)) {
    err = Errors.internal(err.message);
  }

  // Send error response
  res.status(err.statusCode).json({
    success: false,
    ...err.toJSON(),
    ...(process.env.NODE_ENV === 'development' && { stack: err.stack })
  });
};

// Utility for async error handling
export const catchAsync = (fn) => {
  return (req, res, next) => {
    fn(req, res, next).catch(next);
  };
};