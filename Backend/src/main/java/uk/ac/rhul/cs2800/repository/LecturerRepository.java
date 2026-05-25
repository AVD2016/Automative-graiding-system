package uk.ac.rhul.cs2800.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2800.model.Lecturer;

public interface LecturerRepository extends CrudRepository<Lecturer, Integer> {
  Optional<Lecturer> findByUsername(String username);
}
