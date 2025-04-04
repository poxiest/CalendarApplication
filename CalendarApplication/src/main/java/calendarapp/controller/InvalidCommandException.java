package calendarapp.controller;

/**
 * Exception thrown when an invalid command is encountered in the calendar application.
 * This is an unchecked exception that extends RuntimeException.
 * This exception should be thrown when the command format is incorrect,
 * the command is unrecognized, or when command parameters are invalid.
 */
public class InvalidCommandException extends RuntimeException {

  /**
   * Constructs a new InvalidCommandException with the specified detail message.
   *
   * @param message the detail message of the exception.
   */
  public InvalidCommandException(String message) {
    super(message);
  }
}