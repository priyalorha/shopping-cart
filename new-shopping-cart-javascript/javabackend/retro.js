import axios from 'axios'; // Install via: npm install axios


export const JAVA_BILL_API = async (data) => {
    const url = `http://localhost:8080/api/bill`;
  
  try {
    const response = await axios.post(url, data);
    console.log('API Success:', response.data);
    return response.data; // Return just the response data
  } catch (error) {
    console.error('API Error:', error.response?.data || error.message);
    throw error; // Re-throw to let the caller handle it
  }
};