package uk.ac.rhul.cs2800.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.dataObjects.LoginDTO;
import uk.ac.rhul.cs2800.model.Administrator;
import uk.ac.rhul.cs2800.model.LoginRequest;
import uk.ac.rhul.cs2800.model.User;
import uk.ac.rhul.cs2800.repository.AdministratorRepository;
import uk.ac.rhul.cs2800.repository.LecturerRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "https://*.vercel.app")
public class LoginController {

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private LecturerRepository lecturerRepository;

  @Autowired
  private AdministratorRepository administratorRepository;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {

    String username = request.getUsername();
    String password = request.getPassword();
    String userType = request.getUserType();

    User user;
    String role;

    switch (userType) {

      case "student":
        user = authenticate(username, password, studentRepository.findByUsername(username));
        role = "student";
        break;

      case "lecturer":
        user = authenticate(username, password, lecturerRepository.findByUsername(username));
        role = "lecturer";
        break;

      case "administrator": {
        Optional<Administrator> adminOpt = administratorRepository.findByUsername(username);

        if (adminOpt.isEmpty()) {
          return ResponseEntity.status(401).body("Invalid username or password");
        }

        Administrator admin = adminOpt.get();

        if (admin.getPassword() == null || !admin.getPassword().equals(password)) {

          return ResponseEntity.status(401).body("Invalid username or password");
        }

        return ResponseEntity.ok(new LoginDTO(admin.getId(), admin.getFirstName(),
            admin.getLastName(), "administrator"));
      }

      default:
        return ResponseEntity.badRequest().body("Invalid user type");
    }

    if (user == null) {
      return ResponseEntity.status(401).body("Invalid username or password");
    }

    return ResponseEntity.ok(toLoginDTO(user, role));
  }

  // helper methods
  private LoginDTO toLoginDTO(User user, String role) {
    return new LoginDTO(user.getId(), user.getFirstName(), user.getLastName(), role);
  }

  private <T extends User> T authenticate(String username, String password, Optional<T> userOpt) {

    if (userOpt.isEmpty()) {
      return null;
    }

    T user = userOpt.get();

    if (user.getPassword() == null || !user.getPassword().equals(password)) {

      return null;
    }

    return user;
  }

}