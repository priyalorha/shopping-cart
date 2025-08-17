import { DataTypes } from 'sequelize';

export default {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.createTable('carts', {
      id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false,
      },
      userId: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: 'users',
          key: 'id',
        },
        onUpdate: 'CASCADE',
        onDelete: 'CASCADE',
      },
      charged: {
        type: DataTypes.DECIMAL(10, 2),
        allowNull: false,
        validate: {
          min: 0
        }
      },
      avgPrice: {
        type: DataTypes.DECIMAL(10, 2),
        allowNull: false,
        validate: {
          min: 0
        }
      },

      status: {
        type: DataTypes.ENUM('OPEN', 'CLOSED'),
        defaultValue: 'OPEN',
        allowNull: false,
      },
      total: {
        type: DataTypes.DECIMAL(10, 2),
        defaultValue: 0,
        allowNull: false,
      },
      quantity: {
        type: DataTypes.INTEGER,
        defaultValue: 0,
        allowNull: false,
      },
      createdAt: {
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal('CURRENT_TIMESTAMP'),
      },
      updatedAt: {
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: Sequelize.literal('CURRENT_TIMESTAMP'),
      },
    });

    // Add index for better performance on frequently queried fields
    await queryInterface.addIndex('carts', ['userId']);
    await queryInterface.addIndex('carts', ['status']);
  },

  down: async (queryInterface) => {
    await queryInterface.dropTable('carts');
  },
};