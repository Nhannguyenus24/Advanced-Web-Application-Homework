package com.example.grpc.grpc;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.grpc.model.Product;
import com.example.grpc.service.ProductServiceImpl;
import com.example.productservice.grpc.DeleteResponse;
import com.example.productservice.grpc.Empty;
import com.example.productservice.grpc.ProductIdRequest;
import com.example.productservice.grpc.ProductListResponse;
import com.example.productservice.grpc.ProductRequest;
import com.example.productservice.grpc.ProductResponse;
import com.example.productservice.grpc.ProductServiceGrpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    @Autowired
    private ProductServiceImpl productService;

    @Override
    public void createProduct(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        Product savedProduct = productService.createProduct(product);

        ProductResponse response = ProductResponse.newBuilder()
                .setId(savedProduct.getId())
                .setName(savedProduct.getName())
                .setDescription(savedProduct.getDescription())
                .setPrice(savedProduct.getPrice())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProduct(ProductIdRequest request, StreamObserver<ProductResponse> responseObserver) {
        Optional<Product> productOpt = productService.getProduct(request.getId());

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            ProductResponse response = ProductResponse.newBuilder()
                    .setId(product.getId())
                    .setName(product.getName())
                    .setDescription(product.getDescription())
                    .setPrice(product.getPrice())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Product not found with id: " + request.getId()));
        }
    }

    @Override
    public void updateProduct(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product product = new Product();
        product.setId(request.getId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        Product updatedProduct = productService.updateProduct(product);

        if (updatedProduct != null) {
            ProductResponse response = ProductResponse.newBuilder()
                    .setId(updatedProduct.getId())
                    .setName(updatedProduct.getName())
                    .setDescription(updatedProduct.getDescription())
                    .setPrice(updatedProduct.getPrice())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Product not found with id: " + request.getId()));
        }
    }

    @Override
    public void deleteProduct(ProductIdRequest request, StreamObserver<DeleteResponse> responseObserver) {
        boolean success = productService.deleteProduct(request.getId());

        DeleteResponse response = DeleteResponse.newBuilder()
                .setSuccess(success)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listProducts(Empty request, StreamObserver<ProductListResponse> responseObserver) {
        List<Product> products = productService.listAllProducts();

        ProductListResponse.Builder responseBuilder = ProductListResponse.newBuilder();

        for (Product product : products) {
            ProductResponse productResponse = ProductResponse.newBuilder()
                    .setId(product.getId())
                    .setName(product.getName())
                    .setDescription(product.getDescription())
                    .setPrice(product.getPrice())
                    .build();
            responseBuilder.addProducts(productResponse);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
