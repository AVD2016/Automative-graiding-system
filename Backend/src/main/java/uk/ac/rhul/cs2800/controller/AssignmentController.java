package uk.ac.rhul.cs2800.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  @Value("${HelpChatAPIKey}")
  private String apiKey;

  private static final Logger log = LoggerFactory.getLogger(AssignmentController.class);

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

  // get assignments for Student
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


  @DeleteMapping("/deleteSubmission/{assignmentId}")
  public ResponseEntity<?> deleteSubmission(@PathVariable int assignmentId,
      @RequestParam int studentId) {

    try {

      AssignmentSubmission submission =
          assignmentSubmissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId)
              .orElseThrow(() -> new RuntimeException("Submission not found for studentId="
                  + studentId + ", assignmentId=" + assignmentId));

      assignmentSubmissionRepository.delete(submission);

      return ResponseEntity.ok("Submission deleted");

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(e.getMessage());
    }
  }

  // submit assignment Student
  @PostMapping("/submit/{assignmentId}")
  public ResponseEntity<?> submitAssignment(@PathVariable int assignmentId,
      @RequestParam int studentId, @RequestParam("file") MultipartFile file) {

    try {

      // 1. LOAD ASSIGNMENT
      Assignment assignment = assignmentRepository.findById(assignmentId)
          .orElseThrow(() -> new RuntimeException("Assignment not found"));

      // 2. LOAD STUDENT
      Student student = studentRepository.findById(studentId)
          .orElseThrow(() -> new RuntimeException("Student not found"));


      // 3. SAFE UPLOAD DIRECTORY
      Path uploadPath = Paths.get(System.getProperty("java.io.tmpdir"), "uploads");

      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      // 4. SAFE FILE NAME
      String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

      Path destination = uploadPath.resolve(fileName);

      // 5. SAVE FILE
      file.transferTo(destination.toFile());

      // 6. CHECK IF SUBMISSION ALREADY EXISTS
      Optional<AssignmentSubmission> existingSubmission =
          assignmentSubmissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId);

      AssignmentSubmission submission;

      if (existingSubmission.isPresent()) {

        // OPTION A: BLOCK resubmission
        return ResponseEntity.status(409).body("You have already submitted this assignment.");

      } else {
        submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setMarked(false);
        submission.setMark(null);
        submission.setPdfFiles(new ArrayList<>(List.of(destination.toString())));
      }

      // =========================
      // 7. SAVE TO DB
      // =========================
      assignmentSubmissionRepository.save(submission);

      // fire-and-forget (does NOT block response)
      try {
        createReviewAsync(submission.getId());
      } catch (Exception ignored) {
        ignored.printStackTrace();
      }

      return ResponseEntity.ok("Submission created");

    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(e.getMessage());
    }
  }

  // method to request the review by LLM
  @Async
  @Transactional
  public void createReviewAsync(int submissionId) {

    log.info("LLM review START submissionId={}", submissionId);

    try {
      AssignmentSubmission submission = assignmentSubmissionRepository.findById(submissionId)
          .orElseThrow(() -> new RuntimeException("Submission not found: " + submissionId));

      Assignment assignment = submission.getAssignment();

      if (submission.getPdfFiles() == null || submission.getPdfFiles().isEmpty()) {
        throw new RuntimeException("No PDF file linked to submissionId=" + submissionId);
      }

      String filePath = submission.getPdfFiles().get(0);
      log.info("Extracting PDF for submissionId={}, file={}", submissionId, filePath);

      String submissionText = extractTextFromPdf(filePath);

      if (submissionText == null || submissionText.isBlank()) {
        throw new RuntimeException("Empty PDF text for submissionId=" + submissionId);
      }

      String prompt = buildPrompt(assignment, submissionText, submission.getSubmittedAt(),
          assignment.getDeadline());

      log.info("Sending LLM request submissionId={}", submissionId);

      log.info("PROMPT LENGTH submissionId={} chars={}", submissionId, prompt.length());

      log.debug("PROMPT PREVIEW submissionId={} preview={}", submissionId,
          prompt.substring(0, Math.min(prompt.length(), 2000)));

      String rawResponse = callLLM(prompt);

      log.info("LLM RAW response submissionId={} response={}", submissionId, rawResponse);

      ObjectMapper mapper = new ObjectMapper();

      JsonNode root = mapper.readTree(rawResponse);

      JsonNode choices = root.path("choices");

      if (!choices.isArray() || choices.isEmpty()) {
        throw new RuntimeException("Invalid LLM response structure");
          }

      String content = choices.get(0).path("message").path("content").asText(null);

      if (content == null || content.isBlank()) {
        throw new RuntimeException("Empty LLM content");
      }

      content = content.replace("```json", "").replace("```", "").trim();

      JsonNode result = mapper.readTree(content);

      String feedback = result.path("feedback").asText(null);
      int grade = result.path("grade").asInt(-1);

      if (feedback == null || grade < 0) {
        throw new RuntimeException("Invalid parsed LLM JSON: " + content);
      }

      submission.setFeedbackForAssignment(feedback);
      submission.setSuggestedGrade(grade);
      submission.setMarked(true);

      assignmentSubmissionRepository.saveAndFlush(submission);

      log.info("LLM review SUCCESS submissionId={} grade={}", submissionId, grade);

      } catch (Exception e) {
      log.error("LLM review FAILED submissionId={}", submissionId, e);
      }
  }

  private String extractTextFromPdf(String filePath) {

    try {
      File file = new File(filePath);

      if (!file.exists()) {
        throw new RuntimeException("PDF not found: " + filePath);
      }

      Tika tika = new Tika();
      String text = tika.parseToString(file);

      if (text == null || text.isBlank()) {
        throw new RuntimeException("Empty extracted PDF text: " + filePath);
      }

      return text;

    } catch (Exception e) {
      log.error("PDF extraction FAILED filePath={}", filePath, e);
      throw new RuntimeException("PDF extraction failed", e);
    }
  }

  // helper method for debuging
  private String callLLM(String prompt) {

    try {
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(apiKey);

      String safePrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");

      String body = """
          {
            "model": "openrouter/owl-alpha",
            "messages": [
              {
                "role": "user",
                "content": "%s"
              }
            ]
          }
          """.formatted(safePrompt);

      HttpEntity<String> entity = new HttpEntity<>(body, headers);

      ResponseEntity<String> response = restTemplate.exchange(
          "https://openrouter.ai/api/v1/chat/completions", HttpMethod.POST, entity, String.class);

      log.info("LLM STATUS: {}", response.getStatusCode());
      log.info("LLM BODY: {}", response.getBody());

      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new RuntimeException(
            "LLM ERROR status=" + response.getStatusCode() + " body=" + response.getBody());
      }

      return response.getBody();

    } catch (Exception e) {
      log.error("LLM request FAILED FULL TRACE", e);
      throw new RuntimeException(e);
    }
  }

// build promt helper method
private String buildPrompt(Assignment assignment, String submissionText, LocalDateTime submittedAt,
    LocalDateTime deadline) {

  return """
      You are an academic grader.
      TASK DESCRIPTION:
      %s
      MARKING CRITERIA:
      %s
      DEADLINE:
      %s
      SUBMISSION TIME:
      %s
      STUDENT SUBMISSION:
      %s  
      INSTRUCTIONS:
      - Grade the work fairly using the marking criteria.
      - Apply late penalties 0–24 hours late: -10% penalty, more than 24 hours late: grade = 0
      - Provide feedback for the professor. Return STRICT JSON only. Do NOT use markdown.
      - Do NOT include explanation outside JSON.

      REQUIRED RESPONSE FORMAT:
      {
        "feedback": "string",
        "grade": 0
      }
      """.formatted(assignment.getTaskDescription(), assignment.getMarkingCriteria(), deadline,
      submittedAt, submissionText);
}
}
