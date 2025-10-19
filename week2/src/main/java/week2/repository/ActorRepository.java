package week2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import week2.entity.Actor;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {
    
    List<Actor> findByFirstNameContainingIgnoreCase(String firstName);
    
    List<Actor> findByLastNameContainingIgnoreCase(String lastName);
    
    List<Actor> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}

