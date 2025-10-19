package week2.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import week2.dto.ApiResponse;
import week2.dto.FilmDto;
import week2.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/api/films")
@CrossOrigin(origins = "*")
public class FilmController {
    
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    
    @Autowired
    private FilmService filmService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<FilmDto>>> getAllFilms() {
        logger.info("GET /api/films - Fetching all films");
        List<FilmDto> films = filmService.getAllFilms();
        logger.debug("Returning {} films", films.size());
        return ResponseEntity.ok(ApiResponse.success("Films retrieved successfully", films));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FilmDto>> getFilmById(@PathVariable Integer id) {
        logger.info("GET /api/films/{} - Fetching film by id", id);
        FilmDto film = filmService.getFilmById(id);
        return ResponseEntity.ok(ApiResponse.success("Film retrieved successfully", film));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<FilmDto>> createFilm(@Valid @RequestBody FilmDto filmDto) {
        logger.info("POST /api/films - Creating new film");
        FilmDto createdFilm = filmService.createFilm(filmDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Film created successfully", createdFilm));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FilmDto>> updateFilm(
            @PathVariable Integer id,
            @Valid @RequestBody FilmDto filmDto) {
        logger.info("PUT /api/films/{} - Updating film", id);
        FilmDto updatedFilm = filmService.updateFilm(id, filmDto);
        return ResponseEntity.ok(ApiResponse.success("Film updated successfully", updatedFilm));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFilm(@PathVariable Integer id) {
        logger.info("DELETE /api/films/{} - Deleting film", id);
        filmService.deleteFilm(id);
        return ResponseEntity.ok(ApiResponse.success("Film deleted successfully", null));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FilmDto>>> searchFilmsByTitle(@RequestParam String title) {
        logger.info("GET /api/films/search?title={} - Searching films by title", title);
        List<FilmDto> films = filmService.searchFilmsByTitle(title);
        logger.debug("Found {} films matching title", films.size());
        return ResponseEntity.ok(ApiResponse.success("Films found", films));
    }
    
    @GetMapping("/year/{year}")
    public ResponseEntity<ApiResponse<List<FilmDto>>> getFilmsByYear(@PathVariable Integer year) {
        logger.info("GET /api/films/year/{} - Fetching films by year", year);
        List<FilmDto> films = filmService.getFilmsByYear(year);
        logger.debug("Found {} films from year {}", films.size(), year);
        return ResponseEntity.ok(ApiResponse.success("Films retrieved successfully", films));
    }
}

