package uk.ac.rhul.cs2800.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.model.Grade;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.repository.GradeRepository;
import uk.ac.rhul.cs2800.repository.ModuleRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

/**
 * REST controller responsible for handling grade assignments for students in specific modules.
 */
@RestController
public class GradeController {

  GradeRepository gradeRepository;
  ModuleRepository moduleRepository;
  StudentRepository studentRepository;

  /**
   * Constructs a new {@code GradeController} with the required repositories.
   *
   * @param gradeRepository the repository used to store and retrieve grades
   * @param moduleRepository the repository used to retrieve module entities
   * @param studentRepository the repository used to retrieve student entities
   */
  public GradeController(GradeRepository gradeRepository, ModuleRepository moduleRepository,
      StudentRepository studentRepository) {
    this.gradeRepository = gradeRepository;
    this.moduleRepository = moduleRepository;
    this.studentRepository = studentRepository;
  }

  /**
   * Creates a new grade entry linking a student to a module.
   *
   * @param params a map containing the parameters necessary to create the grade
   * @return a {@link ResponseEntity} containing the created {@link Grade}
   *
   * @throws java.util.NoSuchElementException if either the student or module does not exist
   */
  @PostMapping(value = "/grades/addGrade")
  public ResponseEntity<Grade> addGrade(@RequestBody Map<String, String> params) {
    Student student =
        studentRepository.findById(Integer.parseInt(params.get("student_id"))).orElseThrow();

    Module module = moduleRepository.findById(params.get("module_code")).orElseThrow();

    Grade grade = new Grade();

    grade.setStudent(student);
    grade.setModule(module);
    grade.setScore(Integer.parseInt(params.get("score")));

    grade = gradeRepository.save(grade);

    return ResponseEntity.ok(grade);
  }

}
