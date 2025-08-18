import { ValidationError } from "sequelize";
import { z } from "zod";
import rateLimit from "express-rate-limit";
import { authenticate, requireAdmin } from '../middlewares/auth.middleware.js';
import { errorHandler } from "../middlewares/error.middleware.js";
import models from "../models/index.js";
const { User, Cart, Order, CartItem } = models;
// Rate limiting configuration (adjust as needed)
export const userRateLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // limit each IP to 100 requests per windowMs
  message: {
    error: "Too many requests",
    details: "Please try again later"
  }
});

// Zod validation schemas
const userCreateSchema = z.object({
  name: z.string().min(2).max(50),
  email: z.string().email(),
  password: z.string().min(8).max(100),
  role: z.enum(['admin', 'customer']).optional().default('user')
});

const userUpdateSchema = z.object({
  name: z.string().min(2).max(50).optional(),
  email: z.string().email().optional(),
  password: z.string().min(8).max(100).optional(),
  role: z.enum(['admin', 'customer']).optional()
}).refine(data => Object.keys(data).length > 0, {
  message: "At least one field must be provided for update"
});

const paginationSchema = z.object({
  page: z.coerce.number().int().positive().default(1),
  limit: z.coerce.number().int().positive().max(100).default(10)
});

// Create User
// Create User (Admin only)
export const createUser = [
  async (req, res, next) => {
    try {
      console.log(req.body)
      const validatedData = userCreateSchema.parse(req.body);
      const user = await User.create(validatedData);
      
      res.status(201).json({
        id: user.id,
        name: user.name,
        email: user.email,
        role: user.role,
        createdAt: user.createdAt
      });
    } catch (error) {
      next(error)
    }
  }
];

// Get all Users with pagination (Admin only)
export const getUsers = [
  requireAdmin,
  async (req, res, next) => {
    try {
      const { page, limit, search } = paginationSchema.parse(req.query);
      const offset = (page - 1) * limit;
      
      const where = {};
      if (search) {
        where[Op.or] = [
          { name: { [Op.iLike]: `%${search}%` } },
          { email: { [Op.iLike]: `%${search}%` } }
        ];
      }
      
      const { count, rows: users } = await User.findAndCountAll({
        attributes: { exclude: ['password'] },
        where,
        limit,
        offset,
        order: [['createdAt', 'DESC']]
      });
      
      res.json({
        data: users,
        meta: {
          total: count,
          page,
          limit,
          totalPages: Math.ceil(count / limit)
        }
      });
    } catch (error) {
      next(error);
    }
  }
];

// Get User by ID (Admin or own profile)
export const getUserById = async (req, res) => {
  try {
    const user = await User.findByPk(req.params.id, {
      attributes: { exclude: ['password'] }
    });
    
    if (!user) {
      return res.status(404).json({ 
        error: "Not found", 
        details: `User with ID ${req.params.id} not found` 
      });
    }
    
    // Authorization check
    if (req.user.role !== 'admin' && req.user.id !== user.id) {
      return res.status(403).json({
        error: "Forbidden",
        details: "You don't have permission to access this resource"
      });
    }
    
    res.json(user);
  } catch (error) {
    next(error);
  }
};

// Update User (Admin or own profile)
export const updateUser = async (req, res) => {
  try {
    const user = await User.findByPk(req.params.id);
    if (!user) {
      return res.status(404).json({ 
        error: "Not found", 
        details: `User with ID ${req.params.id} not found` 
      });
    }
    
    // Authorization check
    if (req.user.role !== 'admin' && req.user.id !== user.id) {
      return res.status(403).json({
        error: "Forbidden",
        details: "You don't have permission to update this user"
      });
    }
    
    // Prevent non-admins from changing roles
    const updateData = { ...req.body };
    if (req.user.role !== 'admin' && 'role' in updateData) {
      return res.status(403).json({
        error: "Forbidden",
        details: "Only admins can change user roles"
      });
    }
    
    const validatedData = userUpdateSchema.parse(updateData);
    await user.update(validatedData);
    
    res.json({
      id: user.id,
      name: user.name,
      email: user.email,
      role: user.role,
      updatedAt: user.updatedAt
    });
  } catch (error) {
    next(error);
  }
};

// Delete User (Admin only)
export const deleteUser = [
  requireAdmin,
  async (req, res) => {
    try {
      const user = await User.findByPk(req.params.id);
      if (!user) {
        return res.status(404).json({ 
          error: "Not found", 
          details: `User with ID ${req.params.id} not found` 
        });
      }
      
      // Prevent admin from deleting themselves
      if (req.user.id === user.id) {
        return res.status(403).json({
          error: "Forbidden",
          details: "Admins cannot delete their own accounts"
        });
      }
      
      await user.destroy();
      res.status(204).end();
    } catch (error) {
      next(error);
    }
  }
];