package demo.service;

import demo.dto.FilmRequest;
import demo.dto.FilmResponse;
import demo.model.Film;
import demo.repository.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final IRepository repository;

    @Autowired
    public FilmService(IRepository repository) {
        this.repository = repository;
    }

    // GET all
    public List<FilmResponse> getAllFilms() {
        return repository.findAllFilms().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // GET by id
    public Optional<FilmResponse> getFilmById(int id) {
        return repository.findFilmById(id)
                .map(this::convertToResponse);
    }

    // CREATE
    public FilmResponse createFilm(FilmRequest request) {
        Film film = buildFilmFromRequest(request);
        film.setLastUpdate(LocalDateTime.now());

        int newId = repository.insertFilm(film);
        film.setFilmId(newId);

        return convertToResponse(film);
    }

    // UPDATE (truyền id từ path + body là FilmRequest)
    public Optional<FilmResponse> updateFilm(int id, FilmRequest request) {
        Film film = buildFilmFromRequest(request);
        film.setFilmId(id);
        film.setLastUpdate(LocalDateTime.now());

        int updatedRows = repository.updateFilmById(id, film);
        if (updatedRows > 0) {
            return repository.findFilmById(id).map(this::convertToResponse);
        }
        return Optional.empty();
    }

    // DELETE
    public boolean deleteFilm(int id) {
        int deletedRows = repository.deleteFilmById(id);
        return deletedRows > 0;
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
