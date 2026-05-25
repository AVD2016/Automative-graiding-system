package uk.ac.rhul.cs2800.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2800.model.Administrator;

public interface AdministratorRepository extends CrudRepository<Administrator, Integer> {

  Optional<Administrator> findByUsername(String username);
}
