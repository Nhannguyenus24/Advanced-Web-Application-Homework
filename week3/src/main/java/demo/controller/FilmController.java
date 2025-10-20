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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.dto.ApiResponse;
import demo.dto.FilmRequest;
import demo.dto.FilmResponse;
import demo.service.FilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Films", description = "CRUD operations for film management")
@RestController
@RequestMapping("/api/films")
public class FilmController {

    private final FilmService filmService;
    private final Logger logger = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @Operation(
            summary = "Get all films",
            description = "Returns a list of all films, sorted by film_id in ascending order",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved films",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<FilmResponse>>> getAllFilms() {
        logger.info("GET /api/films - Request: Get all films");
        try {
            List<FilmResponse> films = filmService.getAllFilms();
            ApiResponse<List<FilmResponse>> response = ApiResponse.success("Successfully retrieved all films", films);
            logger.info("GET /api/films - Response: Successfully retrieved {} films", films.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("GET /api/films - Error retrieving all films", e);
            throw e;
        }
    }

    @Operation(
            summary = "Get film by ID",
            description = "Returns a single film by its ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved film",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Film not found"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FilmResponse>> getFilmById(
            @Parameter(description = "Film ID", example = "1001") @PathVariable int id) {
        logger.info("GET /api/films/{} - Request: Get film by ID", id);
        try {
            Optional<FilmResponse> film = filmService.getFilmById(id);
            if (film.isPresent()) {
                ApiResponse<FilmResponse> response = ApiResponse.success("Film found successfully", film.get());
                logger.info("GET /api/films/{} - Response: Film found - {}", id, film.get().getTitle());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("GET /api/films/{} - Response: Film not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Film not found with id: " + id));
            }
        } catch (Exception e) {
            logger.error("GET /api/films/{} - Error retrieving film", id, e);
            throw e;
        }
    }

    @Operation(
            summary = "Create new film",
            description = "Creates a new film with the provided information",
            requestBody = @RequestBody(
                    required = true,
                    description = "Film information to create",
                    content = @Content(
                            schema = @Schema(implementation = FilmRequest.class),
                            examples = @ExampleObject(
                                    name = "NewFilm",
                                    value = """
                                    {
                                      "title": "The Matrix",
                                      "description": "A computer hacker learns about the true nature of reality.",
                                      "releaseYear": 1999,
                                      "languageId": 1,
                                      "originalLanguageId": 2,
                                      "rentalDuration": 3,
                                      "rentalRate": 4.99,
                                      "length": 136,
                                      "replacementCost": 19.99,
                                      "rating": "R",
                                      "specialFeatures": ["TRAILERS", "DELETED_SCENES"]
                                    }
                                    """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Film created successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<FilmResponse>> createFilm(
            @Valid @org.springframework.web.bind.annotation.RequestBody FilmRequest request) {
        logger.info("POST /api/films - Request: {}", request);
        try {
            FilmResponse created = filmService.createFilm(request);
            ApiResponse<FilmResponse> response = ApiResponse.success("Film created successfully", created);
            logger.info("POST /api/films - Response: Film created successfully with ID {} - {}", created.getFilmId(), created.getTitle());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("POST /api/films - Error creating film with request: {}", request, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create film: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Update film",
            description = "Updates film information by ID (full or partial, depending on service logic)",
            requestBody = @RequestBody(
                    required = true,
                    description = "Film information to update",
                    content = @Content(
                            schema = @Schema(implementation = FilmRequest.class),
                            examples = @ExampleObject(
                                    name = "UpdateFilm",
                                    value = """
                                    {
                                      "title": "The Matrix Reloaded",
                                      "description": "Sequel description...",
                                      "releaseYear": 2003,
                                      "languageId": 1,
                                      "originalLanguageId": 2,
                                      "rentalDuration": 5,
                                      "rentalRate": 5.99,
                                      "length": 138,
                                      "replacementCost": 24.99,
                                      "rating": "PG_13",
                                      "specialFeatures": ["TRAILERS", "COMMENTARIES"]
                                    }
                                    """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Film updated successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Film not found"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FilmResponse>> updateFilm(
            @Parameter(description = "Film ID", example = "1001") @PathVariable int id,
            @Valid @org.springframework.web.bind.annotation.RequestBody FilmRequest request) {
        logger.info("PUT /api/films/{} - Request: {}", id, request);
        try {
            Optional<FilmResponse> updated = filmService.updateFilm(id, request);
            if (updated.isPresent()) {
                ApiResponse<FilmResponse> response = ApiResponse.success("Film updated successfully", updated.get());
                logger.info("PUT /api/films/{} - Response: Film updated successfully - {}", id, updated.get().getTitle());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("PUT /api/films/{} - Response: Film not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Film not found with id: " + id));
            }
        } catch (Exception e) {
            logger.error("PUT /api/films/{} - Error updating film with request: {}", id, request, e);
            throw e;
        }
    }

    @Operation(
            summary = "Delete film by ID",
            description = "Deletes a film by its ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "204",
                            description = "Film deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Film not found"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFilm(
            @Parameter(description = "Film ID", example = "1001") @PathVariable int id) {
        logger.info("DELETE /api/films/{} - Request: Delete film by ID", id);
        try {
            boolean deleted = filmService.deleteFilm(id);
            if (deleted) {
                logger.info("DELETE /api/films/{} - Response: Film deleted successfully", id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(ApiResponse.success("Film deleted successfully", null));
            } else {
                logger.warn("DELETE /api/films/{} - Response: Film not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Film not found with id: " + id));
            }
        } catch (Exception e) {
            logger.error("DELETE /api/films/{} - Error deleting film", id, e);
            throw e;
        }
    }
}
