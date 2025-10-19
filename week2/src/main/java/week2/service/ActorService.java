package week2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import week2.dto.ActorDto;
import week2.entity.Actor;
import week2.exception.ResourceNotFoundException;
import week2.repository.ActorRepository;

@Service
@Transactional
public class ActorService {
    
    private static final Logger logger = LoggerFactory.getLogger(ActorService.class);
    
    @Autowired
    private ActorRepository actorRepository;
    
    public List<ActorDto> getAllActors() {
        logger.info("Fetching all actors from database");
        List<Actor> actors = actorRepository.findAll();
        logger.debug("Found {} actors", actors.size());
        return actors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ActorDto getActorById(Integer id) {
        logger.info("Fetching actor with id: {}", id);
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Actor not found with id: {}", id);
                    return new ResourceNotFoundException("Actor", "id", id);
                });
        logger.debug("Found actor: {} {}", actor.getFirstName(), actor.getLastName());
        return convertToDto(actor);
    }
    
    public ActorDto createActor(ActorDto actorDto) {
        logger.info("Creating new actor: {} {}", actorDto.getFirstName(), actorDto.getLastName());
        Actor actor = convertToEntity(actorDto);
        Actor savedActor = actorRepository.save(actor);
        logger.info("Successfully created actor with id: {}", savedActor.getActorId());
        return convertToDto(savedActor);
    }
    
    public ActorDto updateActor(Integer id, ActorDto actorDto) {
        logger.info("Updating actor with id: {}", id);
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Actor not found with id: {}", id);
                    return new ResourceNotFoundException("Actor", "id", id);
                });
        
        actor.setFirstName(actorDto.getFirstName());
        actor.setLastName(actorDto.getLastName());
        
        Actor updatedActor = actorRepository.save(actor);
        logger.info("Successfully updated actor with id: {}", id);
        return convertToDto(updatedActor);
    }
    
    public void deleteActor(Integer id) {
        logger.info("Deleting actor with id: {}", id);
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Actor not found with id: {}", id);
                    return new ResourceNotFoundException("Actor", "id", id);
                });
        actorRepository.delete(actor);
        logger.info("Successfully deleted actor with id: {}", id);
    }
    
    public List<ActorDto> searchActors(String keyword) {
        logger.info("Searching actors with keyword: {}", keyword);
        List<Actor> actors = actorRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
        logger.debug("Found {} actors matching keyword: {}", actors.size(), keyword);
        return actors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private ActorDto convertToDto(Actor actor) {
        ActorDto dto = new ActorDto();
        dto.setActorId(actor.getActorId());
        dto.setFirstName(actor.getFirstName());
        dto.setLastName(actor.getLastName());
        return dto;
    }
    
    private Actor convertToEntity(ActorDto dto) {
        Actor actor = new Actor();
        actor.setFirstName(dto.getFirstName());
        actor.setLastName(dto.getLastName());
        return actor;
    }
}

