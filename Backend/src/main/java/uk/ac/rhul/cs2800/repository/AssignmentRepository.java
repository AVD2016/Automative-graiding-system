package uk.ac.rhul.cs2800.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2800.model.Assignment;

public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {
}
