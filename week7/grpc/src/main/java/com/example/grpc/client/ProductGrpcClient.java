package com.example.grpc.client;

import com.example.productservice.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class ProductGrpcClient {

    private final ManagedChannel channel;
    private final ProductServiceGrpc.ProductServiceBlockingStub blockingStub;

    public ProductGrpcClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = ProductServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public ProductResponse createProduct(String name, String description, double price) {
        ProductRequest request = ProductRequest.newBuilder()
                .setName(name)
                .setDescription(description)
                .setPrice(price)
                .build();
        return blockingStub.createProduct(request);
    }

    public ProductResponse getProduct(int id) {
        ProductIdRequest request = ProductIdRequest.newBuilder()
                .setId(id)
                .build();
        return blockingStub.getProduct(request);
    }

    public ProductResponse updateProduct(int id, String name, String description, double price) {
        ProductRequest request = ProductRequest.newBuilder()
                .setId(id)
                .setName(name)
                .setDescription(description)
                .setPrice(price)
                .build();
        return blockingStub.updateProduct(request);
    }

    public DeleteResponse deleteProduct(int id) {
        ProductIdRequest request = ProductIdRequest.newBuilder()
                .setId(id)
                .build();
        return blockingStub.deleteProduct(request);
    }

    public ProductListResponse listProducts() {
        Empty request = Empty.newBuilder().build();
        return blockingStub.listProducts(request);
    }

    public static void main(String[] args) {
        ProductGrpcClient client = new ProductGrpcClient("localhost", 9090);

        try {
            // Test CRUD operations
            System.out.println("=== Testing gRPC Product Service ===\n");

            // 1. Create products
            System.out.println("1. Creating products...");
            ProductResponse product1 = client.createProduct("Laptop", "High-performance laptop", 1299.99);
            System.out.println("Created: " + product1);

            ProductResponse product2 = client.createProduct("Mouse", "Wireless mouse", 29.99);
            System.out.println("Created: " + product2);

            ProductResponse product3 = client.createProduct("Keyboard", "Mechanical keyboard", 89.99);
            System.out.println("Created: " + product3);

            // 2. List all products
            System.out.println("\n2. Listing all products...");
            ProductListResponse listResponse = client.listProducts();
            listResponse.getProductsList().forEach(product -> 
                System.out.println("  - " + product)
            );

            // 3. Get a specific product
            System.out.println("\n3. Getting product with id " + product1.getId() + "...");
            ProductResponse retrievedProduct = client.getProduct(product1.getId());
            System.out.println("Retrieved: " + retrievedProduct);

            // 4. Update a product
            System.out.println("\n4. Updating product with id " + product2.getId() + "...");
            ProductResponse updatedProduct = client.updateProduct(
                product2.getId(), 
                "Wireless Mouse Pro", 
                "Advanced wireless mouse with ergonomic design", 
                39.99
            );
            System.out.println("Updated: " + updatedProduct);

            // 5. List all products again
            System.out.println("\n5. Listing all products after update...");
            listResponse = client.listProducts();
            listResponse.getProductsList().forEach(product -> 
                System.out.println("  - " + product)
            );

            // 6. Delete a product
            System.out.println("\n6. Deleting product with id " + product3.getId() + "...");
            DeleteResponse deleteResponse = client.deleteProduct(product3.getId());
            System.out.println("Delete success: " + deleteResponse.getSuccess());

            // 7. List all products after deletion
            System.out.println("\n7. Listing all products after deletion...");
            listResponse = client.listProducts();
            listResponse.getProductsList().forEach(product -> 
                System.out.println("  - " + product)
            );

            System.out.println("\n=== All tests completed successfully! ===");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                client.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
