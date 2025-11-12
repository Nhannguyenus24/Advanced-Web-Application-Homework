package com.example.grpc.service;

import com.example.grpc.model.Product;
import com.example.grpc.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(1, "Test Product", "Test Description", 99.99);
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.createProduct(testProduct);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.getProduct(1);

        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getName());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void testUpdateProduct() {
        when(productRepository.existsById(1)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.updateProduct(testProduct);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).existsById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() {
        when(productRepository.existsById(1)).thenReturn(false);

        Product result = productService.updateProduct(testProduct);

        assertNull(result);
        verify(productRepository, times(1)).existsById(1);
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.existsById(1)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1);

        boolean result = productService.deleteProduct(1);

        assertTrue(result);
        verify(productRepository, times(1)).existsById(1);
        verify(productRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.existsById(1)).thenReturn(false);

        boolean result = productService.deleteProduct(1);

        assertFalse(result);
        verify(productRepository, times(1)).existsById(1);
        verify(productRepository, times(0)).deleteById(1);
    }

    @Test
    void testListAllProducts() {
        Product product2 = new Product(2, "Product 2", "Description 2", 199.99);
        List<Product> products = Arrays.asList(testProduct, product2);

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.listAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }
}
