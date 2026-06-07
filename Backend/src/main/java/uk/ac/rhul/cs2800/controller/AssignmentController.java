package uk.ac.rhul.cs2800.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.rhul.cs2800.model.Assignment;
import uk.ac.rhul.cs2800.model.AssignmentSubmission;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Registration;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.repository.AssignmentRepository;
import uk.ac.rhul.cs2800.repository.AssignmentSubmissionRepository;
import uk.ac.rhul.cs2800.repository.ModuleRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

@RestController
@RequestMapping("/api/assignment")
@CrossOrigin(originPatterns = "https://*.vercel.app")
public class AssignmentController {

  @Autowired
  private AssignmentRepository assignmentRepository;

  @Autowired
  private ModuleRepository moduleRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private AssignmentSubmissionRepository assignmentSubmissionRepository;

  // CREATE ASSIGNMENT by lecturer
  @PostMapping("/create")
  public ResponseEntity<?> createAssignment(@RequestParam String moduleCode,
      @RequestParam String title,
      @RequestParam int credits, @RequestParam String deadline,
      @RequestParam String taskDescription, @RequestParam String markingCriteria,
      @RequestParam(value = "file", required = false) MultipartFile file) {

    try {

      // 1. Find module
      uk.ac.rhul.cs2800.model.Module module = moduleRepository.findById(moduleCode)
          .orElseThrow(() -> new RuntimeException("Module not found"));

      // 2. Validate credit limit
      int usedCredits = module.getAssignments().stream().mapToInt(Assignment::getCredits).sum();

      if (usedCredits + credits > module.getCredits()) {
        return ResponseEntity.badRequest().body("Not enough available module credits");
      }

      String pdfPath = null;

      // ONLY save file if it exists
      if (file != null && !file.isEmpty()) {

        String uploadDir = "uploads/";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
          dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        File destination = new File(uploadDir + fileName);

        file.transferTo(destination);

        pdfPath = destination.getPath();
      }

      // 4. Create assignment
      Assignment assignment = new Assignment();
      assignment.setTitle(title); // ✅ ADD THIS
      assignment.setCredits(credits);
      assignment.setTaskDescription(taskDescription);
      assignment.setMarkingCriteria(markingCriteria);
      assignment.setDeadline(LocalDateTime.parse(deadline));
      assignment.setPdfFilePath(pdfPath);

      // 5. Link via module (IMPORTANT)
      module.addAssignment(assignment);

      // 6. Save (cascade handles assignment)
      moduleRepository.save(module);

      return ResponseEntity.ok("Assignment created successfully");

    } catch (IOException e) {
      return ResponseEntity.status(500).body("File upload failed");
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
  }

  @GetMapping("/student/getAssignments/{studentId}")
  public ResponseEntity<?> getStudentAssignments(@PathVariable int studentId) {

    // 1. Load student
    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new RuntimeException("Student not found"));

    // 2. Get modules student is registered on
    List<Registration> registrations = student.getRegistered();

    List<Module> modules = registrations.stream().map(Registration::getModule).toList();

    // 3. Collect assignments
    List<Assignment> assignments =
        modules.stream().flatMap(module -> module.getAssignments().stream()).toList();

    // 4. Build response with submission info
    List<Map<String, Object>> response = assignments.stream().map(a -> {

      Map<String, Object> map = new HashMap<>();

      map.put("id", a.getId());
      map.put("title", a.getTitle());
      map.put("taskDescription", a.getTaskDescription());
      map.put("markingCriteria", a.getMarkingCriteria());
      map.put("credits", a.getCredits());
      map.put("deadline", a.getDeadline());
      map.put("pdfFilePath", a.getPdfFilePath());

      map.put("moduleCode", a.getModule().getCode());
      map.put("moduleName", a.getModule().getName());

      // =========================
      // SUBMISSION LOGIC (NEW)
      // =========================

      Optional<AssignmentSubmission> submissionOpt =
          assignmentSubmissionRepository.findByStudentIdAndAssignmentId(studentId, a.getId());

      if (submissionOpt.isPresent()) {

        AssignmentSubmission submission = submissionOpt.get();

        map.put("submitted", true);
        map.put("submittedDate", submission.getSubmittedAt());
        map.put("mark", submission.getMark());

      } else {

        map.put("submitted", false);
        map.put("submittedDate", null);
        map.put("mark", null);
      }

      return map;
    }).toList();

    return ResponseEntity.ok(response);
  }

  // get details on a assignment
  @GetMapping("/getAssignmentDetails/{id}")
  public ResponseEntity<?> getAssignmentDetails(@PathVariable int id) {

    Assignment assignment = assignmentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Assignment not found"));

    Map<String, Object> response = new HashMap<>();

    // =========================
    // BASIC INFO
    // =========================
    response.put("id", assignment.getId());
    response.put("title", assignment.getTitle());

    // frontend expects "description"
    response.put("description", assignment.getTaskDescription());

    response.put("markingCriteria", assignment.getMarkingCriteria());
    response.put("credits", assignment.getCredits());
    response.put("deadline", assignment.getDeadline());

    // =========================
    // FILES (adapt single file → list)
    // =========================
    List<Map<String, Object>> files = new ArrayList<>();

    if (assignment.getPdfFilePath() != null) {

      Map<String, Object> file = new HashMap<>();
      file.put("name", "Assignment PDF");

      // If you later serve static files, this should be a URL
      file.put("url", "/files/" + new File(assignment.getPdfFilePath()).getName());

      files.add(file);
    }

    response.put("files", files);

    return ResponseEntity.ok(response);
  }

}
