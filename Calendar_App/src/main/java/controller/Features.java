package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import view.IViewEvent;
import view.IViewRecurringEvent;

/**
 * Interface which defines the abilities of the controller.
 * This includes methods for interacting with the model through
 * the controller.
 */
public interface Features {

  /**
   * Creates a new calendar with the given name and zone id.
   * @param calendarName the name of the calendar.
   * @param zoneIdString the zone id string for this calendar.
   */
  public void createCalendar(String calendarName, String zoneIdString) throws IOException;

  /**
   * Uses the calendar with the given calendar name.
   * @param calendarName the calendar name to use.
   */
  public void useCalendar(String calendarName);

  /**
   * Queries the active calendar for events on the given date,
   * then sets them in the view.
   * @param date the date to query for events.
   */
  public void queryEventsOnDate(LocalDate date) throws IOException;

  /**
   * Edits a single event using the provided
   * view event. The view event includes all properties
   * relevant to single events. All values in the view
   * event are inputs from fields in the view and are
   * parsed in the controller before passing to the model.
   * @param id the id of the event to edit.
   * @param editEvent the editEvent including the properties.
   * @throws IOException if there is a conflict or a parsing error.
   */
  public void editSingle(String id, IViewEvent editEvent) throws IOException;

  /**
   * Edits a recurring event using the provided
   * view event. The view event includes all properties
   * relevant to either a recurring until or a
   * recurring sequence event and will set the correct
   * type of event dynamically. All values in the view
   * event are inputs from fields in the view and are
   * parsed in the controller before passing to the model.
   * @param id the id of the event to edit.
   * @param viewEvent the viewEvent including the properties.
   * @param editType the type of edit, which can either be single,
   *          this and following, or all events in the series.
   * @throws IOException if there is a conflict or a parsing error.
   */
  public void editRecurring(
      String id, 
      IViewRecurringEvent viewEvent, 
      EditType editType
  ) throws IOException;

  /**
   * Creates a single event using the provided
   * view event. The view event includes all properties
   * relevant to single events. All values in the view
   * event are inputs from fields in the view and are
   * parsed in the controller before passing to the model.
   * @param viewEvent the viewEvent including the properties.
   * @throws IOException if there is a conflict or a parsing error.
   */
  public void createSingle(
      IViewEvent viewEvent
  ) throws IOException;

  /**
   * Creates a recurring event using the provided
   * view event. The view event includes all properties
   * relevant to either a recurring until or a
   * recurring sequence event and will set the correct
   * type of event dynamically. All values in the view
   * event are inputs from fields in the view and are
   * parsed in the controller before passing to the model.
   * @param viewEvent the viewEvent including the properties.
   * @throws IOException if there is a conflict or a parsing error.
   */
  public void createRecurring(
      IViewRecurringEvent viewEvent
  ) throws IOException;

  /**
   * Exports the contents of the current calendar to a file.
   * @throws IOException if there is an error reading or writing to the file.
   */
  public void export() throws IOException;

  /**
   * Imports the events from the import file into the active calendar.
   * @throws FileNotFoundException if the file is not found.
   * @throws IOException if there is a conflict while creating events.
   */
  public void importFile(File importFile) throws FileNotFoundException, IOException;

  /**
   * Computes and updates analytics data for the active calendar between the given date range.
   *
   * @param startDate the start date of the analysis range
   * @param endDate the end date of the analysis range
   * @throws IOException if data processing fails
   */
  public void queryAnalytics(LocalDate startDate, LocalDate endDate) throws IOException;
}
