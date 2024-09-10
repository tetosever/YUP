package app.YUP;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Main application class for the YUP application.
 * This class contains the main method which serves as the entry point for the Spring Boot application.
 * It is also responsible for enabling the Web MVC framework.
 */
@SpringBootApplication
@EnableWebMvc
public class YupApplication {

	/**
	 * The main method which serves as the entry point for the Spring Boot application.
	 * @param args command line arguments passed to the application. Not currently used in this application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(YupApplication.class, args);
	}

}