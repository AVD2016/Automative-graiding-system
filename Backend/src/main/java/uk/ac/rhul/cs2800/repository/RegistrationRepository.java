package uk.ac.rhul.cs2800.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2800.model.Registration;

/**
 * Repository for performing CRUD operations on {@link Registration} entities.
 *
 * <p>
 * Spring Data automatically provides the implementation.
 * </p>
 *
 * @author Vladyslav Abramov
 * @version 1.0
 * @since 2025-10-24
 */
public interface RegistrationRepository extends CrudRepository<Registration, Long> {

}

