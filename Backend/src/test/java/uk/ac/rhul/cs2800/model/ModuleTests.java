package uk.ac.rhul.cs2800.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs2800.model.Module;

/**
 * Test class to test Module class functionality.
 */
public class ModuleTests {

  /**
   * Tests constructor and getters.
   */
  @Test
  void moduleConstractorTest() {
    Module module1 = new Module("F1881", "Computer science", true);
    assertEquals("F1881", module1.getCode());
    assertEquals("Computer science", module1.getName());
    assertEquals(true, module1.getMnc());
  }

  /**
   * Tests default constructor, setters and getters.
   */
  @Test
  void moduleSettersTest() {
    Module module1 = new Module();
    module1.setCode("KK4888");
    module1.setName("122CornerTest!");
    module1.setMnc(false);
    assertEquals("KK4888", module1.getCode());
    assertEquals("122CornerTest!", module1.getName());
    assertEquals(false, module1.getMnc());
  }
}
