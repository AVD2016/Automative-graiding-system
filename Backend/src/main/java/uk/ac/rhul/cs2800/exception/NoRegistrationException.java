package uk.ac.rhul.cs2800.exception;

/**
 * Exception thrown when a student attempts to access a module for which they are not registered.
 */
public class NoRegistrationException extends Exception {

  /**
   * Constructs a new NoRegistrationException with the specified detail message.
   *
   * @param s the detail message explaining the reason for the exception
   */
  public NoRegistrationException(String s) {
    super(s);
  }
}
