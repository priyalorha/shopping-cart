// controllers/cartController.js
import models, { sequelize } from "../models/index.js";
import { validFruits } from "../constants.js";
import { z } from "zod";
import { JAVA_BILL_API } from "../javabackend/retro.js";
import { NUMBER } from "sequelize";
import { number } from "zod/v3";

const { Cart, CartItem } = models;

// --- Utils ---
const storeCartItems = async (fruits, cartId, userId, transaction) => {
  // First delete existing items for this cart
  await CartItem.destroy({
    where: { cartId, userId },
    transaction
  });

  // Then create new items
  return Promise.all(
  fruits.map(async (fruit) => {
    try {
      // Validate required fields
      if (!fruit.fruit || !validFruits.includes(fruit.fruit)) {
        throw new Error(`Invalid fruit: ${fruit.fruit}`);
      }
      console.log(fruit.fruit)
      console.log(fruit.avgPrice)
      console.log(fruit.charged)
      const discount = fruit.unitPrice*fruit.quantity - fruit.avgPrice*fruit.quantity

      const cartIdz = Number(cartId)
      console.log(cartId, Number(cartId))
      console.log(cartIdz)
      console.log( {
          name: fruit.fruit,
          offer: fruit.offer || 'NONE',
          price: fruit.unitPrice,
          quantity: fruit.quantity,
          avg: fruit.avgPrice,
          cost: fruit.charged,
          discount: discount > 0 ? discount : 0, // Ensure non-negative
          total: fruit.avgPrice*fruit.quantity,
          userId:userId,
          cartId:cartIdz,
        })

        
      // Create cart item with transaction
      return await CartItem.create(
        {
          name: fruit.fruit,
          offer: fruit.offer || 'NONE',
          price: fruit.unitPrice,
          quantity: fruit.quantity,
          avgPrice: fruit.avgPrice,
          charged: fruit.charged,
          discount: discount > 0 ? discount : 0, // Ensure non-negative
          total: fruit.avgPrice*fruit.quantity,
          userId: userId,
         cartId: cartIdz
        },
        { transaction }
      );
    } catch (error) {
      console.error(`Error creating cart item for ${fruit.fruit}:`, error);
      throw error; // Re-throw to fail the entire operation
    }
  })
);
};

// --- Validation ---
export const cartItemSchema = z.object({
  item: z.enum(validFruits, {
    required_error: "Item is required",
    invalid_type_error: "Item must be a valid fruit name",
  }),
});

