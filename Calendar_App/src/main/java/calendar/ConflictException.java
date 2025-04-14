package calendar;

import java.io.IOException;

/**
 * This class represents a conflict exception. This is used by
 * Calendar implementations to signal when events conflict with
 * one another.
 */
public class ConflictException extends IOException {

  /**
   * This is a constructor for a conflict exception.
   * @param message the message associated with this exception.
   */
  public ConflictException(String message) {
    super(message);
  }
}
