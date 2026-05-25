package uk.ac.rhul.cs2800.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs2800.repository.AdministratorRepository;
import uk.ac.rhul.cs2800.repository.LecturerRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

@Service
public class AuthService {

  @Autowired
  private StudentRepository studentRepo;

  @Autowired
  private LecturerRepository lecturerRepo;

  @Autowired
  private AdministratorRepository adminRepo;

  public Object login(String username, String password, Role role) {

    switch (role) {

      case STUDENT:
        Student s = studentRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Not found"));
        if (!s.getPassword().equals(password))
          throw new RuntimeException("Wrong password");
        return s;

      case LECTURER:
        Lecturer l = lecturerRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Not found"));
        if (!l.getPassword().equals(password))
          throw new RuntimeException("Wrong password");
        return l;

      case ADMIN:
        Administrator a =
            adminRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("Not found"));
        if (!a.getPassword().equals(password))
          throw new RuntimeException("Wrong password");
        return a;

      default:
        throw new RuntimeException("Invalid role");
    }
  }
}
