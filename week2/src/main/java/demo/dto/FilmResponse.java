package demo.dto;

import demo.model.Film;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Film information returned in responses")
public class FilmResponse {

    @Schema(description = "Unique ID of the film", example = "1001")
    private Integer filmId;

    @Schema(description = "Title of the film", example = "The Matrix")
    private String title;

    @Schema(description = "Short description or plot summary", example = "A computer hacker learns the truth about reality.")
    private String description;

    @Schema(description = "Release year of the film", example = "1999")
    private Short releaseYear;

    @Schema(description = "Language ID (foreign key)", example = "1")
    private Short languageId;

    @Schema(description = "Original language ID (foreign key)", example = "2")
    private Short originalLanguageId;

    @Schema(description = "Rental duration in days", example = "3")
    private Short rentalDuration;

    @Schema(description = "Rental rate", example = "4.99")
    private double rentalRate;

    @Schema(description = "Length of the film in minutes", example = "136")
    private Integer length;

    @Schema(description = "Replacement cost if lost/damaged", example = "19.99")
    private double replacementCost;

    @Schema(description = "Rating (G, PG, PG_13, R, NC_17)", example = "R")
    private Film.Rating rating;

    @Schema(description = "Available special features", example = "[\"TRAILERS\", \"DELETED_SCENES\"]")
    private Set<Film.SpecialFeature> specialFeatures;

    @Schema(description = "Timestamp of last update", example = "2025-10-20T15:30:00")
    private LocalDateTime lastUpdate;
}
