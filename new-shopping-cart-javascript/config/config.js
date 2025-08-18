import { dirname } from "path";
import { fileURLToPath } from "url";

const __dirname = dirname(fileURLToPath(import.meta.url));

export default {
  development: {
    dialect: "sqlite",
    storage: `${__dirname}/../database.sqlite`
  },
  test: {
    dialect: "sqlite",
    storage: ":memory:"
  },
  production: {
    dialect: "sqlite",
    storage: `${__dirname}/../database.sqlite`
  }
};
