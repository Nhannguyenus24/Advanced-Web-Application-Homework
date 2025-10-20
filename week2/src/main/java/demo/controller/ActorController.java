package demo.controller;

import demo.dto.*;
import demo.service.ActorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = "Actors", description = "CRUD operations for actor management")
@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @Operation(
            summary = "Get all actors",
            description = "Returns a list of all actors, sorted by actor_id in ascending order",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "Successfully retrieved actors",
                            content = @Content(schema = @Schema(implementation = demo.dto.ApiResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<demo.dto.ApiResponse<List<ActorResponse>>> getAllActors() {
        List<ActorResponse> actors = actorService.getAllActors();
        return ResponseEntity.ok(demo.dto.ApiResponse.success("Successfully retrieved all actors", actors));
    }

    @Operation(
            summary = "Get actor by ID",
            description = "Returns a single actor by their ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "Successfully retrieved actor",
                            content = @Content(schema = @Schema(implementation = demo.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", 
                            description = "Actor not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<demo.dto.ApiResponse<ActorResponse>> getActorById(
            @Parameter(description = "Actor ID", example = "1")
            @PathVariable int id) {
        Optional<ActorResponse> actor = actorService.getActorById(id);
        
        if (actor.isPresent()) {
            return ResponseEntity.ok(demo.dto.ApiResponse.success("Actor found successfully", actor.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(demo.dto.ApiResponse.error("Actor not found with id: " + id));
        }
    }

    @Operation(
            summary = "Create new actor",
            description = "Creates a new actor with the provided information",
            requestBody = @RequestBody(
                    required = true,
                    description = "Actor information to create",
                    content = @Content(
                            schema = @Schema(implementation = ActorRequest.class),
                            examples = @ExampleObject(
                                    name = "NewActor",
                                    value = """
                            {
                              "firstName": "Tom",
                              "lastName": "Cruise"
                            }
                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", 
                            description = "Actor created successfully",
                            content = @Content(schema = @Schema(implementation = demo.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", 
                            description = "Invalid input data")
            }
    )
    @PostMapping
    public ResponseEntity<demo.dto.ApiResponse<ActorResponse>> createActor(
            @Valid @org.springframework.web.bind.annotation.RequestBody ActorRequest request) {
        try {
            ActorResponse newActor = actorService.createActor(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(demo.dto.ApiResponse.success("Actor created successfully", newActor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(demo.dto.ApiResponse.error("Failed to create actor: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Update actor (partial)",
            description = "Updates actor information using PATCH method",
            requestBody = @RequestBody(
                    required = true,
                    description = "Actor information to update",
                    content = @Content(
                            schema = @Schema(implementation = ActorUpdateRequest.class),
                            examples = @ExampleObject(
                                    name = "UpdateActor",
                                    value = """
                            {
                              "actorId": 1,
                              "firstName": "Thomas",
                              "lastName": "Cruise"
                            }
                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", 
                            description = "Actor updated successfully",
                            content = @Content(schema = @Schema(implementation = demo.dto.ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", 
                            description = "Actor not found")
            }
    )
    @PatchMapping
    public ResponseEntity<demo.dto.ApiResponse<ActorResponse>> updateActor(
            @Valid @org.springframework.web.bind.annotation.RequestBody ActorUpdateRequest request) {
        Optional<ActorResponse> updatedActor = actorService.updateActor(request);
        
        if (updatedActor.isPresent()) {
            return ResponseEntity.ok(demo.dto.ApiResponse.success("Actor updated successfully", updatedActor.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(demo.dto.ApiResponse.error("Actor not found with id: " + request.getActorId()));
        }
    }

    @Operation(
            summary = "Delete actor by ID",
            description = "Deletes an actor by their ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", 
                            description = "Actor deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", 
                            description = "Actor not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<demo.dto.ApiResponse<Void>> deleteActor(
            @Parameter(description = "Actor ID", example = "1")
            @PathVariable int id) {
        boolean deleted = actorService.deleteActor(id);
        
        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(demo.dto.ApiResponse.success("Actor deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(demo.dto.ApiResponse.error("Actor not found with id: " + id));
        }
    }
}
