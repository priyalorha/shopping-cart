import { z } from 'zod'; // Add this import

export class AppError extends Error {
  constructor(
    message,
    statusCode = 422,
    type = 'API_ERROR',
    details = null,
    isOperational = true
  ) {
    super(message);
    this.statusCode = statusCode;
    this.type = type;
    this.details = details;
    this.isOperational = isOperational;
    Error.captureStackTrace(this, this.constructor);
  }

  toJSON() {
    return {
      success: false,
      error: {
        code: this.statusCode,
        type: this.type,
        message: this.message,
        ...(this.details && { details: this.details }),
        ...(process.env.NODE_ENV === 'development' && {
          stack: this.stack
        })
      },
      timestamp: new Date().toISOString()
    };
  }
}

export const errorHandler = (err, req, res, next) => {
  // Default to 500 if status code not set
  
  let statusCode = err.statusCode || 500;
  
  // Handle Sequelize errors
  if (err.name && err.name.includes('Sequelize')) {
    statusCode = 409
    const response = {
      success: false,
      error: {
        code: 409,
        type: err.name,
        message: err.message || 'Database error',
        ...(process.env.NODE_ENV === 'development' && {
          stack: err.stack
        })
      },
      timestamp: new Date().toISOString()
    };

    // Add details for validation errors
    if (err.errors && err.errors.length > 0) {
      response.error.details = err.errors.map(e => ({
        field: e.path,
        message: e.message,
        type: e.type
      }));
    }

    return res.status(statusCode).json(response);
  }
  
  console.log( err);
  // Handle Zod validation errors
  if (err instanceof z.ZodError) {
    return res.status(400).json({
      success: false,
      error: formatZodError(err),
      timestamp: new Date().toISOString()
    });
  }

  // Handle AppError instances
  if (err instanceof AppError) {
    return res.status(err.statusCode).json(err.toJSON());
  }

  // Handle all other errors
  const error = new AppError(
    err.message || 'Internal Server Error',
    statusCode,
    err.name || 'INTERNAL_ERROR',
    err.details || null,
    false
  );

  // Log error information
  console.error(
    `[${new Date().toISOString()}] ${error.type}: ${error.message}`,
    {
      path: req.originalUrl,
      method: req.method,
      ...(error.details && { details: error.details }),
      ...(process.env.NODE_ENV === 'development' && { stack: error.stack })
    }
  );

  res.status(error.statusCode).json(error.toJSON());
};

// Common error types as functions
export const Errors = {
  badRequest: (message, details) =>
    new AppError(message, 400, 'BAD_REQUEST', details),
  unauthenticated: (message = 'Authentication required') =>
    new AppError(message, 401, 'AUTHENTICATION_ERROR'),
  unauthorized: (message = 'Not authorized to access this resource') =>
    new AppError(message, 403, 'AUTHORIZATION_ERROR'),
  notFound: (message = 'Resource not found') =>
    new AppError(message, 404, 'NOT_FOUND'),
  conflict: (message = 'Resource conflict') =>
    new AppError(message, 409, 'CONFLICT'),
  validation: (errors, message = 'Validation failed') =>
    new AppError(message, 400, 'VALIDATION_ERROR', { errors }),
  internal: (message = 'Internal Server Error') =>
    new AppError(message, 500, 'INTERNAL_ERROR')
};

function formatZodError(error) {
  const issues = error.issues || error.errors || [];
  
  
  if (issues.length === 0) {
    return [{
      field: 'unknown',
      code: 'invalid_request',
      message: error.message || 'Invalid data format'
    }];
  }

  return issues.map(issue => ({
    field: issue.path?.join('.') || 'unknown_field',
    code: issue.code || 'validation_error',
    message: issue.message || 'Invalid value',
    ...(issue.expected && { expected: issue.expected })
  }));
}
