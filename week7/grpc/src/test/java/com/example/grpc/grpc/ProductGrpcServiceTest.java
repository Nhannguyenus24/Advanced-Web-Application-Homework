package com.example.grpc.grpc;

import com.example.grpc.model.Product;
import com.example.grpc.service.ProductServiceImpl;
import com.example.productservice.grpc.*;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductGrpcServiceTest {

    @Mock
    private ProductServiceImpl productService;

    @InjectMocks
    private ProductGrpcService productGrpcService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(1, "Test Product", "Test Description", 99.99);
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        ProductRequest request = ProductRequest.newBuilder()
                .setName("Test Product")
                .setDescription("Test Description")
                .setPrice(99.99)
                .build();

        StreamRecorder<ProductResponse> responseObserver = StreamRecorder.create();
        productGrpcService.createProduct(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        assertNull(responseObserver.getError());
        List<ProductResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        ProductResponse response = results.get(0);
        assertEquals(1, response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(99.99, response.getPrice(), 0.01);
    }

    @Test
    void testGetProduct() throws Exception {
        when(productService.getProduct(1)).thenReturn(Optional.of(testProduct));

        ProductIdRequest request = ProductIdRequest.newBuilder()
                .setId(1)
                .build();

        StreamRecorder<ProductResponse> responseObserver = StreamRecorder.create();
        productGrpcService.getProduct(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        assertNull(responseObserver.getError());
        List<ProductResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        ProductResponse response = results.get(0);
        assertEquals(1, response.getId());
        assertEquals("Test Product", response.getName());
    }

    @Test
    void testGetProductNotFound() throws Exception {
        when(productService.getProduct(999)).thenReturn(Optional.empty());

        ProductIdRequest request = ProductIdRequest.newBuilder()
                .setId(999)
                .build();

        StreamRecorder<ProductResponse> responseObserver = StreamRecorder.create();
        productGrpcService.getProduct(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        assertNotNull(responseObserver.getError());
        assertTrue(responseObserver.getError().getMessage().contains("Product not found"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        when(productService.updateProduct(any(Product.class))).thenReturn(testProduct);

        ProductRequest request = ProductRequest.newBuilder()
                .setId(1)
                .setName("Updated Product")
                .setDescription("Updated Description")
                .setPrice(149.99)
                .build();

        StreamRecorder<ProductResponse> responseObserver = StreamRecorder.create();
        productGrpcService.updateProduct(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        assertNull(responseObserver.getError());
        List<ProductResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
    }

    @Test
    void testDeleteProduct() throws Exception {
        when(productService.deleteProduct(1)).thenReturn(true);

        ProductIdRequest request = ProductIdRequest.newBuilder()
                .setId(1)
                .build();

        StreamRecorder<DeleteResponse> responseObserver = StreamRecorder.create();
        productGrpcService.deleteProduct(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        assertNull(responseObserver.getError());
        List<DeleteResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        assertTrue(results.get(0).getSuccess());
    }

    @Test
    void testListProducts() throws Exception {
        Product product2 = new Product(2, "Product 2", "Description 2", 199.99);
        List<Product> products = Arrays.asList(testProduct, product2);

        when(productService.listAllProducts()).thenReturn(products);

        Empty request = Empty.newBuilder().build();

        StreamRecorder<ProductListResponse> responseObserver = StreamRecorder.create();
        productGrpcService.listProducts(request, responseObserver);

        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }

        assertNull(responseObserver.getError());
        List<ProductListResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());

        ProductListResponse response = results.get(0);
        assertEquals(2, response.getProductsCount());
    }
}
