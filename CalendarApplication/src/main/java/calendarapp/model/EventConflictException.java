package calendarapp.model;

public class EventConflictException extends RuntimeException {
  public EventConflictException(String message) {
    super(message);
  }
}
