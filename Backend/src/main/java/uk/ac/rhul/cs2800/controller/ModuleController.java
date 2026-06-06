package uk.ac.rhul.cs2800.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.dataObjects.LecturerViewModulesDTO;
import uk.ac.rhul.cs2800.model.Assignment;
import uk.ac.rhul.cs2800.model.Lecturer;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Registration;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.repository.LecturerRepository;
import uk.ac.rhul.cs2800.repository.ModuleRepository;
import uk.ac.rhul.cs2800.repository.RegistrationRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

@RestController
@RequestMapping("/api/module")
@CrossOrigin(originPatterns = "https://*.vercel.app")
public class ModuleController {

  @Autowired
  private ModuleRepository moduleRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  LecturerRepository lecturerRepository;

  @Autowired
  RegistrationRepository registrationRepository;

  @GetMapping("/getAvailableModules/{studentId}")
  public List<Map<String, Object>> getAvailableModules(@PathVariable int studentId) {

    List<Map<String, Object>> response = new ArrayList<>();

    Optional<Student> optionalStudent = studentRepository.findById(studentId);

    if (optionalStudent.isEmpty()) {
      return response;
    }

    Student student = optionalStudent.get();

    Iterable<Module> allModules = moduleRepository.findAll();

    for (Module module : allModules) {

      boolean registered = false;

      for (Registration registration : student.getRegistered()) {

        if (registration.getModule() != null
            && registration.getModule().getCode().equals(module.getCode())) {

          registered = true;
          break;
        }
      }

      Map<String, Object> moduleData = new HashMap<>();

      moduleData.put("code", module.getCode());
      moduleData.put("name", module.getName());
      moduleData.put("registered", registered);

      response.add(moduleData);
    }

    return response;
  }

  @GetMapping("/getAvailableModulesLecturer/{lecturerId}")
  public List<Map<String, Object>> getAvailableModulesLecturer(@PathVariable int lecturerId) {

    List<Map<String, Object>> response = new ArrayList<>();

    Optional<Lecturer> optionalLecturer = lecturerRepository.findById(lecturerId);

    if (optionalLecturer.isEmpty()) {

      return response;
    }

    Lecturer lecturer = optionalLecturer.get();

    Iterable<Module> allModules = moduleRepository.findAll();

    for (Module module : allModules) {

      boolean registered = false;

      for (Registration registration : lecturer.getRegistered()) {

        if (registration.getModule() != null
            && registration.getModule().getCode().equals(module.getCode())) {

          registered = true;

          break;
        }
      }

      Map<String, Object> moduleData = new HashMap<>();

      moduleData.put("code", module.getCode());

      moduleData.put("name", module.getName());

      moduleData.put("registered", registered);

      response.add(moduleData);
    }

    return response;
  }

  @GetMapping("/getLecturerModules/{lecturerId}")
  public ResponseEntity<List<Map<String, Object>>> getLecturerModules(
      @PathVariable int lecturerId) {

    Optional<Lecturer> optionalLecturer = lecturerRepository.findById(lecturerId);

    if (optionalLecturer.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
    }

    Lecturer lecturer = optionalLecturer.get();

    List<Map<String, Object>> response = new ArrayList<>();

    for (Registration registration : lecturer.getRegistered()) {

      if (registration.getModule() == null) {
        continue;
      }

      Module module = registration.getModule();


      // calculate used credits
      int usedCredits = 0;

      if (module.getAssignments() != null) {
        usedCredits = module.getAssignments().stream().mapToInt(a -> a.getCredits()).sum();
      }

      int totalCredits = module.getCredits();
      int availableCredits = totalCredits - usedCredits;

      Map<String, Object> moduleData = new HashMap<>();

      moduleData.put("code", module.getCode());
      moduleData.put("name", module.getName());

      moduleData.put("totalCredits", totalCredits);
      moduleData.put("usedCredits", usedCredits);
      moduleData.put("availableCredits", availableCredits);

      response.add(moduleData);
    }

    return ResponseEntity.ok(response);
  }



  @PostMapping("/createModule")
  public ResponseEntity<?> createModule(@RequestBody Module module) {

    // CHECK IF MODULE ALREADY EXISTS

    Optional<Module> existingModule = moduleRepository.findById(module.getCode());

    if (existingModule.isPresent()) {

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Module already exists");
    }

    // SAVE MODULE

    Module savedModule = moduleRepository.save(module);

    return ResponseEntity.ok(savedModule);
  }


  @GetMapping("/overview/{lecturerId}")
  public ResponseEntity<List<LecturerViewModulesDTO>> getModuleOverview(
      @PathVariable int lecturerId) {

    Optional<Lecturer> optionalLecturer = lecturerRepository.findById(lecturerId);

    if (optionalLecturer.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Lecturer lecturer = optionalLecturer.get();

    List<LecturerViewModulesDTO> result = new ArrayList<>();

    // use SET logic to avoid duplicate modules
    Set<Module> modules = lecturer.getRegistered().stream().map(Registration::getModule)
        .filter(Objects::nonNull).collect(Collectors.toSet());

    for (Module module : modules) {

      // 1. coursework count
      int courseworkCount = module.getAssignments() != null ? module.getAssignments().size() : 0;

      // 2. assigned credits
      int assignedCredits = module.getAssignments() != null
          ? module.getAssignments().stream().mapToInt(Assignment::getCredits).sum()
          : 0;

      // 3. students enrolled (ONLY Student users)
      int studentsEnrolled = 0;

      if (module.getRegistrations() != null) {
        studentsEnrolled = (int) module.getRegistrations().stream().map(Registration::getUser)
            .filter(user -> user instanceof Student).count();
      }

      LecturerViewModulesDTO dto = new LecturerViewModulesDTO(module.getCode(), module.getName(),
          module.getCredits(), assignedCredits, courseworkCount, studentsEnrolled);

      result.add(dto);
    }

    return ResponseEntity.ok(result);
  }
}
