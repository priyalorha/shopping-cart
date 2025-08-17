import express from 'express';
import {
  
  login,
  getMe
} from '../controllers/authController.js';
import { authenticate } from '../middlewares/auth.middleware.js';
import { authLimiter } from '../middlewares/rateLimiter.middleware.js';

const router = express.Router();

router.post('/login', authLimiter, login);

// Protected routes (require authentication)
router.use(authenticate);

router.get('/me', getMe);

export default router;