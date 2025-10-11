package demo.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Film {
    private int filmId;
    private String title;
    private int languageId;
    private int releaseYear;
}
