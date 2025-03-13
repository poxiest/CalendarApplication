package calendarapp.controller;

/**
 * Interface defining the controller for the calendar application.
 */
public interface ICalendarController {
  /**
   * Starts the controller and begins processing user commands.
   * This method typically runs continuously until an exit command is received.
   */
  void start() throws InvalidCommandException;
}