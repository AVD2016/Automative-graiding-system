package uk.ac.rhul.cs2800.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs2800.model.Grade;
import uk.ac.rhul.cs2800.model.Module;

/**
 * Test class to test functionality of the Grade class.
 */
public class GradeTests {

  /**
   * Tests regular Module constructor, and getter for the Score.
   */
  @Test
  void gradeConstractorTest() {
    // Test 1
    Module module1 = new Module("CS2800", "Software engineering", true);
    Grade grade1 = new Grade(85, module1);
    assertEquals("CS2800", grade1.getModule().getCode());
    assertEquals(85, grade1.getScore());
  }

  /**
   * Tests default Module constructor and both module and score setters.
   */
  @Test
  void gradeSettersTest() {
    // Test 2
    // Modified for coursework2
    Module module2 = new Module("CS2850", "Operating Systems", true);
    Student student =
        new Student(918123001, "Bob", "Morris", "Morisson", "boringemail96@gmail.com");
    Grade grade2 = new Grade();
    grade2.setId(101);
    grade2.setScore(67);
    grade2.setStudent(student);
    grade2.setModule(module2);
    assertEquals("Operating Systems", grade2.getModule().getName());
    assertEquals(67, grade2.getScore());
    assertEquals(101, grade2.getId());
    assertEquals(918123001, grade2.getStudent().getId());
  }
}
