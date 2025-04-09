package calendarapp.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import calendarapp.controller.Features;
import calendarapp.model.dto.CalendarResponseDTO;
import calendarapp.model.dto.EventsResponseDTO;

/**
 * Interface for the graphical user interface view in the calendar application.
 * Defines methods for user interaction, displaying data, and navigating the UI.
 */
public interface GUIView {

  /**
   * Connects the controller features to the view for handling user actions.
   */
  void addFeatures(Features features);

  /**
   * Updates the calendar and details panel with the given list of events.
   */
  void updateEvents(List<EventsResponseDTO> events);

  void updateCalendarList(List<CalendarResponseDTO> calendarNames);

  /**
   * Sets the specified calendar as the active calendar in the view.
   */
  void setActiveCalendar(String calendarName);

  /**
   * Displays a confirmation message to the user.
   */
  void showConfirmation(String message);

  /**
   * Displays an error message to the user.
   */
  void showError(String errorMessage);

  /**
   * Opens a form to create a new event and returns the input.
   */
  Map<String, String> showCreateEventForm();

  /**
   * Opens a form to edit the given event and returns the updated input.
   */
  Map<String, String> showEditEventForm(EventsResponseDTO event);

  /**
   * Opens a form to create a new calendar and returns the input.
   */
  Map<String, String> showCreateCalendarForm();

  /**
   * Returns the current date displayed in the view.
   */
  LocalDate getCurrentDate();

  /**
   * Updates the view to show the previous date or time period.
   */
  void navigateToPrevious(LocalDate date);

  /**
   * Updates the view to show the next date or time period.
   */
  void navigateToNext(LocalDate date);

  /**
   * Opens a form to search for events and returns the input criteria.
   */
  Map<String, String> findEvents();

  /**
   * Opens a form to export a calendar and returns the export settings.
   */
  Map<String, String> showExportCalendarForm();

  /**
   * Opens a dialog to import a calendar file and returns the selected file path.
   */
  Map<String, String> showImportCalendarDialog();
}
