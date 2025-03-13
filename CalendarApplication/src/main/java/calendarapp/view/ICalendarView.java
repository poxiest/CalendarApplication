package calendarapp.view;

import java.util.List;

/**
 * Interface for the calendar view component.
 * Defines methods for displaying messages and events to the user.
 */
public interface ICalendarView {

  /**
   * Displays a message to the user.
   *
   * @param message the message to be displayed
   */
  void displayMessage(String message);

  /**
   * Displays a list of events to the user.
   * The formatting of each event is determined by the event's formatForDisplay method.
   *
   * @param events the list of events to be displayed
   */
  void displayEvents(List<String> events);
}