package uk.ac.rhul.cs2800.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uk.ac.rhul.cs2800.model.Module;

/**
 * Repository for performing CRUD operations on {@link Module} entities.
 *
 * <p>
 * Spring Data automatically provides the implementation.
 * </p>
 *
 * @author Vladyslav Abramov
 * @version 1.0
 * @since 2025-10-24
 */
public interface ModuleRepository extends CrudRepository<Module, String> {

  @Query("""
          SELECT DISTINCT m
          FROM Module m
          JOIN m.registrations r
          WHERE r.student.id = :studentId
      """)
  List<Module> findModulesByStudentId(@Param("studentId") int studentId);
}
