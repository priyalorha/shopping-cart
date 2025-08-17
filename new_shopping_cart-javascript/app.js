import express from "express";
import cookieParser from "cookie-parser";
import { PORT } from "./config/env.js";
import { connectToDatabase } from "./config/database.js";

import userRouter from "./routes/userRoutes.js";
import authRouter from "./routes/authRoutes.js";
import cartRouter from "./routes/cartRoutes.js";
// import orderRouter from "./routes/orderRoutes.js";

import { errorHandler } from "./middlewares/error.middleware.js";

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
// app.use(arcjetMiddleware);

app.use("/api/v1/auth", authRouter);
app.use("/api/v1/users", userRouter);
app.use("/api/v1/cart", cartRouter);
// app.use("/api/v1/order", orderRouter);

app.use(errorHandler);

const startServer = async () => {
  try {
    await connectToDatabase();
    app.listen(PORT, () => {
      console.log(PORT)
      console.log(`🚀 Server running on http://localhost:${PORT}`);
    });
  } catch (error) {
    console.error("❌ Failed to start server:", error);
    process.exit(1);
  }
};


startServer();

export default app;
