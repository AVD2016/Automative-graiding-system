package uk.ac.rhul.cs2800.controller;

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

    System.out.println("execution started!!!!!!!!!!");
    try {

      /*
       * ========================= 1. LOAD LECTURER =========================
       */

      Lecturer lecturer = lecturerRepository.findById(lecturerId)
          .orElseThrow(() -> new RuntimeException("Lecturer not found"));

      /*
       * ========================= 2. GET MODULES (via registrations) =========================
       */

      List<Registration> registrations = lecturer.getRegistered();

      List<Module> modules =
          registrations.stream().map(Registration::getModule).distinct().toList();

      /*
       * ========================= 3. ACTIVITY FEED (latest submissions) =========================
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
       * ========================= 5. MODULE OVERVIEW =========================
       */

      List<Map<String, Object>> moduleData = new ArrayList<>();

      for (Module module : modules) {

        int totalSubmissions = 0;
        int marked = 0;

        for (Assignment assignment : module.getAssignments()) {

          List<AssignmentSubmission> subs =
              assignmentSubmissionRepository.findByAssignmentId(assignment.getId());

          totalSubmissions += subs.size();
          marked += subs.stream().filter(AssignmentSubmission::isMarked).count();
        }

        double submissionRate =
            totalSubmissions == 0 ? 0 : (double) totalSubmissions / (modules.size() * 10) * 100; // safe
                                                                                                 // fallback

        double avgGrade = 0;

        List<Integer> allMarks = new ArrayList<>();

        for (Assignment assignment : module.getAssignments()) {

          assignmentSubmissionRepository.findByAssignmentId(assignment.getId()).forEach(s -> {
            if (s.getMark() != null)
              allMarks.add(s.getMark());
          });
        }

        if (!allMarks.isEmpty()) {
          avgGrade = allMarks.stream().mapToInt(i -> i).average().orElse(0);
        }

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

      /*
       * ========================= FINAL RESPONSE =========================
       */

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
}
