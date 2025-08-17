import { Model, DataTypes } from "sequelize";

export const initCartItem = (sequelize) => {
  class CartItem extends Model {}

  CartItem.init(
    {
      id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
      },
      userId: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: 'users',
          key: 'id'
        }
      },
      cartId: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: 'carts',
          key: 'id'
        }
      },
      name: {
        type: DataTypes.STRING,
        allowNull: false,
        validate: {
          notEmpty: true
        }
      },
      price: {
        type: DataTypes.DECIMAL(10, 2),
        validate: {
          min: 0.0
        }
      },
      charged: {
        type: DataTypes.DECIMAL(10, 2),
        validate: {
          min: 0.0
        }
      },
      avgPrice: {
        type: DataTypes.DECIMAL(10, 2),
        validate: {
          min: 0.0
        }
      },

      quantity: {
        type: DataTypes.INTEGER,
        allowNull: false,
        defaultValue: 1,
        validate: {
          min: 1
        }
      },
      offerType: {
        type: DataTypes.STRING,
        defaultValue: "NONE"
      },
      createdAt: {
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: DataTypes.NOW
      },
      updatedAt: {
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: DataTypes.NOW
      }
    },
    {
      sequelize,
      modelName: "CartItem",
      tableName: "cart_items",
      timestamps: true,
      underscored: false,
      indexes: [
        {
          unique: true,
          fields: ["userId","cartId", "name"],
          name: "unique_cart_item"
        }
      ]
    }
  );

  return CartItem;
};

export const associateCartItem = (CartItem, models) => {
  CartItem.belongsTo(models.User, {
    foreignKey: "userId",
    as: "user",
    onDelete: "CASCADE",
    onUpdate: "CASCADE"
  });

  CartItem.belongsTo(models.Cart, {
    foreignKey: "cartId",
    as: "cart",
    onDelete: "CASCADE",
    onUpdate: "CASCADE"
  });
};