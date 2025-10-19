package week2.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmDto {
    
    private Integer filmId;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    private String description;
    
    @Min(value = 1888, message = "Release year must be at least 1888")
    @Max(value = 2100, message = "Release year must not exceed 2100")
    private Integer releaseYear;
    
    @NotNull(message = "Language ID is required")
    private Integer languageId;
    
    private Integer originalLanguageId;
    
    @NotNull(message = "Rental duration is required")
    @Min(value = 1, message = "Rental duration must be at least 1 day")
    private Integer rentalDuration;
    
    @NotNull(message = "Rental rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rental rate must be greater than 0")
    private BigDecimal rentalRate;
    
    @Min(value = 1, message = "Length must be at least 1 minute")
    private Integer length;
    
    @NotNull(message = "Replacement cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Replacement cost must be greater than 0")
    private BigDecimal replacementCost;
    
    @Size(max = 10, message = "Rating must not exceed 10 characters")
    private String rating;
    
    private String specialFeatures;
}

