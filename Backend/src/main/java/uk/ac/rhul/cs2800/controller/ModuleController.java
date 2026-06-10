package uk.ac.rhul.cs2800.controller;

import java.time.LocalDateTime;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.dataObjects.LecturerModuleExpandedDTO;
import uk.ac.rhul.cs2800.dataObjects.LecturerModuleStudentDTO;
import uk.ac.rhul.cs2800.dataObjects.LecturerViewModulesDTO;
import uk.ac.rhul.cs2800.model.Assignment;
import uk.ac.rhul.cs2800.model.AssignmentSubmission;
import uk.ac.rhul.cs2800.model.Lecturer;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Registration;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.model.User;
import uk.ac.rhul.cs2800.repository.AssignmentSubmissionRepository;
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
  private LecturerRepository lecturerRepository;

  @Autowired
  private RegistrationRepository registrationRepository;

  @Autowired
  private AssignmentSubmissionRepository assignmentSubmissionRepository;

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

    // save module
    Module savedModule = moduleRepository.save(module);

    return ResponseEntity.ok(savedModule);
  }

  // show modules for Lecturers
  @Transactional
  @GetMapping("/overview/{lecturerId}")
  public ResponseEntity<List<LecturerModuleExpandedDTO>> getModuleOverview(
      @PathVariable int lecturerId) {

    Lecturer lecturer = lecturerRepository.findById(lecturerId)
        .orElseThrow(() -> new RuntimeException("Lecturer not found"));

    List<LecturerModuleExpandedDTO> result = new ArrayList<>();

    Set<Module> modules = lecturer.getRegistered().stream().map(Registration::getModule)
        .filter(Objects::nonNull).collect(Collectors.toSet());

    for (Module module : modules) {

      // =========================
      // MODULE BASIC STATS
      // =========================
      int courseworkCount = module.getAssignments() != null ? module.getAssignments().size() : 0;

      int assignedCredits = module.getAssignments() != null
          ? module.getAssignments().stream().mapToInt(Assignment::getCredits).sum()
          : 0;

      int studentsEnrolled =
          module.getRegistrations() != null
              ? (int) module.getRegistrations().stream().map(Registration::getUser)
                  .filter(u -> u instanceof Student).count()
              : 0;

      LecturerViewModulesDTO moduleDTO =
          new LecturerViewModulesDTO(module.getCode(), module.getName(), module.getCredits(),
              assignedCredits, courseworkCount, studentsEnrolled);

      // =========================
      // STUDENT DETAILS
      // =========================
      List<LecturerModuleStudentDTO> studentDTOs = new ArrayList<>();

      if (module.getRegistrations() != null && module.getAssignments() != null) {

        List<Student> students = module.getRegistrations().stream().map(Registration::getUser)
            .filter(u -> u instanceof Student).map(u -> (Student) u).toList();

        for (Student student : students) {

          int missedDeadlines = 0;

          List<Integer> marks = new ArrayList<>();

          boolean hasAnyPastAssignment = false;
          boolean hasUnmarkedPastDeadline = false;
          boolean hasAnySubmission = false;

          for (Assignment assignment : module.getAssignments()) {

            boolean isPastDeadline = assignment.getDeadline() != null
                && assignment.getDeadline().isBefore(java.time.LocalDateTime.now());

            if (isPastDeadline) {
              hasAnyPastAssignment = true;
            }

            List<AssignmentSubmission> subs =
                assignmentSubmissionRepository.findByAssignmentId(assignment.getId());

            AssignmentSubmission submission = subs.stream()
                .filter(s -> s.getStudent().getId() == student.getId()).findFirst().orElse(null);

            // =========================
            // NO SUBMISSION
            // =========================
            if (submission == null) {

              if (isPastDeadline) {
                marks.add(0); // penalty counts in average
                missedDeadlines++;
              }

              continue;
            }

            // =========================
            // SUBMISSION EXISTS
            // =========================
            hasAnySubmission = true;

            if (submission.getSubmittedAt() != null
                && submission.getSubmittedAt().isAfter(assignment.getDeadline())) {
              missedDeadlines++;
            }

            Integer mark = submission.getMark();

            if (mark != null) {
              marks.add(mark);
            } else {
              // submitted but not marked yet
              marks.add(0);

              if (isPastDeadline) {
                hasUnmarkedPastDeadline = true;
              }
            }
          }

          // =========================
          // AVG RULES
          // =========================
          double avg;

          if (!hasAnyPastAssignment && !hasAnySubmission) {
            avg = -1; // nothing has happened yet
          } else if (hasUnmarkedPastDeadline) {
            avg = -2; // marking backlog exists
          } else {
            avg = marks.stream().mapToInt(i -> i).average().orElse(0);
          }

          studentDTOs.add(new LecturerModuleStudentDTO(student.getId(),
              student.getFirstName() + " " + student.getLastName(), student.getEmail(),
              missedDeadlines, avg));
        }
      }

      result.add(new LecturerModuleExpandedDTO(moduleDTO, studentDTOs));
    }

    return ResponseEntity.ok(result);
  }

  // get modules and registered lecturers for s student
  @GetMapping("/getModules/{studentId}")
  public ResponseEntity<List<Map<String, Object>>> getModules(@PathVariable int studentId) {

    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new RuntimeException("Student not found"));

    List<Module> modules = moduleRepository.findModulesByStudentId(studentId);

    List<Map<String, Object>> response = new ArrayList<>();

    for (Module module : modules) {

      Map<String, Object> moduleMap = new HashMap<>();

      moduleMap.put("code", module.getCode());
      moduleMap.put("name", module.getName());
      moduleMap.put("credits", module.getCredits());

      // =========================
      // ASSIGNMENTS + AVERAGE
      // =========================
      List<Assignment> assignments = module.getAssignments();
      moduleMap.put("assignmentCount", assignments.size());

      double total = 0;
      int counted = 0;

      LocalDateTime now = LocalDateTime.now();

      for (Assignment assignment : assignments) {

        AssignmentSubmission submission = assignmentSubmissionRepository
            .findByStudentIdAndAssignmentId(studentId, assignment.getId()).orElse(null);

        if (submission != null && submission.isMarked()) {

          total += submission.getMark();
          counted++;

        } else if (assignment.getDeadline().isBefore(now)) {

          total += 0;
          counted++;
        }
      }

      double avg = counted == 0 ? 0 : total / counted;
      moduleMap.put("averageGrade", avg);

      // =========================
      // LECTURERS (FIXED)
      // =========================

      List<Map<String, Object>> lecturers = new ArrayList<>();

      for (Registration reg : module.getRegistrations()) {

        User user = reg.getUser();

        if (user instanceof Lecturer lecturer) {

          Map<String, Object> lecMap = new HashMap<>();
          lecMap.put("firstName", lecturer.getFirstName());
          lecMap.put("lastName", lecturer.getLastName());
          lecMap.put("email", lecturer.getEmail());

          lecturers.add(lecMap);
        }
      }

      moduleMap.put("lecturers", lecturers);

      response.add(moduleMap);
    }

    return ResponseEntity.ok(response);
  }
}
