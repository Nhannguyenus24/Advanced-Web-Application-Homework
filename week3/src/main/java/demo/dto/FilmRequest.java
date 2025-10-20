package demo.dto;

import demo.model.Film;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Film creation/update request")
public class FilmRequest {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Schema(description = "Title of the film", example = "The Matrix", required = true)
    private String title;

    @Schema(description = "Description or summary of the film", example = "A computer hacker learns about the true nature of reality.")
    private String description;

    @Min(value = 1900, message = "Release year must be >= 1900")
    @Max(value = 2100, message = "Release year must be <= 2100")
    @Schema(description = "Release year of the film", example = "1999")
    private Short releaseYear;

    @Positive(message = "Language ID must be positive")
    @Schema(description = "Language ID of the film (foreign key)", example = "1", required = true)
    private Short languageId;

    @PositiveOrZero(message = "Original language ID must be >= 0")
    @Schema(description = "Original language ID (nullable, foreign key)", example = "2")
    private Short originalLanguageId;

    @Schema(description = "Rental duration in days", example = "3", defaultValue = "3")
    private Short rentalDuration = 3;

    @DecimalMin(value = "0.00", message = "Rental rate must be non-negative")
    @Schema(description = "Rental rate price", example = "4.99", defaultValue = "4.99")
    private double rentalRate = 4.99;

    @Positive
    @Schema(description = "Film length in minutes", example = "120")
    private Integer length;

    @DecimalMin(value = "0.00", message = "Replacement cost must be non-negative")
    @Schema(description = "Replacement cost for lost or damaged film", example = "19.99", defaultValue = "19.99")
    private double replacementCost = 19.99;

    @NotNull
    @Schema(description = "Film rating (G, PG, PG_13, R, NC_17)", example = "PG_13", defaultValue = "G")
    private Film.Rating rating = Film.Rating.G;

    @Schema(description = "Special features available", example = "[\"TRAILERS\", \"DELETED_SCENES\"]")
    private Set<Film.SpecialFeature> specialFeatures;
}
