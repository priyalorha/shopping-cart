import jwt from 'jsonwebtoken';
import bcrypt from 'bcryptjs';
import models from "../models/index.js";
const { User, Cart, CartItem } = models;

import { Errors, catchAsync } from '../middlewares/errorHandler.js';
import { authLimiter } from '../middlewares/rateLimiter.middleware.js';

const generateToken = (user) => {
  return jwt.sign(
    {
      id: user.id,  // Changed from _id to id for consistency
      name: user.name,
      email: user.email,
      role: user.role
    },
    process.env.JWT_SECRET,
    { expiresIn: process.env.JWT_EXPIRES_IN || '1d' }  // Added fallback
  );
};

export const login = [
  authLimiter,
  async (req, res, next) => {
    try {
      const { email, password } = req.body;

      // 1. Validate input
      if (!email || !password) {
        throw Errors.badRequest('Email and password are required');
      }

      // 2. Find user with password
      const user = await User.findOne({ where: { email } });
      console.log(user)
    

      if (!user) {
        throw Errors.unauthenticated('Invalid credentials');
      }

      consol.log(user)

      // 3. Verify password
      const isValidPassword = await bcrypt.compare(password, user.password);
      if (!isValidPassword) {
        throw Errors.unauthenticated('Invalid credentials');
      }

      // 4. Generate token
      const token = generateToken(user);

      // 5. Set secure cookie
      res.cookie('token', token, {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        maxAge: 24 * 60 * 60 * 1000,  // 1 day
        sameSite: 'strict'
      });

      // 6. Send response
      res.status(200).json({
        success: true,
        token,
        user: {
          id: user.id,
          name: user.name,
          email: user.email,
          role: user.role
        }
      });

    } catch (error) {
      next(error);
    }
  }
];

export const getMe = async (req, res, next) => {
  try {
    // Assuming req.user is set by your auth middleware
    const user = await User.findByPk(req.user.id, {
      attributes: { exclude: ['password'] }  // Sequelize syntax
    });

    if (!user) {
      throw Errors.notFound('User not found');
    }

    res.status(200).json({
      success: true,
      data: { user }
    });

  } catch (error) {
    next(error);
  }
};