package demo.controller;

import demo.dto.ActorRequest;
import demo.dto.ActorResponse;
import demo.dto.ActorUpdateRequest;
import demo.service.ActorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActorController.class)
class ActorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActorService actorService;

    @Autowired
    private ObjectMapper objectMapper;

    private ActorResponse testActorResponse;
    private ActorRequest testActorRequest;
    private ActorUpdateRequest testActorUpdateRequest;

    @BeforeEach
    void setUp() {
        testActorResponse = new ActorResponse();
        testActorResponse.setActorId(1);
        testActorResponse.setFirstName("Tom");
        testActorResponse.setLastName("Cruise");
        testActorResponse.setLastUpdate(LocalDateTime.now());

        testActorRequest = new ActorRequest();
        testActorRequest.setFirstName("Tom");
        testActorRequest.setLastName("Cruise");

        testActorUpdateRequest = new ActorUpdateRequest();
        testActorUpdateRequest.setActorId(1);
        testActorUpdateRequest.setFirstName("Thomas");
        testActorUpdateRequest.setLastName("Cruise");
    }

    @Test
    void getAllActors_ShouldReturnListOfActors() throws Exception {
        // Given
        List<ActorResponse> actors = Arrays.asList(testActorResponse);
        when(actorService.getAllActors()).thenReturn(actors);

        // When & Then
        mockMvc.perform(get("/api/actors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Successfully retrieved all actors"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].actorId").value(1))
                .andExpect(jsonPath("$.data[0].firstName").value("Tom"))
                .andExpect(jsonPath("$.data[0].lastName").value("Cruise"));

        verify(actorService).getAllActors();
    }

    @Test
    void getActorById_WhenActorExists_ShouldReturnActor() throws Exception {
        // Given
        when(actorService.getActorById(1)).thenReturn(Optional.of(testActorResponse));

        // When & Then
        mockMvc.perform(get("/api/actors/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Actor found successfully"))
                .andExpect(jsonPath("$.data.actorId").value(1))
                .andExpect(jsonPath("$.data.firstName").value("Tom"))
                .andExpect(jsonPath("$.data.lastName").value("Cruise"));

        verify(actorService).getActorById(1);
    }

    @Test
    void getActorById_WhenActorNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(actorService.getActorById(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/actors/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Actor not found with id: 999"));

        verify(actorService).getActorById(999);
    }

    @Test
    void createActor_WithValidData_ShouldCreateActor() throws Exception {
        // Given
        when(actorService.createActor(any(ActorRequest.class))).thenReturn(testActorResponse);

        // When & Then
        mockMvc.perform(post("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testActorRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Actor created successfully"))
                .andExpect(jsonPath("$.data.actorId").value(1))
                .andExpect(jsonPath("$.data.firstName").value("Tom"))
                .andExpect(jsonPath("$.data.lastName").value("Cruise"));

        verify(actorService).createActor(any(ActorRequest.class));
    }

    @Test
    void createActor_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        ActorRequest invalidRequest = new ActorRequest();
        invalidRequest.setFirstName(""); // Invalid empty string
        invalidRequest.setLastName(""); // Invalid empty string

        // When & Then
        mockMvc.perform(post("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(actorService, never()).createActor(any(ActorRequest.class));
    }

    @Test
    void updateActor_WhenActorExists_ShouldUpdateActor() throws Exception {
        // Given
        ActorResponse updatedActorResponse = new ActorResponse();
        updatedActorResponse.setActorId(1);
        updatedActorResponse.setFirstName("Thomas");
        updatedActorResponse.setLastName("Cruise");
        updatedActorResponse.setLastUpdate(LocalDateTime.now());

        when(actorService.updateActor(any(ActorUpdateRequest.class)))
                .thenReturn(Optional.of(updatedActorResponse));

        // When & Then
        mockMvc.perform(patch("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testActorUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Actor updated successfully"))
                .andExpect(jsonPath("$.data.actorId").value(1))
                .andExpect(jsonPath("$.data.firstName").value("Thomas"))
                .andExpect(jsonPath("$.data.lastName").value("Cruise"));

        verify(actorService).updateActor(any(ActorUpdateRequest.class));
    }

    @Test
    void updateActor_WhenActorNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(actorService.updateActor(any(ActorUpdateRequest.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testActorUpdateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"));

        verify(actorService).updateActor(any(ActorUpdateRequest.class));
    }

    @Test
    void deleteActor_WhenActorExists_ShouldDeleteActor() throws Exception {
        // Given
        when(actorService.deleteActor(1)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/actors/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Actor deleted successfully"));

        verify(actorService).deleteActor(1);
    }

    @Test
    void deleteActor_WhenActorNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(actorService.deleteActor(999)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/actors/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Actor not found with id: 999"));

        verify(actorService).deleteActor(999);
    }
}