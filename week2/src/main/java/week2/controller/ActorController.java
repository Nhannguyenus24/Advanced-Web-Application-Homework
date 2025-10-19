package week2.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import week2.dto.ActorDto;
import week2.dto.ApiResponse;
import week2.service.ActorService;

import java.util.List;

@RestController
@RequestMapping("/api/actors")
@CrossOrigin(origins = "*")
public class ActorController {
    
    private static final Logger logger = LoggerFactory.getLogger(ActorController.class);
    
    @Autowired
    private ActorService actorService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActorDto>>> getAllActors() {
        logger.info("GET /api/actors - Fetching all actors");
        List<ActorDto> actors = actorService.getAllActors();
        logger.debug("Returning {} actors", actors.size());
        return ResponseEntity.ok(ApiResponse.success("Actors retrieved successfully", actors));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActorDto>> getActorById(@PathVariable Integer id) {
        logger.info("GET /api/actors/{} - Fetching actor by id", id);
        ActorDto actor = actorService.getActorById(id);
        return ResponseEntity.ok(ApiResponse.success("Actor retrieved successfully", actor));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ActorDto>> createActor(@Valid @RequestBody ActorDto actorDto) {
        logger.info("POST /api/actors - Creating new actor");
        ActorDto createdActor = actorService.createActor(actorDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Actor created successfully", createdActor));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ActorDto>> updateActor(
            @PathVariable Integer id,
            @Valid @RequestBody ActorDto actorDto) {
        logger.info("PUT /api/actors/{} - Updating actor", id);
        ActorDto updatedActor = actorService.updateActor(id, actorDto);
        return ResponseEntity.ok(ApiResponse.success("Actor updated successfully", updatedActor));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteActor(@PathVariable Integer id) {
        logger.info("DELETE /api/actors/{} - Deleting actor", id);
        actorService.deleteActor(id);
        return ResponseEntity.ok(ApiResponse.success("Actor deleted successfully", null));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ActorDto>>> searchActors(@RequestParam String keyword) {
        logger.info("GET /api/actors/search?keyword={} - Searching actors", keyword);
        List<ActorDto> actors = actorService.searchActors(keyword);
        logger.debug("Found {} actors matching keyword", actors.size());
        return ResponseEntity.ok(ApiResponse.success("Actors found", actors));
    }
}

