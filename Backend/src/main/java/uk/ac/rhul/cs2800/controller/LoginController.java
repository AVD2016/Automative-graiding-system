package uk.ac.rhul.cs2800.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.model.Administrator;
import uk.ac.rhul.cs2800.model.Lecturer;
import uk.ac.rhul.cs2800.model.LoginRequest;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.repository.AdministratorRepository;
import uk.ac.rhul.cs2800.repository.LecturerRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://automative-graiding-system.vercel.app")
public class LoginController {

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private LecturerRepository lecturerRepository;

  @Autowired
  private AdministratorRepository administratorRepository;

  @PostMapping("/login")
  public Object login(@RequestBody LoginRequest request) {

    String username = request.getUsername();
    String password = request.getPassword();
    String userType = request.getUserType();

    switch (userType) {

      case "student":

        Optional<Student> optionalStudent = studentRepository.findByUsername(username);

        if (optionalStudent.isEmpty()) {
          return ResponseEntity.status(401).body("Invalid username or password");
        }

        Student student = optionalStudent.get();

        if (!student.getPassword().equals(password)) {
          return ResponseEntity.status(401).body("Invalid username or password");
        }

        return student;


      case "lecturer":

        Optional<Lecturer> optionalLecturer = lecturerRepository.findByUsername(username);
        if (optionalLecturer.isEmpty()) {
          return ResponseEntity.status(401).body("Invalid username or password");
        }

        Lecturer lecturer = optionalLecturer.get();

        if (!lecturer.getPassword().equals(password)) {
          return ResponseEntity.status(401).body("Invalid username or password");
        }

        return lecturer;


      case "administrator":

        Optional<Administrator> optionalAdmin = administratorRepository.findByUsername(username);

        if (optionalAdmin.isEmpty()) {
          return ResponseEntity.status(401).body("Invalid username or password");
        }

        Administrator admin = optionalAdmin.get();

        if (!admin.getPassword().equals(password)) {
          return ResponseEntity.status(401).body("Invalid username or password");
        }

        return admin;


      default:
        throw new RuntimeException("Invalid user type");
    }
  }
}