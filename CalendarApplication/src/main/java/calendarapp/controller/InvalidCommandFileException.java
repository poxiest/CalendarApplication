package calendarapp.controller;

public class InvalidCommandFileException extends RuntimeException {
  public InvalidCommandFileException(String message) {
    super(message);
  }
}