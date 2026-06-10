package uk.ac.rhul.cs2800.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.model.Assignment;
import uk.ac.rhul.cs2800.model.AssignmentSubmission;
import uk.ac.rhul.cs2800.model.Lecturer;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Registration;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.repository.AssignmentSubmissionRepository;
import uk.ac.rhul.cs2800.repository.LecturerRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(originPatterns = "https://*.vercel.app")
public class StatisticsController {

  @Autowired
  private LecturerRepository lecturerRepository;
  @Autowired
  private AssignmentSubmissionRepository assignmentSubmissionRepository;
  @Autowired
  private StudentRepository studentRepository;

  @Transactional
  @GetMapping("/lecturer/{lecturerId}")
  public ResponseEntity<?> getLecturerDashboard(@PathVariable int lecturerId) {

    System.out.println("execution started!");

    try {

      /*
       * ========================= 1. LOAD LECTURER =========================
       */

      Lecturer lecturer = lecturerRepository.findById(lecturerId)
          .orElseThrow(() -> new RuntimeException("Lecturer not found"));

      /*
       * ========================= 2. GET MODULES =========================
       */

      List<Registration> registrations = lecturer.getRegistered();

      List<Module> modules =
          registrations.stream().map(Registration::getModule).distinct().toList();

      /*
       * ========================= 3. ACTIVITY FEED =========================
       */

      List<Map<String, Object>> activityFeed = new ArrayList<>();

      for (Module module : modules) {

        for (Assignment assignment : module.getAssignments()) {

          List<AssignmentSubmission> submissions =
              assignmentSubmissionRepository.findByAssignmentId(assignment.getId());

          for (AssignmentSubmission sub : submissions) {

            Map<String, Object> item = new HashMap<>();

            item.put("studentName",
                sub.getStudent().getFirstName() + " " + sub.getStudent().getLastName());

            item.put("assignmentTitle", assignment.getTitle());
            item.put("timestamp", sub.getSubmittedAt());

            activityFeed.add(item);
          }
        }
      }

      /*
       * ========================= 4. AT RISK STUDENTS =========================
       */

      Map<Integer, List<Integer>> studentGrades = new HashMap<>();

      for (Module module : modules) {

        for (Assignment assignment : module.getAssignments()) {

          List<AssignmentSubmission> submissions =
              assignmentSubmissionRepository.findByAssignmentId(assignment.getId());

          for (AssignmentSubmission sub : submissions) {

            if (sub.getMark() != null) {

              studentGrades.computeIfAbsent(sub.getStudent().getId(), k -> new ArrayList<>())
                  .add(sub.getMark());
            }
          }
        }
      }

      List<Map<String, Object>> atRiskStudents = new ArrayList<>();

      for (Map.Entry<Integer, List<Integer>> entry : studentGrades.entrySet()) {

        double avg = entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(0);

        Student s = studentRepository.findById(entry.getKey()).orElse(null);

        if (s == null)
          continue;

        Map<String, Object> map = new HashMap<>();
        map.put("name", s.getFirstName() + " " + s.getLastName());
        map.put("email", s.getEmail());
        map.put("avg", avg);

        atRiskStudents.add(map);
      }

      /*
       * ========================= 5. MODULE OVERVIEW (FIXED SUBMISSION RATE)
       * =========================
       */

      List<Map<String, Object>> moduleData = new ArrayList<>();

      for (Module module : modules) {

        List<Registration> moduleRegs = module.getRegistrations();
        int enrolledStudents = moduleRegs != null ? moduleRegs.size() : 0;

        int totalAssignments = module.getAssignments() != null ? module.getAssignments().size() : 0;

        int totalSubmissions = 0;
        int marked = 0;

        for (Assignment assignment : module.getAssignments()) {

          List<AssignmentSubmission> subs =
              assignmentSubmissionRepository.findByAssignmentId(assignment.getId());

          totalSubmissions += subs.size();
          marked += subs.stream().filter(AssignmentSubmission::isMarked).count();
        }

        /*
         * FIX: correct expected submissions (only enrolled students × assignments)
         */

        int expectedSubmissions = enrolledStudents * totalAssignments;

        double submissionRate =
            expectedSubmissions == 0 ? 0 : ((double) totalSubmissions / expectedSubmissions) * 100;

        /*
         * AVG GRADE
         */

        List<Integer> allMarks = new ArrayList<>();

        for (Assignment assignment : module.getAssignments()) {

          assignmentSubmissionRepository.findByAssignmentId(assignment.getId()).forEach(s -> {
            if (s.getMark() != null) {
              allMarks.add(s.getMark());
            }
          });
        }

        double avgGrade =
            allMarks.isEmpty() ? 0 : allMarks.stream().mapToInt(i -> i).average().orElse(0);

        Map<String, Object> map = new HashMap<>();
        map.put("code", module.getCode());
        map.put("submissionRate", Math.min(100, submissionRate));
        map.put("avgGrade", avgGrade);
        map.put("markedCount", marked);
        map.put("unmarkedCount", totalSubmissions - marked);

        moduleData.add(map);
      }

      /*
       * ========================= 6. MARKING QUEUE =========================
       */

      List<Map<String, Object>> markingQueue = new ArrayList<>();

      for (Module module : modules) {

        for (Assignment assignment : module.getAssignments()) {

          List<AssignmentSubmission> subs =
              assignmentSubmissionRepository.findByAssignmentId(assignment.getId());

          long unmarked = subs.stream().filter(s -> !s.isMarked()).count();

          if (unmarked > 0) {

            Map<String, Object> map = new HashMap<>();
            map.put("title", assignment.getTitle());
            map.put("deadline", assignment.getDeadline());
            map.put("unmarkedCount", unmarked);

            markingQueue.add(map);
          }
        }
      }



      Map<String, Object> response = new HashMap<>();
      response.put("activityFeed", activityFeed);
      response.put("atRiskStudents", atRiskStudents);
      response.put("modules", moduleData);
      response.put("markingQueue", markingQueue);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  @GetMapping("/student/{studentId}")
  @Transactional
  public ResponseEntity<?> getStudentDashboard(@PathVariable int studentId) {

    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new RuntimeException("Student not found"));

    List<Registration> registrations = student.getRegistered();

    List<Map<String, Object>> assignmentDTOs = new ArrayList<>();

    LocalDateTime now = LocalDateTime.now();

    double totalMarks = 0;
    int countForAverage = 0;

    for (Registration reg : registrations) {

      Module module = reg.getModule();

      if (module.getAssignments() == null)
        continue;

      for (Assignment assignment : module.getAssignments()) {

        AssignmentSubmission submission = assignmentSubmissionRepository
            .findByStudentIdAndAssignmentId(studentId, assignment.getId()).orElse(null);

        LocalDateTime deadline = assignment.getDeadline();

        boolean hasSubmission = submission != null;
        boolean isMarked = hasSubmission && submission.isMarked();
        boolean isSubmitted = hasSubmission && submission.getSubmittedAt() != null;

        String status;

        if (isMarked) {

          boolean isLate = submission.getSubmittedAt() != null && deadline != null
              && submission.getSubmittedAt().isAfter(deadline);

          status = isLate ? "LATE" : "MARKED";

        } else if (isSubmitted) {

          status = "SUBMITTED";

        } else if (deadline != null && deadline.isBefore(now)) {

          status = "UNSUBMITTED";

        } else {

          status = "FUTURE";
        }

        Double gradeResult = calculateAssignmentContribution(assignment, submission, now);

        if (gradeResult != null) {

          totalMarks += gradeResult;
          countForAverage++;
        }

        Map<String, Object> dto = new HashMap<>();

        dto.put("title", assignment.getTitle());
        dto.put("module", module.getCode());
        dto.put("deadline", assignment.getDeadline());
        dto.put("status", status);
        dto.put("credits", assignment.getCredits());

        assignmentDTOs.add(dto);
      }
    }

    double avgGrade = countForAverage == 0 ? 0 : totalMarks / countForAverage;

    Map<String, Object> response = new HashMap<>();

    response.put("assignments", assignmentDTOs);
    response.put("avgGrade", avgGrade);

    return ResponseEntity.ok(response);
  }

  // for grades page student
  @GetMapping("/student/grades/{studentId}")
  public ResponseEntity<List<Map<String, Object>>> getStudentGrades(@PathVariable int studentId) {

    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new RuntimeException("Student not found"));

    List<Map<String, Object>> response = new ArrayList<>();

    LocalDateTime now = LocalDateTime.now();

    for (Registration reg : student.getRegistered()) {

      Module module = reg.getModule();

      if (module == null)
        continue;

      Map<String, Object> moduleMap = new HashMap<>();

      moduleMap.put("code", module.getCode());
      moduleMap.put("name", module.getName());
      moduleMap.put("credits", module.getCredits());

      List<Assignment> assignments = module.getAssignments();

      int assignmentCount = assignments != null ? assignments.size() : 0;

      moduleMap.put("assignmentCount", assignmentCount);

      if (assignments == null || assignments.isEmpty()) {

        moduleMap.put("averageGrade", -1);
        response.add(moduleMap);
        continue;
      }

      double total = 0;
      int counted = 0;

      for (Assignment assignment : assignments) {

        AssignmentSubmission submission = assignmentSubmissionRepository
            .findByStudentIdAndAssignmentId(studentId, assignment.getId()).orElse(null);

        Double gradeResult = calculateAssignmentContribution(assignment, submission, now);

        if (gradeResult != null) {

          total += gradeResult;
          counted++;
        }
      }

      double avg = counted == 0 ? -1 : total / counted;

      moduleMap.put("averageGrade", avg);

      response.add(moduleMap);
    }

    return ResponseEntity.ok(response);
  }

  // helper method to calculate average
  private Double calculateAssignmentContribution(Assignment assignment,
      AssignmentSubmission submission, LocalDateTime now) {

    LocalDateTime deadline = assignment.getDeadline();

    boolean isPastDeadline = deadline != null && deadline.isBefore(now);

    boolean hasSubmission = submission != null;

    boolean isMarked = hasSubmission && submission.isMarked();

    // CASE 1:
    // Marked assignment

    if (isMarked) {

      boolean isLate = submission.getSubmittedAt() != null && deadline != null
          && submission.getSubmittedAt().isAfter(deadline);

      // late submission = 0
      if (isLate) {
        return 0.0;
      }

      return (double) submission.getMark();
    }

    // CASE 2:
    // No submission + missed deadline = 0

    if (!hasSubmission && isPastDeadline) {
      return 0.0;
    }

    // CASE 3:
    // Future / unmarked assignments ignored

    return null;
  }
}
