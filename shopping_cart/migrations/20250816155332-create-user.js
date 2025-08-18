"use strict";

export async function up(queryInterface, Sequelize) {
  await queryInterface.createTable("users", {  // Changed "Users" to "users" for consistency
    id: {
      allowNull: false,
      autoIncrement: true,
      primaryKey: true,
      type: Sequelize.INTEGER,
    },
    name: {
      type: Sequelize.STRING,
      allowNull: false,
    },
    email: {
      type: Sequelize.STRING,
      allowNull: false,
      unique: true,
    },
    password: {
      type: Sequelize.STRING,
      allowNull: false,
    },
    role: {
      type: Sequelize.ENUM("admin", "customer"),
      allowNull: false,
      defaultValue: "customer",
    },
    createdAt: {
      allowNull: false,
      type: Sequelize.DATE,
      defaultValue: Sequelize.literal('CURRENT_TIMESTAMP')  // Added default value
    },
    updatedAt: {
      allowNull: false,
      type: Sequelize.DATE,
      defaultValue: Sequelize.literal('CURRENT_TIMESTAMP')  // Added default value
    },
  });

  // Add index for email if not automatically created by unique constraint
  await queryInterface.addIndex('users', ['email'], {
    unique: true,
    name: 'users_email_unique'
  });
}

export async function down(queryInterface, Sequelize) {
  await queryInterface.dropTable("users");  // Changed to match createTable
}