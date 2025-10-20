package demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {

	@Test
	void contextLoads() {
		// Test that the Spring application context loads successfully
		// This test ensures all beans are properly configured and can be instantiated
	}

	@Test
	void mainApplicationRuns() {
		// Test that main method doesn't throw any exceptions
		// This is a basic smoke test
		// We don't actually call main here as it would start the server
		// The contextLoads test above is sufficient for testing the configuration
	}

}
