package com.example.grpc.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.grpc.model.Product;
import com.example.grpc.repository.ProductRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            // Clear existing data
            repository.deleteAll();

            // Insert sample products
            Product laptop = new Product();
            laptop.setName("Laptop");
            laptop.setDescription("High-performance laptop for developers");
            laptop.setPrice(1299.99);
            repository.save(laptop);

            Product mouse = new Product();
            mouse.setName("Wireless Mouse");
            mouse.setDescription("Ergonomic wireless mouse");
            mouse.setPrice(29.99);
            repository.save(mouse);

            Product keyboard = new Product();
            keyboard.setName("Mechanical Keyboard");
            keyboard.setDescription("RGB mechanical keyboard");
            keyboard.setPrice(89.99);
            repository.save(keyboard);

            Product monitor = new Product();
            monitor.setName("4K Monitor");
            monitor.setDescription("27-inch 4K UHD monitor");
            monitor.setPrice(399.99);
            repository.save(monitor);

            Product headset = new Product();
            headset.setName("Gaming Headset");
            headset.setDescription("7.1 surround sound gaming headset");
            headset.setPrice(79.99);
            repository.save(headset);

            System.out.println("✅ Sample data initialized: " + repository.count() + " products");
        };
    }
}
