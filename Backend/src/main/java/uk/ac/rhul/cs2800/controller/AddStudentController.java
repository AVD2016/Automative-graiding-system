package uk.ac.rhul.cs2800.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.dataObjects.StudentDTO;
import uk.ac.rhul.cs2800.dataObjects.StudentModuleDTO;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.repository.StudentRepository;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(originPatterns = "https://*.vercel.app") // allows frontend JS to call backend
public class AddStudentController {

  @Autowired
  private StudentRepository studentRepository;

  @PostMapping("/addStudent")
  public ResponseEntity<?> addStudent(@RequestBody Student student) {

    if (student == null) {
      return ResponseEntity.badRequest().body("Student cannot be null");
    }

    if (student.getUsername() == null || student.getUsername().isEmpty()) {
      return ResponseEntity.badRequest().body("Username is required");
    }

    if (student.getEmail() == null || student.getEmail().isEmpty()) {
      return ResponseEntity.badRequest().body("Email is required");
    }

    if (studentRepository.existsByUsername(student.getUsername())) {
      return ResponseEntity.badRequest().body("Username already exists");
    }

    if (studentRepository.existsById(student.getId())) {
      return ResponseEntity.badRequest().body("Username already exists");
    }

    Student savedStudent = studentRepository.save(student);

    return ResponseEntity.ok(savedStudent);
  }
  
  @PostMapping("/getStudents")
  public ResponseEntity<List<StudentDTO>> getAllStudents() {

    List<Student> students = (List<Student>) studentRepository.findAll();

    List<StudentDTO> dtoList = students.stream().map(student -> {

      List<StudentModuleDTO> modules = student.getRegistered().stream()
          .map(reg -> new StudentModuleDTO(reg.getModule().getCode(), reg.getModule().getName()))
          .toList();

      return new StudentDTO(student.getId(), student.getFirstName(), student.getLastName(),
          student.getUsername(), student.getEmail(), modules);

    }).toList();

    return ResponseEntity.ok(dtoList);
  }
}
