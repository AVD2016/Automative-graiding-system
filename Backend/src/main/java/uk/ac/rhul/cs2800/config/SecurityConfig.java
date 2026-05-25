package uk.ac.rhul.cs2800.config;


import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration class responsible for defining the application's Spring Security and
 * Cross-Origin Resource Sharing settings.
 *
 * <ul>
 * <li>Disables CSRF protection (typical for stateless APIs)</li>
 * <li>Enables CORS support using a custom {@link CorsConfigurationSource}</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

  /**
   * Configure the main Spring Security filter chain.
   *
   * <ul>
   * <li>Disables CSRF protection</li>
   * <li>Enables CORS using the default Spring Security CORS filter, which pulls configuration from
   * the {@link CorsConfigurationSource} bean</li>
   * </ul>
   *
   * @param http the {@link HttpSecurity} object used to configure security behavior
   * @return the built {@link SecurityFilterChain} for the application
   * @throws Exception if an error occurs while building the filter chain
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults());

    return http.build();
  }

  /**
   * Define the CORS configuration for the entire application.
   *
   * <ul>
   * <li>Allows all HTTP methods (GET, POST, PUT, DELETE, etc.)</li>
   * </ul>
   *
   * @return a {@link CorsConfigurationSource} containing the CORS rules
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(Arrays.asList("*"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowedMethods(Arrays.asList("*"));
    config.setAllowCredentials(false);
    config.applyPermitDefaultValues();

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
  }
}
