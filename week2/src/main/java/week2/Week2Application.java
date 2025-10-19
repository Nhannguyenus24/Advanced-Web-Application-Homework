package week2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Week2Application {

	private static final Logger logger = LoggerFactory.getLogger(Week2Application.class);

	public static void main(String[] args) {
		logger.info("Starting Sakila CRUD Application...");
		
		SpringApplication app = new SpringApplication(Week2Application.class);
		Environment env = app.run(args).getEnvironment();
		
		String protocol = "http";
		String serverPort = env.getProperty("server.port");
		String contextPath = env.getProperty("server.servlet.context-path", "");
		
		logger.info("""
				
				----------------------------------------------------------
					Application '{}' is running! Access URLs:
					Local: \t\t{}://localhost:{}{}
					Profile(s): \t{}
				----------------------------------------------------------""",
				env.getProperty("spring.application.name"),
				protocol,
				serverPort,
				contextPath,
				env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
		);
		
		logger.info("Database URL: {}", env.getProperty("spring.datasource.url"));
		logger.info("Sakila CRUD API is ready for requests!");
	}

}
