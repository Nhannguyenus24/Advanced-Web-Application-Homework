package week2.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;
    
    @Column(name = "store_id", nullable = false)
    private Integer storeId;
    
    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;
    
    @Column(name = "email", length = 50)
    private String email;
    
    @Column(name = "address_id", nullable = false)
    private Integer addressId;
    
    @Column(name = "active", nullable = false)
    private Boolean active;
    
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;
    
    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
}

