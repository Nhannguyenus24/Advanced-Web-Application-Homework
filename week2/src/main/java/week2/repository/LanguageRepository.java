package week2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import week2.entity.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {
}

