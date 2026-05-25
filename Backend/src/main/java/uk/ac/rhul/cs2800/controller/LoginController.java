package uk.ac.rhul.cs2800.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

      case "STUDENT":

        Student student = studentRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.getPassword().equals(password)) {
          throw new RuntimeException("Invalid password");
        }

        return student;


      case "LECTURER":

        Lecturer lecturer = lecturerRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        if (!lecturer.getPassword().equals(password)) {
          throw new RuntimeException("Invalid password");
        }

        return lecturer;


      case "ADMINISTRATOR":

        Administrator admin = administratorRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Administrator not found"));

        if (!admin.getPassword().equals(password)) {
          throw new RuntimeException("Invalid password");
        }

        return admin;


      default:
        throw new RuntimeException("Invalid user type");
    }
  }
}