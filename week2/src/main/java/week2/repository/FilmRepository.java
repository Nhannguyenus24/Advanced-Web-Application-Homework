package week2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import week2.entity.Film;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {
    
    List<Film> findByTitleContainingIgnoreCase(String title);
    
    List<Film> findByReleaseYear(Integer releaseYear);
    
    List<Film> findByRating(String rating);
    
    List<Film> findByLanguage_LanguageId(Integer languageId);
}

