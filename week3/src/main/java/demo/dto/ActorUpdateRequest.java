package demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Update actor request")
public class ActorUpdateRequest {
    
    @Schema(description = "Actor ID", example = "1", required = true)
    private int actorId;
    
    @Schema(description = "Actor's first name", example = "Thomas")
    private String firstName;
    
    @Schema(description = "Actor's last name", example = "Cruise")
    private String lastName;
}