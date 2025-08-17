import jwt from 'jsonwebtoken';
import User from '../models/user.js';
import { Errors, catchAsync } from '../middlewares/errorHandler.js';

export const authenticate = async (req, res, next) => {
  try {
    // 1. Check for token in cookies or Authorization header
    let token;
    
    // Check cookies first
    if (req.cookies?.token) {
      token = req.cookies.token;
    } 
    // Then check Authorization header
    else if (req.headers.authorization?.startsWith('Bearer ')) {
      token = req.headers.authorization.split(' ')[1];
    }

    if (!token) {
      throw Errors.badRequest('Authentication token missing');
    }

    // 2. Verify token
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    
    // 3. Find user in database
    const user = await User.findByPk(decoded.id);
    if (!user) {
      throw  Errors.notFound('User not found');
    }

    // 4. Attach user to request
    req.user = {
      id: user.id,
      email: user.email,
      role: user.role
    };

    next();
  } catch (error) {
    // Handle specific JWT errors
    if (error.name === 'JsonWebTokenError') {
      return next(Errors.unauthorized('Invalid token'));
    }
    if (error.name === 'TokenExpiredError') {
      return next(Errors.unauthenticated('Token expired. Please login again'));
    }
    next(error);
  }
};

export const authorize = (...roles) => {
  return (req, res, next) => {
    if (!roles.includes(req.user.role)) {
      throw Errors.unauthorized('Unauthorized to access this route');
    }
    next();
  };
};

export const requireAdmin = (req, res, next) => {
  console.log(req.user)
  if (req.user.role !== 'admin') {
    return res.status(403).send({ 
      error: 'Admin privileges required',
      message: 'You do not have permission to access this resource'
    });
  }
  next();
};

export const requireCustomer = (req, res, next) => {
  console.log(req.user)
  if (req.user.role === 'admin') {
    return res.status(403).send({ 
      error: 'customer privileges required',
      message: 'You do not have permission to access this resource'
    });
  }
  next();
};

