package uk.ac.rhul.cs2800;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the CW2 Spring Boot application.
 *
 * <p>
 * This class bootstraps the Spring context and launches the embedded server.
 * </p>
 */
@SpringBootApplication
public class Cw2Application {

  /**
   * Starts the CW2 Spring Boot application.
   *
   * @param args command-line arguments passed to the application
   */
  public static void main(String[] args) {
    SpringApplication.run(Cw2Application.class, args);
  }

}
