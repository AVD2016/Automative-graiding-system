package uk.ac.rhul.cs2800.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs2800.model.AssignmentSubmission;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

  Optional<AssignmentSubmission> findByStudentIdAndAssignmentId(int studentId, Long assignmentId);
}
