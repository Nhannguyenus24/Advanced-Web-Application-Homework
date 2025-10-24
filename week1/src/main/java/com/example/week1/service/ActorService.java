package com.example.week1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.example.week1.model.Actor;
import com.example.week1.repository.ActorRepository;

@Service
public class ActorService {
    private final ActorRepository actorRepository;

    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    public Optional<Actor> getActorById(Short id) {
        return actorRepository.findById(id);
    }

    public Actor createActor(Actor actor) {
        return actorRepository.save(actor);
    }

    public Optional<Actor> updateActor(Short id, Actor actorDetails) {
        return actorRepository.findById(id).map(existingActor -> {
            existingActor.setFirstName(actorDetails.getFirstName());
            existingActor.setLastName(actorDetails.getLastName());
            return actorRepository.save(existingActor);
        });
    }

    public boolean deleteActor(Short id) {
        return actorRepository.findById(id).map(actor -> {
            actorRepository.delete(actor);
            return true;
        }).orElse(false);
    }
}
