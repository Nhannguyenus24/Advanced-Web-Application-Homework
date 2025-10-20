package demo.dto;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ActorRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void validActorRequest_ShouldPassValidation() {
        // Given
        ActorRequest request = new ActorRequest();
        request.setFirstName("Tom");
        request.setLastName("Cruise");

        // When
        Set<ConstraintViolation<ActorRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void actorRequestWithNullFirstName_ShouldFailValidation() {
        // Given
        ActorRequest request = new ActorRequest();
        request.setFirstName(null);
        request.setLastName("Cruise");

        // When
        Set<ConstraintViolation<ActorRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("First name is required")));
    }

    @Test
    void actorRequestWithEmptyFirstName_ShouldFailValidation() {
        // Given
        ActorRequest request = new ActorRequest();
        request.setFirstName("");
        request.setLastName("Cruise");

        // When
        Set<ConstraintViolation<ActorRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("First name is required")));
    }

    @Test
    void actorRequestWithTooLongFirstName_ShouldFailValidation() {
        // Given
        ActorRequest request = new ActorRequest();
        request.setFirstName("a".repeat(46)); // Exceeds 45 character limit
        request.setLastName("Cruise");

        // When
        Set<ConstraintViolation<ActorRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("First name must not exceed 45 characters")));
    }

    @Test
    void actorRequestWithNullLastName_ShouldFailValidation() {
        // Given
        ActorRequest request = new ActorRequest();
        request.setFirstName("Tom");
        request.setLastName(null);

        // When
        Set<ConstraintViolation<ActorRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Last name is required")));
    }

    @Test
    void actorRequestWithEmptyLastName_ShouldFailValidation() {
        // Given
        ActorRequest request = new ActorRequest();
        request.setFirstName("Tom");
        request.setLastName("");

        // When
        Set<ConstraintViolation<ActorRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Last name is required")));
    }

    @Test
    void actorRequestWithTooLongLastName_ShouldFailValidation() {
        // Given
        ActorRequest request = new ActorRequest();
        request.setFirstName("Tom");
        request.setLastName("a".repeat(46)); // Exceeds 45 character limit

        // When
        Set<ConstraintViolation<ActorRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Last name must not exceed 45 characters")));
    }

    @Test
    void actorRequestGettersAndSetters_ShouldWork() {
        // Given
        ActorRequest request = new ActorRequest();

        // When
        request.setFirstName("Tom");
        request.setLastName("Cruise");

        // Then
        assertEquals("Tom", request.getFirstName());
        assertEquals("Cruise", request.getLastName());
    }

    @Test
    void actorRequestConstructors_ShouldWork() {
        // Test no-args constructor
        ActorRequest request1 = new ActorRequest();
        assertNull(request1.getFirstName());
        assertNull(request1.getLastName());

        // Test all-args constructor
        ActorRequest request2 = new ActorRequest("Tom", "Cruise");
        assertEquals("Tom", request2.getFirstName());
        assertEquals("Cruise", request2.getLastName());
    }

    @Test
    void actorRequestToString_ShouldWork() {
        // Given
        ActorRequest request = new ActorRequest("Tom", "Cruise");

        // When
        String toString = request.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Tom"));
        assertTrue(toString.contains("Cruise"));
    }
}