package demo.integration;

import demo.dto.ActorRequest;
import demo.dto.ActorUpdateRequest;
import demo.model.Actor;
import demo.repository.IRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class ActorControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private IRepository actorRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void completeActorWorkflow_ShouldWork() throws Exception {
        // Setup test data
        Actor testActor = new Actor();
        testActor.setActorId(1);
        testActor.setFirstName("Tom");
        testActor.setLastName("Cruise");
        testActor.setLastUpdate(LocalDateTime.now());

        // Mock repository responses
        when(actorRepository.findAll()).thenReturn(Arrays.asList(testActor));
        when(actorRepository.findById(1)).thenReturn(Optional.of(testActor));
        when(actorRepository.insert(any(Actor.class))).thenReturn(1);
        when(actorRepository.update(eq(1), any(Actor.class))).thenReturn(1);
        when(actorRepository.delete(1)).thenReturn(1);

        // Test GET all actors
        mockMvc.perform(get("/api/actors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray());

        // Test GET actor by ID
        mockMvc.perform(get("/api/actors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.actorId").value(1));

        // Test POST create actor
        ActorRequest createRequest = new ActorRequest("Tom", "Cruise");
        mockMvc.perform(post("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"));

        // Test PATCH update actor
        ActorUpdateRequest updateRequest = new ActorUpdateRequest();
        updateRequest.setActorId(1);
        updateRequest.setFirstName("Thomas");
        updateRequest.setLastName("Cruise");

        mockMvc.perform(patch("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // Test DELETE actor
        mockMvc.perform(delete("/api/actors/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value("success"));

        // Verify repository interactions
        verify(actorRepository).findAll();
        verify(actorRepository, times(2)).findById(1);
        verify(actorRepository).insert(any(Actor.class));
        verify(actorRepository).update(eq(1), any(Actor.class));
        verify(actorRepository).delete(1);
    }

    @Test
    void testErrorHandling_ShouldReturnProperErrorResponses() throws Exception {
        // Test 404 for non-existent actor
        when(actorRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/actors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Actor not found with id: 999"));

        // Test validation error for invalid request
        ActorRequest invalidRequest = new ActorRequest("", ""); // Empty names

        mockMvc.perform(post("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}