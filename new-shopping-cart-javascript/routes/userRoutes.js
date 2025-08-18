import express from 'express';
import {
  createUser,
  getUsers,
  getUserById,
  updateUser,
  deleteUser,
  userRateLimiter
} from '../controllers/userController.js';
import { authenticate, authorize } from '../middlewares/auth.middleware.js';

const router = express.Router();

// Apply rate limiting to all user routes
router.use(userRateLimiter);

// Public routes
router.post('/', createUser);

// Protected routes (require authentication)
router.use(authenticate);

router.get('/', getUsers);
router.get('/:id', getUserById);
router.put('/:id', updateUser);
router.delete('/:id', deleteUser);

export default router;