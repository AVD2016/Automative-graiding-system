package uk.ac.rhul.cs2800.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.model.Lecturer;
import uk.ac.rhul.cs2800.repository.LecturerRepository;

@RestController
@RequestMapping("/api/lecturer")
@CrossOrigin(originPatterns = "https://*.vercel.app")
public class AddLecturerController {

  @Autowired
  private LecturerRepository lecturerRepository;

  /**
   * Adds a new lecturer to the system.
   */
  @PostMapping("/addLecturer")
  public ResponseEntity<?> addLecturer(@RequestBody Lecturer lecturer) {

    if (lecturer == null) {
      return ResponseEntity.badRequest().body("Lecturer cannot be null");
    }

    if (lecturer.getUsername() == null || lecturer.getUsername().isEmpty()) {
      return ResponseEntity.badRequest().body("Username is required");
    }

    if (lecturer.getEmail() == null || lecturer.getEmail().isEmpty()) {
      return ResponseEntity.badRequest().body("Email is required");
    }

    if (lecturerRepository.existsByUsername(lecturer.getUsername())) {
      return ResponseEntity.badRequest().body("Username already exists");
    }

    if (lecturerRepository.existsById(lecturer.getId())) {
      return ResponseEntity.badRequest().body("ID already exists");
    }

    Lecturer saved = lecturerRepository.save(lecturer);

    return ResponseEntity.ok(saved);
  }
}
