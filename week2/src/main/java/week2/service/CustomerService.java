package week2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import week2.dto.CustomerDto;
import week2.entity.Customer;
import week2.exception.ResourceNotFoundException;
import week2.repository.CustomerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public List<CustomerDto> getAllCustomers() {
        logger.info("Fetching all customers from database");
        List<Customer> customers = customerRepository.findAll();
        logger.debug("Found {} customers", customers.size());
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public CustomerDto getCustomerById(Integer id) {
        logger.info("Fetching customer with id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer not found with id: {}", id);
                    return new ResourceNotFoundException("Customer", "id", id);
                });
        logger.debug("Found customer: {} {}", customer.getFirstName(), customer.getLastName());
        return convertToDto(customer);
    }
    
    public CustomerDto createCustomer(CustomerDto customerDto) {
        logger.info("Creating new customer: {} {}", customerDto.getFirstName(), customerDto.getLastName());
        Customer customer = convertToEntity(customerDto);
        customer.setCreateDate(LocalDateTime.now());
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Successfully created customer with id: {}", savedCustomer.getCustomerId());
        return convertToDto(savedCustomer);
    }
    
    public CustomerDto updateCustomer(Integer id, CustomerDto customerDto) {
        logger.info("Updating customer with id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer not found with id: {}", id);
                    return new ResourceNotFoundException("Customer", "id", id);
                });
        
        customer.setStoreId(customerDto.getStoreId());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setEmail(customerDto.getEmail());
        customer.setAddressId(customerDto.getAddressId());
        customer.setActive(customerDto.getActive());
        
        Customer updatedCustomer = customerRepository.save(customer);
        logger.info("Successfully updated customer with id: {}", id);
        return convertToDto(updatedCustomer);
    }
    
    public void deleteCustomer(Integer id) {
        logger.info("Deleting customer with id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer not found with id: {}", id);
                    return new ResourceNotFoundException("Customer", "id", id);
                });
        customerRepository.delete(customer);
        logger.info("Successfully deleted customer with id: {}", id);
    }
    
    public List<CustomerDto> searchCustomers(String keyword) {
        logger.info("Searching customers with keyword: {}", keyword);
        List<Customer> customers = customerRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
        logger.debug("Found {} customers matching keyword: {}", customers.size(), keyword);
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<CustomerDto> getActiveCustomers() {
        logger.info("Fetching active customers");
        List<Customer> customers = customerRepository.findByActive(true);
        logger.debug("Found {} active customers", customers.size());
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private CustomerDto convertToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(customer.getCustomerId());
        dto.setStoreId(customer.getStoreId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setAddressId(customer.getAddressId());
        dto.setActive(customer.getActive());
        return dto;
    }
    
    private Customer convertToEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setStoreId(dto.getStoreId());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setAddressId(dto.getAddressId());
        customer.setActive(dto.getActive());
        return customer;
    }
}

