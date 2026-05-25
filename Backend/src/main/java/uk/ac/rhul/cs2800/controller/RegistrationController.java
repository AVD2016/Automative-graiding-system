package uk.ac.rhul.cs2800.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Registration;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.repository.ModuleRepository;
import uk.ac.rhul.cs2800.repository.RegistrationRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

/**
 * REST controller responsible for handling student-module registrations.
 */
@RestController
public class RegistrationController {

  RegistrationRepository registrationRepository;

  ModuleRepository moduleRepository;

  StudentRepository studentRepository;

  /**
   * Constructs a new {@code RegistrationController} with the required repositories.
   *
   * @param registrationRepository the repository used to store and retrieve registrations
   * @param moduleRepository the repository used to retrieve module entities
   * @param studentRepository the repository used to retrieve student entities
   */
  public RegistrationController(RegistrationRepository registrationRepository,
      ModuleRepository moduleRepository, StudentRepository studentRepository) {
    this.registrationRepository = registrationRepository;
    this.moduleRepository = moduleRepository;
    this.studentRepository = studentRepository;
  }

  /**
   * Creates a new registration linking a student to a module.
   *
   * @param params a map containing the required registration parameters
   * @return a {@link ResponseEntity} containing the created {@link Registration}
   *
   * @throws java.util.NoSuchElementException if either the student or module does not exist
   */
  @PostMapping(value = "/registrations/addRegistration")
  public ResponseEntity<Registration> addRegistration(@RequestBody Map<String, String> params) {
    Student student =
        studentRepository.findById(Integer.valueOf(params.get("student_id"))).orElseThrow();

    Module module =
        moduleRepository.findById(params.get("module_code")).orElseThrow();

    Registration registration = new Registration();

    registration.setStudent(student);
    registration.setModule(module);

    registration = registrationRepository.save(registration);

    return ResponseEntity.ok(registration);
  }
}
