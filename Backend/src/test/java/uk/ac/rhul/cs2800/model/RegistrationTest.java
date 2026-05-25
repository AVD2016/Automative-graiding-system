package uk.ac.rhul.cs2800.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Registration;

/**
 * Test Class that tests functionality of Registration class.
 */
public class RegistrationTest {

  /**
   * Tests regular constructor of the Module class.
   */
  @Test
  void registrationConstractorTest() {
    // Test 1
    Module module1 = new Module("cs1600", "Mathematical Stractures", true);
    Registration registration1 = new Registration(module1);
    assertEquals("cs1600", registration1.getModule().getCode());
  }

  /**
   * Tests default constructor and setter of the Module class.
   */
  @Test
  void RegistrationSetterTest() {
    // Test 2
    // Modified for coursework2
    Module module2 = new Module("cs1850", "Machine Fundamentals", true);
    Student student =
        new Student(918123001, "Bob", "Morris", "Morisson", "boringemail96@gmail.com");
    Registration registration2 = new Registration();
    registration2.setModule(module2);
    registration2.setId(10012);
    registration2.setStudent(student);
    assertEquals("Machine Fundamentals", registration2.getModule().getName());
    assertEquals(10012, registration2.getId());
    assertEquals(918123001, registration2.getStudent().getId());
  }
}
