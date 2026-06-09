package uk.ac.rhul.cs2800.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import uk.ac.rhul.cs2800.model.Administrator;
import uk.ac.rhul.cs2800.model.Assignment;
import uk.ac.rhul.cs2800.model.AssignmentSubmission;
import uk.ac.rhul.cs2800.model.AuthService;
import uk.ac.rhul.cs2800.model.Lecturer;
import uk.ac.rhul.cs2800.model.LoginRequest;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Registration;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.model.User;

/**
 * Exposes entity IDs in Spring Data REST responses.
 */
@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

  /** Expose IDs for Student, Module, and Grade entities. */
  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
      CorsRegistry cors) {
    config.exposeIdsFor(Student.class);
    config.exposeIdsFor(Lecturer.class);
    config.exposeIdsFor(Administrator.class);
    config.exposeIdsFor(User.class);
    config.exposeIdsFor(Registration.class);
    config.exposeIdsFor(AuthService.class);
    config.exposeIdsFor(LoginRequest.class);
    config.exposeIdsFor(Assignment.class);
    config.exposeIdsFor(Module.class);
    config.exposeIdsFor(AssignmentSubmission.class);


    cors.addMapping("/**")
        .allowedOriginPatterns(
            "https://*.vercel.app")
        .allowedMethods("*").allowedHeaders("*");
  }

}