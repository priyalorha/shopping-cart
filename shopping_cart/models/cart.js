import { Model, DataTypes } from 'sequelize';

export default class Cart extends Model {
  static associate(models) {
    this.belongsTo(models.User, { 
      foreignKey: 'userId', 
      as: 'user',
      onDelete: 'CASCADE',
      onUpdate: 'CASCADE'
    });
    this.hasMany(models.CartItem, { 
      foreignKey: 'cartId', 
      as: 'cartItems',
      onDelete: 'CASCADE'
    });
  }
}

export function initCart(sequelize) {
  Cart.init({
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
    status: {
      type: DataTypes.ENUM('OPEN', 'CLOSED'),
      defaultValue: 'OPEN',
      allowNull: false,
    },
    total: {
      type: DataTypes.DECIMAL(10, 2),
      defaultValue: 0,
      allowNull: false,
      validate: {
        min: 0
      }
    },
    quantity: {
      type: DataTypes.INTEGER,
      defaultValue: 0,
      allowNull: false,
      validate: {
        min: 0
      }
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
  }, {
    sequelize,
    modelName: 'Cart',
    tableName: 'carts',
    timestamps: true,
    underscored: false,
    paranoid: false
  });

  return Cart;
}