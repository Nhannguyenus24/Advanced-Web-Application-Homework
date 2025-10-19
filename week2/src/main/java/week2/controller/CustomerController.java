package week2.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import week2.dto.ApiResponse;
import week2.dto.CustomerDto;
import week2.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomers() {
        logger.info("GET /api/customers - Fetching all customers");
        List<CustomerDto> customers = customerService.getAllCustomers();
        logger.debug("Returning {} customers", customers.size());
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable Integer id) {
        logger.info("GET /api/customers/{} - Fetching customer by id", id);
        CustomerDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customer));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        logger.info("POST /api/customers - Creating new customer");
        CustomerDto createdCustomer = customerService.createCustomer(customerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully", createdCustomer));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(
            @PathVariable Integer id,
            @Valid @RequestBody CustomerDto customerDto) {
        logger.info("PUT /api/customers/{} - Updating customer", id);
        CustomerDto updatedCustomer = customerService.updateCustomer(id, customerDto);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", updatedCustomer));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Integer id) {
        logger.info("DELETE /api/customers/{} - Deleting customer", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> searchCustomers(@RequestParam String keyword) {
        logger.info("GET /api/customers/search?keyword={} - Searching customers", keyword);
        List<CustomerDto> customers = customerService.searchCustomers(keyword);
        logger.debug("Found {} customers matching keyword", customers.size());
        return ResponseEntity.ok(ApiResponse.success("Customers found", customers));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getActiveCustomers() {
        logger.info("GET /api/customers/active - Fetching active customers");
        List<CustomerDto> customers = customerService.getActiveCustomers();
        logger.debug("Found {} active customers", customers.size());
        return ResponseEntity.ok(ApiResponse.success("Active customers retrieved successfully", customers));
    }
}

