package week2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import week2.dto.FilmDto;
import week2.entity.Film;
import week2.entity.Language;
import week2.exception.ResourceNotFoundException;
import week2.repository.FilmRepository;
import week2.repository.LanguageRepository;

@Service
@Transactional
public class FilmService {
    
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);
    
    @Autowired
    private FilmRepository filmRepository;
    
    @Autowired
    private LanguageRepository languageRepository;
    
    public List<FilmDto> getAllFilms() {
        logger.info("Fetching all films from database");
        List<Film> films = filmRepository.findAll();
        logger.debug("Found {} films", films.size());
        return films.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public FilmDto getFilmById(Integer id) {
        logger.info("Fetching film with id: {}", id);
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Film not found with id: {}", id);
                    return new ResourceNotFoundException("Film", "id", id);
                });
        logger.debug("Found film: {}", film.getTitle());
        return convertToDto(film);
    }
    
    public FilmDto createFilm(FilmDto filmDto) {
        logger.info("Creating new film: {}", filmDto.getTitle());
        Film film = convertToEntity(filmDto);
        Film savedFilm = filmRepository.save(film);
        logger.info("Successfully created film with id: {}", savedFilm.getFilmId());
        return convertToDto(savedFilm);
    }
    
    public FilmDto updateFilm(Integer id, FilmDto filmDto) {
        logger.info("Updating film with id: {}", id);
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Film not found with id: {}", id);
                    return new ResourceNotFoundException("Film", "id", id);
                });
        
        film.setTitle(filmDto.getTitle());
        film.setDescription(filmDto.getDescription());
        film.setReleaseYear(filmDto.getReleaseYear());
        film.setRentalDuration(filmDto.getRentalDuration());
        film.setRentalRate(filmDto.getRentalRate());
        film.setLength(filmDto.getLength());
        film.setReplacementCost(filmDto.getReplacementCost());
        film.setRating(filmDto.getRating());
        film.setSpecialFeatures(filmDto.getSpecialFeatures());
        
        // Update language if changed
        if (filmDto.getLanguageId() != null) {
            Language language = languageRepository.findById(filmDto.getLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language", "id", filmDto.getLanguageId()));
            film.setLanguage(language);
        }
        
        // Update original language if provided
        if (filmDto.getOriginalLanguageId() != null) {
            Language originalLanguage = languageRepository.findById(filmDto.getOriginalLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language", "id", filmDto.getOriginalLanguageId()));
            film.setOriginalLanguage(originalLanguage);
        }
        
        Film updatedFilm = filmRepository.save(film);
        logger.info("Successfully updated film with id: {}", id);
        return convertToDto(updatedFilm);
    }
    
    public void deleteFilm(Integer id) {
        logger.info("Deleting film with id: {}", id);
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Film not found with id: {}", id);
                    return new ResourceNotFoundException("Film", "id", id);
                });
        filmRepository.delete(film);
        logger.info("Successfully deleted film with id: {}", id);
    }
    
    public List<FilmDto> searchFilmsByTitle(String title) {
        logger.info("Searching films with title containing: {}", title);
        List<Film> films = filmRepository.findByTitleContainingIgnoreCase(title);
        logger.debug("Found {} films matching title: {}", films.size(), title);
        return films.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<FilmDto> getFilmsByYear(Integer year) {
        logger.info("Fetching films released in year: {}", year);
        List<Film> films = filmRepository.findByReleaseYear(year);
        logger.debug("Found {} films from year {}", films.size(), year);
        return films.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private FilmDto convertToDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setFilmId(film.getFilmId());
        dto.setTitle(film.getTitle());
        dto.setDescription(film.getDescription());
        dto.setReleaseYear(film.getReleaseYear());
        dto.setLanguageId(film.getLanguage() != null ? film.getLanguage().getLanguageId() : null);
        dto.setOriginalLanguageId(film.getOriginalLanguage() != null ? film.getOriginalLanguage().getLanguageId() : null);
        dto.setRentalDuration(film.getRentalDuration());
        dto.setRentalRate(film.getRentalRate());
        dto.setLength(film.getLength());
        dto.setReplacementCost(film.getReplacementCost());
        dto.setRating(film.getRating());
        dto.setSpecialFeatures(film.getSpecialFeatures());
        return dto;
    }
    
    private Film convertToEntity(FilmDto dto) {
        Film film = new Film();
        film.setTitle(dto.getTitle());
        film.setDescription(dto.getDescription());
        film.setReleaseYear(dto.getReleaseYear());
        film.setRentalDuration(dto.getRentalDuration());
        film.setRentalRate(dto.getRentalRate());
        film.setLength(dto.getLength());
        film.setReplacementCost(dto.getReplacementCost());
        film.setRating(dto.getRating());
        film.setSpecialFeatures(dto.getSpecialFeatures());
        
        // Set language
        if (dto.getLanguageId() != null) {
            Language language = languageRepository.findById(dto.getLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language", "id", dto.getLanguageId()));
            film.setLanguage(language);
        }
        
        // Set original language if provided
        if (dto.getOriginalLanguageId() != null) {
            Language originalLanguage = languageRepository.findById(dto.getOriginalLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language", "id", dto.getOriginalLanguageId()));
            film.setOriginalLanguage(originalLanguage);
        }
        
        return film;
    }
}

