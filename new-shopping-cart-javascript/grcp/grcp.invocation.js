
import { AppError, errorHandler } from '../middlewares/error.middleware.js';
import { client } from './grcp.connections.js'

export const calculateTotalFromGrpc = async function(itemsArray) {
  const request = {
    items: itemsArray.map(item => item.name.toLowerCase())
  };

  // Convert callback to Promise for async/await
  return new Promise((resolve, reject) => {
    client.ProcessFruits(request, (error, response) => {
      if (error) {
        reject (new AppError(error));
        return;
      } else {
        resolve(response);
      }
    });
  });
}