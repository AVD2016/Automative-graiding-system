package uk.ac.rhul.cs2800.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.rhul.cs2800.model.Grade;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Student;
import uk.ac.rhul.cs2800.repository.ModuleRepository;
import uk.ac.rhul.cs2800.repository.RegistrationRepository;
import uk.ac.rhul.cs2800.repository.StudentRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class GradeControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private ModuleRepository moduleRepository;

  @Autowired
  private RegistrationRepository gradeRepository;

  @BeforeEach
  void beforeEach() {
    Student student = new Student(101, "Vlad", "Abram", "AVD", "znac588@live.rhul.ac.uk");
    student = studentRepository.save(student);

    Module module = new Module("CS2800", "Computer science", true);
    module = moduleRepository.save(module);
  }

  @Test
  void addGradeTest() throws JsonProcessingException, Exception {
    Map<String, String> params = new HashMap<String, String>();
    params.put("score", "85");
    params.put("student_id", "101");
    params.put("module_code", "CS2800");

    MvcResult action = mockMvc
        .perform(MockMvcRequestBuilders.post("/grades/addGrade")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(params)).accept(MediaType.APPLICATION_JSON))
        .andReturn();

    assertEquals(HttpStatus.OK.value(), action.getResponse().getStatus());
    Grade grade = objectMapper.readValue(action.getResponse().getContentAsString(), Grade.class);
    assertNotNull(grade.getId());
    assertEquals("CS2800", grade.getModule().getCode());
    assertEquals(101, grade.getStudent().getId());
    assertEquals(85, grade.getScore());
  }
}
