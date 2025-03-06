package calendarapp.model.event;

public class EventConflictException extends RuntimeException {
  public EventConflictException(String message) {
    super(message);
  }
}
