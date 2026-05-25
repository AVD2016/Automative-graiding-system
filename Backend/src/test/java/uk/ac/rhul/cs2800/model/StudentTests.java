package uk.ac.rhul.cs2800.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs2800.exception.NoGradeAvailableException;
import uk.ac.rhul.cs2800.exception.NoRegistrationException;
import uk.ac.rhul.cs2800.model.Module;
import uk.ac.rhul.cs2800.model.Student;

/**
 * Student test class to test the constractor, setters and getters.
 */
public class StudentTests {
    
  /**
   * Initialise an instance of class Student and run all getters
   */
  @Test 
  void studentConstractorTest() {
    Student student = new Student(132, "Vlad", "Abram", "AVD", "znac588@live.rhul.ac.uk");
    assertEquals(132, student.getId());
    assertEquals("Vlad", student.getFirstName());
    assertEquals("Abram", student.getLastName());
    assertEquals("AVD", student.getUsername());
    assertEquals("znac588@live.rhul.ac.uk", student.getEmail());
  }

  /**
   * Initialise an instance of class Student using a constractor with no arguments, add all instance
   * variables by using setters, and run all getters.
   */
  @Test
  void studentSettersTest() {
    Student student = new Student();
    student.setId(90190);
    student.setFirstName("My firstname");
    student.setLastName("My surname");
    student.setUsername("1st gay");
    student.setEmail("MyEmail5@mail.com");

    assertEquals(90190, student.getId());
    assertEquals("My firstname", student.getFirstName());
    assertEquals("My surname", student.getLastName());
    assertEquals("1st gay", student.getUsername());
    assertEquals("MyEmail5@mail.com", student.getEmail());
  }

  /**
   * Initialises instance of Student class, 2 instances of module classes. Maps instances of Module
   * class to grades received for that module with addGrade(int score, Module module). Tests mapping
   * by checking expected return values to the ones returned by Score.getScore(). Testing
   * computeAverage()
   * 
   * @throws NoRegistrationException may be thrown, but is not an expected output.
   * @throws NoGradeAvailableException may be thrown, but not an expected output.
   */
  @Test
  void studentGradesTest() throws NoRegistrationException, NoGradeAvailableException {
    Student student =
        new Student(918123001, "Bob", "Morris", "Morisson", "boringemail96@gmail.com");

    Module module1 = new Module("CS1800", "Internet Services", true);
    Module module2 = new Module("CS1550", "Object Oriented Programming", true);
    Module module3 = new Module("CS1900", "Object Oriented Programming 2", true);

    student.registerModule(module1);
    student.registerModule(module2);
    student.registerModule(module3);

    student.addGrade(58, module1);
    student.addGrade(76, module2);
    student.addGrade(85, module3);
    
    assertEquals(58, student.getGrade(module1).getScore());
    assertEquals(76, student.getGrade(module2).getScore());
    assertEquals(85, student.getGrade(module3).getScore());

    assertEquals(73, student.computeAverage());
  }

  /**
   * Tests if student registration on a module can be done without an exception thrown.
   */
  @Test
  void studentRegistration() {
    Student student =
        new Student(100643, "Mikelle", "Morris", "Professor Morris", "zmtl109@live.rhul.ac.uk");
    Module module = new Module("F1881", "Computer science", true);
    student.registerModule(module);
  }

  /**
   * Tests that attempting to retrieve a grade for a module in which the student has no grade
   * recorded throws a {@link NoGradeAvailableException}.
   */
  @Test
  void studentNoGradeAvailableTest() {
    Student student = new Student();
    Module module = new Module("CS101", "Intro to Computer Science", true);

    assertThrows(NoGradeAvailableException.class,
          () -> student.getGrade(module));
  }

  /**
   * Tests that attempting to add a grade for a module in which the student is not registered throws
   * a {@link NoRegistrationException}.
   */
  @Test
  void studentNoRegistrationTest() {
    Student student = new Student();
    Module module = new Module("CS1800", "Internet Services", true);

    assertThrows(NoRegistrationException.class, () -> student.addGrade(58, module));
  }
}
