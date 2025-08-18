import { Model, DataTypes } from "sequelize";
import bcrypt from "bcrypt";

export default class User extends Model {
  static associate(models) {
    User.hasMany(models.Cart, { foreignKey: "userId", as: "carts" });
    User.hasMany(models.CartItem, { foreignKey: "userId", as: "cartItems" });
  }

  async validPassword(password) {
    return await bcrypt.compare(password, this.password);
  }
}

export function initUser(sequelize) {
  User.init(
    {
      name: { type: DataTypes.STRING, allowNull: false },
      email: {
        type: DataTypes.STRING,
        allowNull: false,
        unique: true,
        validate: { isEmail: true },
      },
      password: { type: DataTypes.STRING, allowNull: false },
      role: {
        type: DataTypes.ENUM("admin", "customer"),
        allowNull: false,
        defaultValue: "customer",
      },
    },
    {
      sequelize,
      modelName: "User",
      hooks: {
        async beforeCreate(user) {
          if (user.password) {
            const salt = await bcrypt.genSalt(10);
            user.password = await bcrypt.hash(user.password, salt);
          }
        },
        async beforeUpdate(user) {
          if (user.changed("password")) {
            const salt = await bcrypt.genSalt(10);
            user.password = await bcrypt.hash(user.password, salt);
          }
        },
      },
    }
  );
  return User;
}
