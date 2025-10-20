package demo.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.dto.ActorRequest;
import demo.dto.ActorResponse;
import demo.dto.ActorUpdateRequest;
import demo.service.ActorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Actors", description = "CRUD operations for actor management")
@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;
    private final Logger logger = LoggerFactory.getLogger(ActorController.class);

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
        logger.info("GET /api/actors - Request: Get all actors");
        try {
            List<ActorResponse> actors = actorService.getAllActors();
            demo.dto.ApiResponse<List<ActorResponse>> response = demo.dto.ApiResponse.success("Successfully retrieved all actors", actors);
            logger.info("GET /api/actors - Response: Successfully retrieved {} actors", actors.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("GET /api/actors - Error retrieving all actors", e);
            throw e;
        }
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
        logger.info("GET /api/actors/{} - Request: Get actor by ID", id);
        try {
            Optional<ActorResponse> actor = actorService.getActorById(id);
            
            if (actor.isPresent()) {
                demo.dto.ApiResponse<ActorResponse> response = demo.dto.ApiResponse.success("Actor found successfully", actor.get());
                logger.info("GET /api/actors/{} - Response: Actor found - {}", id, actor.get());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("GET /api/actors/{} - Response: Actor not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(demo.dto.ApiResponse.error("Actor not found with id: " + id));
            }
        } catch (Exception e) {
            logger.error("GET /api/actors/{} - Error retrieving actor", id, e);
            throw e;
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
        logger.info("POST /api/actors - Request: {}", request);
        try {
            ActorResponse newActor = actorService.createActor(request);
            demo.dto.ApiResponse<ActorResponse> response = demo.dto.ApiResponse.success("Actor created successfully", newActor);
            logger.info("POST /api/actors - Response: Actor created successfully with ID {} - {}", newActor.getActorId(), newActor);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("POST /api/actors - Error creating actor with request: {}", request, e);
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
        logger.info("PATCH /api/actors - Request: {}", request);
        try {
            Optional<ActorResponse> updatedActor = actorService.updateActor(request);
            
            if (updatedActor.isPresent()) {
                demo.dto.ApiResponse<ActorResponse> response = demo.dto.ApiResponse.success("Actor updated successfully", updatedActor.get());
                logger.info("PATCH /api/actors - Response: Actor updated successfully - {}", updatedActor.get());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("PATCH /api/actors - Response: Actor not found with ID {}", request.getActorId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(demo.dto.ApiResponse.error("Actor not found with id: " + request.getActorId()));
            }
        } catch (Exception e) {
            logger.error("PATCH /api/actors - Error updating actor with request: {}", request, e);
            throw e;
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
        logger.info("DELETE /api/actors/{} - Request: Delete actor by ID", id);
        try {
            boolean deleted = actorService.deleteActor(id);
            
            if (deleted) {
                logger.info("DELETE /api/actors/{} - Response: Actor deleted successfully", id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(demo.dto.ApiResponse.success("Actor deleted successfully", null));
            } else {
                logger.warn("DELETE /api/actors/{} - Response: Actor not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(demo.dto.ApiResponse.error("Actor not found with id: " + id));
            }
        } catch (Exception e) {
            logger.error("DELETE /api/actors/{} - Error deleting actor", id, e);
            throw e;
        }
    }
}
