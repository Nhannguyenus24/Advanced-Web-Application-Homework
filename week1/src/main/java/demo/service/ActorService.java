package demo.service;

import demo.dto.ActorRequest;
import demo.dto.ActorResponse;
import demo.dto.ActorUpdateRequest;
import demo.model.Actor;
import demo.repository.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActorService {

    private final IRepository actorRepository;

    @Autowired
    public ActorService(IRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    public List<ActorResponse> getAllActors() {
        return actorRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<ActorResponse> getActorById(int id) {
        return actorRepository.findById(id)
                .map(this::convertToResponse);
    }

    public ActorResponse createActor(ActorRequest request) {
        Actor actor = new Actor();
        actor.setFirstName(request.getFirstName());
        actor.setLastName(request.getLastName());
        actor.setLastUpdate(LocalDateTime.now());
        
        int newId = actorRepository.insert(actor);
        actor.setActorId(newId);
        
        return convertToResponse(actor);
    }

    public Optional<ActorResponse> updateActor(ActorUpdateRequest request) {
        Actor actor = new Actor();
        actor.setActorId(request.getActorId());
        actor.setFirstName(request.getFirstName());
        actor.setLastName(request.getLastName());
        actor.setLastUpdate(LocalDateTime.now());
        
        int updatedRows = actorRepository.update(request.getActorId(), actor);
        
        if (updatedRows > 0) {
            return actorRepository.findById(request.getActorId())
                    .map(this::convertToResponse);
        }
        
        return Optional.empty();
    }

    public boolean deleteActor(int id) {
        int deletedRows = actorRepository.delete(id);
        return deletedRows > 0;
    }

    private ActorResponse convertToResponse(Actor actor) {
        ActorResponse response = new ActorResponse();
        response.setActorId(actor.getActorId());
        response.setFirstName(actor.getFirstName());
        response.setLastName(actor.getLastName());
        response.setLastUpdate(actor.getLastUpdate());
        return response;
    }
}