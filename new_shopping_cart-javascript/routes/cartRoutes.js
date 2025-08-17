// routes/cartRoutes.js
import express from "express";
import { cartController } from "../controllers/cartController.js";
import { authenticate, requireCustomer } from '../middlewares/auth.middleware.js';


const router = express.Router();

// Protect all cart routes
router.use(authenticate);
router.use(requireCustomer);

// Cart routes
router.post("/", cartController.createCart);
router.get("/", cartController.getActiveCart);

router.patch("/:cartId/generateBill", cartController.bill);

// Cart item routes
router.get("/:cartId", cartController.getItems);
router.post("/:cartId/items", cartController.addCartItem);


export default router;