'use strict';

export const up = async (queryInterface, Sequelize) => {
  await queryInterface.createTable('cart_items', {
    id: {
      type: Sequelize.INTEGER,
      primaryKey: true,
      autoIncrement: true,
      allowNull: false
    },
    userId: {
      type: Sequelize.INTEGER,
      allowNull: false,
      references: {
        model: 'users',
        key: 'id'
      },
      onDelete: 'CASCADE',
      onUpdate: 'CASCADE'
    },
    cartId: {
      type: Sequelize.INTEGER,
      allowNull: false,
      references: {
        model: 'carts',
        key: 'id'
      },
      onDelete: 'CASCADE',
      onUpdate: 'CASCADE'
    },
    name: {
      type: Sequelize.ENUM("melon", "lime", "banana", "apple"),
      allowNull: false
    },
    price: {
      type: Sequelize.DECIMAL(10, 2),
      allowNull: false,
      validate: {
        min: 0
      }
    },
    quantity: {
      type: Sequelize.INTEGER,
      allowNull: false,
      defaultValue: 1,
      validate: {
        min: 1
      }
    },
    offerType: {
      type: Sequelize.ENUM("NONE", "BOGO", "THREEFORTWO"),
      defaultValue: "NONE"
    },
    createdAt: {
      type: Sequelize.DATE,
      allowNull: false,
      defaultValue: Sequelize.literal('CURRENT_TIMESTAMP')
    },
    updatedAt: {
      type: Sequelize.DATE,
      allowNull: false,
      defaultValue: Sequelize.literal('CURRENT_TIMESTAMP')
    }
  });

  await queryInterface.addIndex('cart_items', ["userId","cartId", "name"], {
    unique: true,
    name: "unique_cart_item"
  });
};

export const down = async (queryInterface, Sequelize) => {
  await queryInterface.dropTable('cart_items');
};