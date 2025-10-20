package demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Actor response")
public class ActorResponse {
    
    @Schema(description = "Actor ID", example = "1")
    private int actorId;
    
    @Schema(description = "Actor's first name", example = "Tom")
    private String firstName;
    
    @Schema(description = "Actor's last name", example = "Cruise")
    private String lastName;
    
    @Schema(description = "Last update timestamp", example = "2023-10-08T10:30:00")
    private LocalDateTime lastUpdate;
}