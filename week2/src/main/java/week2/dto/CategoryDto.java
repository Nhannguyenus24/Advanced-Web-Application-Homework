package week2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    
    private Integer categoryId;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 25, message = "Category name must not exceed 25 characters")
    private String name;
}

