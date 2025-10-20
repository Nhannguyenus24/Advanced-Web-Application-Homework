package demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.dto.ActorRequest;
import demo.dto.ActorResponse;
import demo.dto.ActorUpdateRequest;
import demo.model.Actor;
import demo.repository.IRepository;

@Service
public class ActorService {

    private final IRepository actorRepository;
    private final Logger logger = LoggerFactory.getLogger(ActorService.class);

    @Autowired
    public ActorService(IRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    public List<ActorResponse> getAllActors() {
        logger.debug("ActorService.getAllActors() - Fetching all actors from repository");
        try {
            List<ActorResponse> actors = actorRepository.findAllActors().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            logger.debug("ActorService.getAllActors() - Successfully fetched {} actors", actors.size());
            return actors;
        } catch (Exception e) {
            logger.error("ActorService.getAllActors() - Error fetching all actors", e);
            throw e;
        }
    }

    public Optional<ActorResponse> getActorById(int id) {
        logger.debug("ActorService.getActorById() - Fetching actor with ID: {}", id);
        try {
            Optional<ActorResponse> actor = actorRepository.findActorById(id)
                    .map(this::convertToResponse);
            if (actor.isPresent()) {
                logger.debug("ActorService.getActorById() - Found actor with ID: {} - {}", id, actor.get());
            } else {
                logger.warn("ActorService.getActorById() - Actor not found with ID: {}", id);
            }
            return actor;
        } catch (Exception e) {
            logger.error("ActorService.getActorById() - Error fetching actor with ID: {}", id, e);
            throw e;
        }
    }

    public ActorResponse createActor(ActorRequest request) {
        logger.debug("ActorService.createActor() - Creating new actor: {}", request);
        try {
            Actor actor = new Actor();
            actor.setFirstName(request.getFirstName());
            actor.setLastName(request.getLastName());
            actor.setLastUpdate(LocalDateTime.now());
            
            int newId = actorRepository.insertActor(actor);
            actor.setActorId(newId);
            
            ActorResponse response = convertToResponse(actor);
            logger.info("ActorService.createActor() - Successfully created actor with ID: {} - {} {}", 
                    newId, actor.getFirstName(), actor.getLastName());
            return response;
        } catch (Exception e) {
            logger.error("ActorService.createActor() - Error creating actor: {}", request, e);
            throw e;
        }
    }

    public Optional<ActorResponse> updateActor(ActorUpdateRequest request) {
        logger.debug("ActorService.updateActor() - Updating actor: {}", request);
        try {
            Actor actor = new Actor();
            actor.setActorId(request.getActorId());
            actor.setFirstName(request.getFirstName());
            actor.setLastName(request.getLastName());
            actor.setLastUpdate(LocalDateTime.now());
            
            int updatedRows = actorRepository.updateActorById(request.getActorId(), actor);
            
            if (updatedRows > 0) {
                Optional<ActorResponse> updated = actorRepository.findActorById(request.getActorId())
                        .map(this::convertToResponse);
                logger.info("ActorService.updateActor() - Successfully updated actor with ID: {} - {} {}", 
                        request.getActorId(), actor.getFirstName(), actor.getLastName());
                return updated;
            } else {
                logger.warn("ActorService.updateActor() - Actor not found for update with ID: {}", request.getActorId());
            }
            
            return Optional.empty();
        } catch (Exception e) {
            logger.error("ActorService.updateActor() - Error updating actor: {}", request, e);
            throw e;
        }
    }

    public boolean deleteActor(int id) {
        logger.debug("ActorService.deleteActor() - Deleting actor with ID: {}", id);
        try {
            int deletedRows = actorRepository.deleteActorById(id);
            boolean success = deletedRows > 0;
            if (success) {
                logger.info("ActorService.deleteActor() - Successfully deleted actor with ID: {}", id);
            } else {
                logger.warn("ActorService.deleteActor() - Actor not found for deletion with ID: {}", id);
            }
            return success;
        } catch (Exception e) {
            logger.error("ActorService.deleteActor() - Error deleting actor with ID: {}", id, e);
            throw e;
        }
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