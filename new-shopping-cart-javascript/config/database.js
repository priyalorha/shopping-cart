import dotenv from "dotenv";
import { Sequelize } from "sequelize";

dotenv.config();

export const sequelize = new Sequelize({
  dialect: "sqlite",
  storage: process.env.SQLITE_STORAGE || "../database.sqlite",
  logging: false, // set true to see SQL logs
});

export const connectToDatabase = async () => {
  try {
    await sequelize.authenticate();
    console.log("✅ Database connected");

    await sequelize.sync({ alter: true }); // safe sync
    console.log("✅ Models synchronized");
  } catch (error) {
    console.error("❌ Database connection error:", error);
    throw error;
  }
};
