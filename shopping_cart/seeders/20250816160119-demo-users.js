"use strict";

import bcrypt from "bcrypt";

/** @type {import('sequelize-cli').Migration} */
export async function up(queryInterface, Sequelize) {
  const hashedUserPassword = await bcrypt.hash("user@123", 10);
  const hashedAdminPassword = await bcrypt.hash("admin@123", 10);

  await queryInterface.bulkInsert("Users", [
    {
      name: "Priya Lorha",
      email: "priya.lorha@gmail.com",
      password: hashedUserPassword,
      role: "customer",
      createdAt: new Date(),
      updatedAt: new Date(),
    },
    {
      name: "Store Admin",
      email: "admin@store.com",
      password: hashedAdminPassword,
      role: "admin",
      createdAt: new Date(),
      updatedAt: new Date(),
    },
  ]);
}

export async function down(queryInterface, Sequelize) {
  await queryInterface.bulkDelete("Users", {
    email: ["priya.lorha@gmail.com", "admin@store.com"],
  });
}