// --- Controller ---
export const cartController = {
  // Create a new cart
  async createCart(req, res, next) {
    try {
      // Check for existing OPEN cart
      const existingCart = await Cart.findOne({
        where: {
          userId: req.user.id,
          status: 'OPEN'
        }
      });

      if (existingCart) {
        return res.status(422).json({ 
          message: "User already has an active cart",
          cartId: existingCart.id
        });
      }

      // Create new cart
      const cart = await Cart.create({
        userId: req.user.id,
        status: 'OPEN',
        total: 0,
        quantity: 0
      });
      
      res.status(201).json(cart);
    } catch (error) {
      next(error);
    }
  },

  // Get user's active cart
  async getActiveCart(req, res, next) {
    try {
      const cart = await Cart.findOne({
        where: { userId: req.user.id, status: "OPEN" },
        include: [{ model: CartItem, as: "items" }],
      });

      if (!cart) {
        return res.status(404).json({ message: "No active cart found" });
      }

      res.json(cart);
    } catch (error) {
      next(error);
    }
  },

  // Get items for a cartId
  async getItems(req, res, next) {
    try {
      // Verify cart belongs to user
      const cart = await Cart.findOne({
        where: { 
          id: req.params.cartId,
          userId: req.user.id 
        }
      });

      if (!cart) {
        return res.status(404).json({ message: "Cart not found" });
      }

      const cartItems = await CartItem.findAll({
        where: { cartId: req.params.cartId },
      });

      const fruitArray = cartItems.flatMap((item) =>
        Array(item.quantity).fill(item.name)
      );

      res.status(200).json(fruitArray);
    } catch (error) {
      next(error);
    }
  },

  // Add item to cart
  async addCartItem(req, res, next) {
    const items = cartItemSchema.safeParse(req.body);

    if (!items.success) {
      return res.status(400).json({ error: items.error.flatten() });
    }

    const transaction = await sequelize.transaction();
    
    try {
      // Verify cart belongs to user and is open
      const cart = await Cart.findOne({
        where: { 
          id: req.params.cartId,
          userId: req.user.id,
          status: 'OPEN'
        },
        transaction
      });

      if (!cart) {
        await transaction.rollback();
        return res.status(404).json({ message: "Active cart not found" });
      }

      // Get current items
      const cartItems = await CartItem.findAll({
        where: { cartId: req.params.cartId },
        transaction,
        raw: true
      });

      let fruitArray = cartItems.flatMap((item) =>
        Array(item.quantity).fill(item.name)
      );

      // Add new item
      fruitArray.push(items.data.item);

      // Call Java Billing API
      const billingResult = await JAVA_BILL_API(fruitArray);

      // Store in DB
      const createdItems = await storeCartItems(
        billingResult.fruits, 
        req.params.cartId, 
        req.user.id, 
        transaction
      );

      // Update cart totals
      const total = billingResult.fruits.reduce((sum, fruit) => sum + parseFloat(fruit.charged), 0);
      const quantity = billingResult.fruits.reduce((sum, fruit) => sum + fruit.quantity, 0);
      
      await cart.update({
        total,
        quantity
      }, { transaction });

      await transaction.commit();

      res.status(201).json({
        message: `${createdItems.length} items added to cart`,
        items: createdItems,
        cart: {
          id: cart.id,
          total,
          quantity
        }
      });
    } catch (error) {
      await transaction.rollback();
      next(error);
    }
  },

  // Generate bill and close cart
  async bill(req, res, next) {
    const transaction = await sequelize.transaction();
    
    try {
      const cart = await Cart.findOne({
        where: { 
          userId: req.user.id, 
          status: "OPEN" 
        },
        include: [{ model: CartItem, as: "items" }],
        transaction
      });

      if (!cart) {
        await transaction.rollback();
        return res.status(404).json({ message: "No active cart found" });
      }

      // Calculate totals
      const totalCost = cart.items.reduce(
        (sum, item) => sum + Number(item.total || 0),
        0
      );
      const totalQuantity = cart.items.reduce(
        (sum, item) => sum + Number(item.quantity || 0),
        0
      );

      // Close cart
      await cart.update(
        { 
          status: "CLOSED",
          total: totalCost,
          quantity: totalQuantity
        },
        { transaction }
      );

      await transaction.commit();

      res.json({
        items: cart.items,
        grand_total: totalCost,
        total_quantity: totalQuantity,
      });
    } catch (error) {
      await transaction.rollback();
      next(error);
    }
  },

  // Clear cart items
  async clearCart(req, res, next) {
    const transaction = await sequelize.transaction();
    
    try {
      const cart = await Cart.findOne({
        where: { 
          id: req.params.cartId,
          userId: req.user.id,
          status: 'OPEN'
        },
        transaction
      });

      if (!cart) {
        await transaction.rollback();
        return res.status(404).json({ message: "Active cart not found" });
      }

      // Delete all items
      await CartItem.destroy({
        where: { cartId: req.params.cartId },
        transaction
      });

      // Reset cart totals
      await cart.update({
        total: 0,
        quantity: 0
      }, { transaction });

      await transaction.commit();

      res.status(200).json({ 
        message: "Cart cleared successfully",
        cart: {
          id: cart.id,
          total: 0,
          quantity: 0
        }
      });
    } catch (error) {
      await transaction.rollback();
      next(error);
    }
  }
};