package uk.ac.rhul.cs2800.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.model.Lecturer;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Registration;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.repository.LecturerRepository;
import uk.ac.rhul.cs2800.repository.ModuleRepository;
import uk.ac.rhul.cs2800.repository.RegistrationRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

/**
 * REST controller responsible for handling student-module registrations.
 */

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(originPatterns = "https://*.vercel.app")
public class RegistrationController {

  RegistrationRepository registrationRepository;

  ModuleRepository moduleRepository;

  StudentRepository studentRepository;

  LecturerRepository lecturerRepository;

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
  @PostMapping(value = "/addStudent")
  public ResponseEntity<Registration> addRegistration(@RequestBody Map<String, String> params) {
    Student student =
        studentRepository.findById(Integer.valueOf(params.get("student_id"))).orElseThrow();

    Module module =
        moduleRepository.findById(params.get("module_code")).orElseThrow();

    Registration registration = new Registration();

    registration.setUser(student);
    registration.setModule(module);

    registration = registrationRepository.save(registration);

    return ResponseEntity.ok(registration);
  }

  @PostMapping("/syncStudent")
  @Transactional
  public ResponseEntity<?> syncRegistrations(@RequestBody Map<String, Object> payload) {

    Integer studentId = (Integer) payload.get("studentId");

    @SuppressWarnings("unchecked")
    List<String> moduleCodes = (List<String>) payload.get("modules");

    Student student = studentRepository.findById(studentId).orElseThrow();

    // CURRENT registrations
    List<Registration> existing = student.getRegistered();

    Set<String> newModuleSet = moduleCodes.stream().collect(Collectors.toSet());

    // 1. DELETE unselected modules
    for (Registration reg : existing) {

      String code = reg.getModule().getCode();

      if (!newModuleSet.contains(code)) {

        registrationRepository.delete(reg);
      }
    }

    // 2. ADD missing modules
    for (String code : newModuleSet) {

      boolean alreadyExists = existing.stream().anyMatch(r -> r.getModule().getCode().equals(code));

      if (!alreadyExists) {

        Module module = moduleRepository.findById(code).orElseThrow();

        Registration registration = new Registration();
        registration.setUser(student);
        registration.setModule(module);

        registrationRepository.save(registration);
      }
    }

    return ResponseEntity.ok("Sync complete");
  }

  @PostMapping("/syncLecturer")
  @Transactional
  public ResponseEntity<?> syncLecturerRegistrations(@RequestBody Map<String, Object> payload) {

    Integer lecturerId = (Integer) payload.get("lecturerId");

    @SuppressWarnings("unchecked")
    List<String> moduleCodes = (List<String>) payload.get("modules");

    Lecturer lecturer = lecturerRepository.findById(lecturerId).orElseThrow();

    // CURRENT registrations
    List<Registration> existing = lecturer.getRegistered();

    Set<String> newModuleSet = moduleCodes.stream().collect(Collectors.toSet());

    // DELETE unselected modules
    for (Registration reg : existing) {

      String code = reg.getModule().getCode();

      if (!newModuleSet.contains(code)) {

        registrationRepository.delete(reg);
      }
    }

    // ADD missing modules
    for (String code : newModuleSet) {

      boolean alreadyExists = existing.stream().anyMatch(r -> r.getModule().getCode().equals(code));

      if (!alreadyExists) {

        Module module = moduleRepository.findById(code).orElseThrow();

        Registration registration = new Registration();

        registration.setUser(lecturer);

        registration.setModule(module);

        registrationRepository.save(registration);
      }
    }

    return ResponseEntity.ok("Lecturer sync complete");
  }
}
