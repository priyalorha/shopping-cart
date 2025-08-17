import { Sequelize } from "sequelize";
import { DB_URI } from "../config/env.js";

// Import initialization functions only (no model classes)
import { initUser } from "./user.js";
import { initCart } from "./cart.js";
import { initCartItem } from "./cartItem.js";

const sequelize = new Sequelize(DB_URI, { dialect: "sqlite", logging: false });

// Initialize models in dependency order
const models = {
  User: initUser(sequelize),
  Cart: initCart(sequelize),
  CartItem: initCartItem(sequelize),
};

// Set up associations after all models are loaded
Object.values(models).forEach(model => {
  if (typeof model.associate === "function") {
    model.associate(models);
  }
});

// Verify connection and sync models
(async () => {
  try {
    await sequelize.authenticate();
    console.log("Database connection established successfully.");
    await sequelize.sync({ alter: true }); // Use { force: true } in development if needed
    console.log("All models were synchronized successfully.");
  } catch (error) {
    console.error("Unable to connect to the database:", error);
  }
})();

export { sequelize, models };
export default models;
