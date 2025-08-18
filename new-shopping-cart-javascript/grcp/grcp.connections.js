import grpc from "@grpc/grpc-js";
import protoLoader from "@grpc/proto-loader";
import path from "path";
import { fileURLToPath } from "url";

// Needed for __dirname in ES modules
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const PROTO_PATH = path.join(__dirname, "cart_service.proto");

// Load proto
const packageDefinition = protoLoader.loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true,
});


// Create gRPC client

const cartProto = grpc.loadPackageDefinition(packageDefinition);

// Create client
export const client = new cartProto.CartService(
  'localhost:50051',
  grpc.credentials.createInsecure()
);
