package demo.service;

import demo.dto.ActorRequest;
import demo.dto.ActorResponse;
import demo.dto.ActorUpdateRequest;
import demo.model.Actor;
import demo.repository.IRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorServiceTest {

    @Mock
    private IRepository actorRepository;

    @InjectMocks
    private ActorService actorService;

    private Actor testActor;
    private ActorRequest testActorRequest;
    private ActorUpdateRequest testActorUpdateRequest;

    @BeforeEach
    void setUp() {
        testActor = new Actor();
        testActor.setActorId(1);
        testActor.setFirstName("Tom");
        testActor.setLastName("Cruise");
        testActor.setLastUpdate(LocalDateTime.now());

        testActorRequest = new ActorRequest();
        testActorRequest.setFirstName("Tom");
        testActorRequest.setLastName("Cruise");

        testActorUpdateRequest = new ActorUpdateRequest();
        testActorUpdateRequest.setActorId(1);
        testActorUpdateRequest.setFirstName("Thomas");
        testActorUpdateRequest.setLastName("Cruise");
    }

    @Test
    void getAllActors_ShouldReturnListOfActorResponses() {
        // Given
        List<Actor> actors = Arrays.asList(testActor);
        when(actorRepository.findAll()).thenReturn(actors);

        // When
        List<ActorResponse> result = actorService.getAllActors();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testActor.getActorId(), result.get(0).getActorId());
        assertEquals(testActor.getFirstName(), result.get(0).getFirstName());
        assertEquals(testActor.getLastName(), result.get(0).getLastName());
        verify(actorRepository).findAll();
    }

    @Test
    void getActorById_WhenActorExists_ShouldReturnActorResponse() {
        // Given
        when(actorRepository.findById(1)).thenReturn(Optional.of(testActor));

        // When
        Optional<ActorResponse> result = actorService.getActorById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testActor.getActorId(), result.get().getActorId());
        assertEquals(testActor.getFirstName(), result.get().getFirstName());
        assertEquals(testActor.getLastName(), result.get().getLastName());
        verify(actorRepository).findById(1);
    }

    @Test
    void getActorById_WhenActorNotExists_ShouldReturnEmpty() {
        // Given
        when(actorRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<ActorResponse> result = actorService.getActorById(999);

        // Then
        assertFalse(result.isPresent());
        verify(actorRepository).findById(999);
    }

    @Test
    void createActor_ShouldReturnActorResponse() {
        // Given
        when(actorRepository.insert(any(Actor.class))).thenReturn(1);

        // When
        ActorResponse result = actorService.createActor(testActorRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getActorId());
        assertEquals(testActorRequest.getFirstName(), result.getFirstName());
        assertEquals(testActorRequest.getLastName(), result.getLastName());
        assertNotNull(result.getLastUpdate());
        verify(actorRepository).insert(any(Actor.class));
    }

    @Test
    void updateActor_WhenActorExists_ShouldReturnUpdatedActorResponse() {
        // Given
        Actor updatedActor = new Actor();
        updatedActor.setActorId(1);
        updatedActor.setFirstName("Thomas");
        updatedActor.setLastName("Cruise");
        updatedActor.setLastUpdate(LocalDateTime.now());

        when(actorRepository.update(eq(1), any(Actor.class))).thenReturn(1);
        when(actorRepository.findById(1)).thenReturn(Optional.of(updatedActor));

        // When
        Optional<ActorResponse> result = actorService.updateActor(testActorUpdateRequest);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getActorId());
        assertEquals("Thomas", result.get().getFirstName());
        assertEquals("Cruise", result.get().getLastName());
        verify(actorRepository).update(eq(1), any(Actor.class));
        verify(actorRepository).findById(1);
    }

    @Test
    void updateActor_WhenActorNotExists_ShouldReturnEmpty() {
        // Given
        when(actorRepository.update(eq(999), any(Actor.class))).thenReturn(0);

        testActorUpdateRequest.setActorId(999);

        // When
        Optional<ActorResponse> result = actorService.updateActor(testActorUpdateRequest);

        // Then
        assertFalse(result.isPresent());
        verify(actorRepository).update(eq(999), any(Actor.class));
        verify(actorRepository, never()).findById(999);
    }

    @Test
    void deleteActor_WhenActorExists_ShouldReturnTrue() {
        // Given
        when(actorRepository.delete(1)).thenReturn(1);

        // When
        boolean result = actorService.deleteActor(1);

        // Then
        assertTrue(result);
        verify(actorRepository).delete(1);
    }

    @Test
    void deleteActor_WhenActorNotExists_ShouldReturnFalse() {
        // Given
        when(actorRepository.delete(999)).thenReturn(0);

        // When
        boolean result = actorService.deleteActor(999);

        // Then
        assertFalse(result);
        verify(actorRepository).delete(999);
    }
}