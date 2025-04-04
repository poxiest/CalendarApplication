package calendarapp.model;

/**
 * Exception thrown when a new/edit calendar event conflicts with existing events.
 */
public class EventConflictException extends RuntimeException {

  /**
   * Constructs a new event conflict exception with the specified detail message.
   *
   * @param message the detail message of the exception.
   */
  public EventConflictException(String message) {
    super(message);
  }
}