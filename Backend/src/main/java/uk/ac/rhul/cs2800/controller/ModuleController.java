package uk.ac.rhul.cs2800.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Registration;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.repository.ModuleRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

@RestController
@RequestMapping("/api/module")
@CrossOrigin(originPatterns = "https://*.vercel.app")
public class ModuleController {

  @Autowired
  private ModuleRepository moduleRepository;

  @Autowired
  private StudentRepository studentRepository;

  @GetMapping("/getAvailableModules/{studentId}")
  public List<Map<String, Object>> getAvailableModules(@PathVariable int studentId) {

    List<Map<String, Object>> response = new ArrayList<>();

    Optional<Student> optionalStudent = studentRepository.findById(studentId);

    if (optionalStudent.isEmpty()) {
      return response;
    }

    Student student = optionalStudent.get();

    Iterable<Module> allModules = moduleRepository.findAll();

    for (Module module : allModules) {

      boolean registered = false;

      for (Registration registration : student.getRegistered()) {

        if (registration.getModule() != null
            && registration.getModule().getCode().equals(module.getCode())) {

          registered = true;
          break;
        }
      }

      Map<String, Object> moduleData = new HashMap<>();

      moduleData.put("code", module.getCode());
      moduleData.put("name", module.getName());
      moduleData.put("registered", registered);

      response.add(moduleData);
    }

    return response;
  }

  @PostMapping("/createModule")
  public ResponseEntity<?> createModule(@RequestBody Module module) {

    // CHECK IF MODULE ALREADY EXISTS

    Optional<Module> existingModule = moduleRepository.findById(module.getCode());

    if (existingModule.isPresent()) {

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Module already exists");
    }

    // SAVE MODULE

    Module savedModule = moduleRepository.save(module);

    return ResponseEntity.ok(savedModule);
  }
}
