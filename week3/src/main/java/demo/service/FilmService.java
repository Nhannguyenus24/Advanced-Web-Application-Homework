package demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.dto.FilmRequest;
import demo.dto.FilmResponse;
import demo.model.Film;
import demo.repository.IRepository;

@Service
public class FilmService {

    private final IRepository repository;
    private final Logger logger = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(IRepository repository) {
        this.repository = repository;
    }

    // GET all
    public List<FilmResponse> getAllFilms() {
        logger.debug("FilmService.getAllFilms() - Fetching all films from repository");
        try {
            List<FilmResponse> films = repository.findAllFilms().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            logger.debug("FilmService.getAllFilms() - Successfully fetched {} films", films.size());
            return films;
        } catch (Exception e) {
            logger.error("FilmService.getAllFilms() - Error fetching all films", e);
            throw e;
        }
    }

    // GET by id
    public Optional<FilmResponse> getFilmById(int id) {
        logger.debug("FilmService.getFilmById() - Fetching film with ID: {}", id);
        try {
            Optional<FilmResponse> film = repository.findFilmById(id)
                    .map(this::convertToResponse);
            if (film.isPresent()) {
                logger.debug("FilmService.getFilmById() - Found film with ID: {} - {}", id, film.get().getTitle());
            } else {
                logger.warn("FilmService.getFilmById() - Film not found with ID: {}", id);
            }
            return film;
        } catch (Exception e) {
            logger.error("FilmService.getFilmById() - Error fetching film with ID: {}", id, e);
            throw e;
        }
    }

    // CREATE
    public FilmResponse createFilm(FilmRequest request) {
        logger.debug("FilmService.createFilm() - Creating new film: {}", request.getTitle());
        try {
            Film film = buildFilmFromRequest(request);
            film.setLastUpdate(LocalDateTime.now());

            int newId = repository.insertFilm(film);
            film.setFilmId(newId);

            FilmResponse response = convertToResponse(film);
            logger.info("FilmService.createFilm() - Successfully created film with ID: {} - {}", newId, film.getTitle());
            return response;
        } catch (Exception e) {
            logger.error("FilmService.createFilm() - Error creating film: {}", request.getTitle(), e);
            throw e;
        }
    }

    // UPDATE (truyền id từ path + body là FilmRequest)
    public Optional<FilmResponse> updateFilm(int id, FilmRequest request) {
        logger.debug("FilmService.updateFilm() - Updating film with ID: {} - {}", id, request.getTitle());
        try {
            Film film = buildFilmFromRequest(request);
            film.setFilmId(id);
            film.setLastUpdate(LocalDateTime.now());

            int updatedRows = repository.updateFilmById(id, film);
            if (updatedRows > 0) {
                Optional<FilmResponse> updated = repository.findFilmById(id).map(this::convertToResponse);
                logger.info("FilmService.updateFilm() - Successfully updated film with ID: {} - {}", id, film.getTitle());
                return updated;
            } else {
                logger.warn("FilmService.updateFilm() - Film not found for update with ID: {}", id);
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("FilmService.updateFilm() - Error updating film with ID: {} - {}", id, request.getTitle(), e);
            throw e;
        }
    }

    // DELETE
    public boolean deleteFilm(int id) {
        logger.debug("FilmService.deleteFilm() - Deleting film with ID: {}", id);
        try {
            int deletedRows = repository.deleteFilmById(id);
            boolean success = deletedRows > 0;
            if (success) {
                logger.info("FilmService.deleteFilm() - Successfully deleted film with ID: {}", id);
            } else {
                logger.warn("FilmService.deleteFilm() - Film not found for deletion with ID: {}", id);
            }
            return success;
        } catch (Exception e) {
            logger.error("FilmService.deleteFilm() - Error deleting film with ID: {}", id, e);
            throw e;
        }
    }

    // --------- Helpers ---------

    private Film buildFilmFromRequest(FilmRequest req) {
        Film f = new Film();
        f.setTitle(req.getTitle());
        f.setDescription(req.getDescription());
        f.setReleaseYear(req.getReleaseYear());
        f.setLanguageId(req.getLanguageId());
        f.setOriginalLanguageId(req.getOriginalLanguageId());
        f.setRentalDuration(req.getRentalDuration());
        f.setRentalRate(req.getRentalRate());
        f.setLength(req.getLength());
        f.setReplacementCost(req.getReplacementCost());
        f.setRating(req.getRating()); // có default G ở DTO/model
        f.setSpecialFeatures(req.getSpecialFeatures());
        return f;
    }

    private FilmResponse convertToResponse(Film film) {
        FilmResponse res = new FilmResponse();
        res.setFilmId(film.getFilmId());
        res.setTitle(film.getTitle());
        res.setDescription(film.getDescription());
        res.setReleaseYear(film.getReleaseYear());
        res.setLanguageId(film.getLanguageId());
        res.setOriginalLanguageId(film.getOriginalLanguageId());
        res.setRentalDuration(film.getRentalDuration());
        res.setRentalRate(film.getRentalRate());
        res.setLength(film.getLength());
        res.setReplacementCost(film.getReplacementCost());
        res.setRating(film.getRating());
        res.setSpecialFeatures(film.getSpecialFeatures());
        res.setLastUpdate(film.getLastUpdate());
        return res;
    }
}
