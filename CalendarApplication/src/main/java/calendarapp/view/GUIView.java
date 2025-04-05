package calendarapp.view;

import java.util.List;

import calendarapp.controller.Features;
import calendarapp.model.dto.PrintEventsResponseDTO;

public interface GUIView {

  void addFeatures(Features features);

  /**
   * Displays the main calendar interface.
   */
  void display();

  /**
   * Updates the displayed events in the calendar.
   *
   * @param events List of events to display
   */
  void updateEvents(List<PrintEventsResponseDTO> events);

  /**
   * Updates the list of available calendars.
   *
   * @param calendarNames List of calendar names
   */
  void updateCalendarList(List<String> calendarNames);

  /**
   * Sets the currently active calendar.
   *
   * @param calendarName Name of the active calendar
   */
  void setActiveCalendar(String calendarName);

  /**
   * Displays a confirmation message to the user.
   *
   * @param message The message to display
   */
  void showConfirmation(String message);

  /**
   * Displays an error message to the user.
   *
   * @param errorMessage The error message to display
   */
  void showError(String errorMessage);

  /**
   * Displays the availability status at a specific time.
   *
   * @param dateTime The date and time checked
   * @param status   The availability status ("Busy" or "Available")
   */
  void showStatus(String dateTime, String status);

  /**
   * Shows an event creation form.
   */
  void showCreateEventForm();

  /**
   * Shows an event editing form pre-populated with the event details.
   *
   * @param event The event to edit
   */
  void showEditEventForm(PrintEventsResponseDTO event);

  /**
   * Shows a calendar creation form.
   */
  void showCreateCalendarForm();

  /**
   * Changes the calendar view type (day, week, month).
   *
   * @param viewType The view type to display
   */
  void changeViewType(String viewType);
}
