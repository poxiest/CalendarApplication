package calendarapp.controller;

import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.view.GUIView;

/**
 * Interface defining the set of user actions handled by the controller in the calendar application.
 * Includes calendar and event operations such as creation, editing, import/export, and navigation.
 */
public interface Features {
  /**
   * Connects the view to this controller and initializes it with model data.
   */
  void setView(GUIView view);

  /**
   * Handles the creation of a new event by collecting input and updating the model.
   */
  void createEvent();

  /**
   * Handles editing an existing event using the provided event data.
   */
  void editEvent(EventsResponseDTO eventDTO);

  /**
   * Handles the creation of a new calendar by collecting input and updating the model.
   */
  void createCalendar();

  /**
   * Sets the specified calendar as the active one in the model and updates the view.
   */
  void setActiveCalendar(String calendarName);

  /**
   * Loads events from the model based on the given date range or specific date.
   */
  void loadEvents(String startDate, String endDate, String on);

  /**
   * Navigates to the previous month and refreshes the events in the view.
   */
  void navigateToPrevious();

  /**
   * Navigates to the next month and refreshes the events in the view.
   */
  void navigateToNext();

  /**
   * Finds events based on user-provided criteria and updates the view with results.
   */
  void findEvents();

  /**
   * Handles exporting the current calendar to a file using a selected format.
   */
  void exportCalendar();

  /**
   * Handles importing events from a selected calendar file into the application.
   */
  void importCalendar();
}
