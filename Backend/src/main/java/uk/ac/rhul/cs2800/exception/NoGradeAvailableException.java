package uk.ac.rhul.cs2800.exception;

/**
 * Exception thrown when a grade for a specific module is not available for a student.
 *
 * @author Vladyslav Abramov
 */
public class NoGradeAvailableException extends Exception {

  /**
   * Constructs a new {@code NoGradeAvailableException} with the specified detail message.
   *
   * @param s the detail message explaining why the grade is unavailable.
   */
  public NoGradeAvailableException(String s) {
    super(s);
  }
}
