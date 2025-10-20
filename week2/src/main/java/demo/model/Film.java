package demo.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Film {
    private Integer filmId;
    private String title;
    private String description;
    private Short releaseYear;
    private Short languageId;
    private Short originalLanguageId;
    private Short rentalDuration = 3;
    private double rentalRate = 4.99;
    private Integer length;
    private double replacementCost = 19.99;
    private Rating rating = Rating.G;
    private Set<SpecialFeature> specialFeatures;
    private LocalDateTime lastUpdate;

    @Getter
    public enum Rating {
        G("G"),
        PG("PG"),
        PG_13("PG-13"),
        R("R"),
        NC_17("NC-17");

        private final String label;

        Rating(String label) {
            this.label = label;
        }
    }

    @Getter
    public enum SpecialFeature {
        TRAILERS("Trailers"),
        COMMENTARIES("Commentaries"),
        DELETED_SCENES("Deleted Scenes"),
        BEHIND_THE_SCENES("Behind the Scenes");

        private final String label;

        SpecialFeature(String label) {
            this.label = label;
        }

    }
}
