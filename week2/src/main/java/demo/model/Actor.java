package demo.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Actor {
    private int actorId;
    private String firstName;
    private String lastName;
    private LocalDateTime lastUpdate;
}
