package uk.ac.rhul.cs2800.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.rhul.cs2800.model.Assignment;
import uk.ac.rhul.cs2800.repository.AssignmentRepository;
import uk.ac.rhul.cs2800.repository.ModuleRepository;

@RestController
@RequestMapping("/api/assignment")
@CrossOrigin(originPatterns = "https://*.vercel.app")
public class AssignmentController {

  @Autowired
  private AssignmentRepository assignmentRepository;

  @Autowired
  private ModuleRepository moduleRepository;

  // =========================
  // CREATE ASSIGNMENT
  // =========================
  @PostMapping("/create")
  public ResponseEntity<?> createAssignment(@RequestParam String moduleCode,
      @RequestParam int credits, @RequestParam String deadline,
      @RequestParam String taskDescription, @RequestParam String markingCriteria,
      @RequestParam MultipartFile file) {

    try {

      // 1. Find module
      uk.ac.rhul.cs2800.model.Module module = moduleRepository.findById(moduleCode)
          .orElseThrow(() -> new RuntimeException("Module not found"));

      // 2. Validate credit limit
      int usedCredits = module.getAssignments().stream().mapToInt(Assignment::getCredits).sum();

      if (usedCredits + credits > module.getCredits()) {
        return ResponseEntity.badRequest().body("Not enough available module credits");
      }

      // 3. Save PDF locally (simple version)
      String uploadDir = "uploads/";

      File dir = new File(uploadDir);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
      File destination = new File(uploadDir + fileName);

      file.transferTo(destination);

      // 4. Create assignment
      Assignment assignment = new Assignment();
      assignment.setCredits(credits);
      assignment.setTaskDescription(taskDescription);
      assignment.setMarkingCriteria(markingCriteria);
      assignment.setDeadline(LocalDateTime.parse(deadline));
      assignment.setPdfFilePath(destination.getPath());

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
}
