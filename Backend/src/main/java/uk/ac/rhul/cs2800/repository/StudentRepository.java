package uk.ac.rhul.cs2800.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2800.model.Student;

/**
 * Repository for performing CRUD operations on {@link Student} entities.
 *
 * <p>
 * Spring Data automatically provides the implementation.
 * </p>
 *
 * @author Vladyslav Abramov
 * @version 1.0
 * @since 2025-10-24
 */
public interface StudentRepository extends CrudRepository<Student, Integer> {

  Optional<Student> findByUsername(String username);
}

