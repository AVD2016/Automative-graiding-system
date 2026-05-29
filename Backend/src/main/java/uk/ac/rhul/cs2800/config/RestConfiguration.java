package uk.ac.rhul.cs2800.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import uk.ac.rhul.cs2800.model.Grade;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Student;

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
    config.exposeIdsFor(Module.class);
    config.exposeIdsFor(Grade.class);


    cors.addMapping("/**")
        .allowedOrigins(
            "https://*.vercel.app")
        .allowedMethods("*").allowedHeaders("*");
  }

}

