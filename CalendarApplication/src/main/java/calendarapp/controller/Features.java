package calendarapp.controller;

import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.view.GUIView;

public interface Features {

  void setView(GUIView view);

  /**
   * Creates a new event with the provided parameters.
   *
   * @param eventName       Name of the event
   * @param startTime       Start time in format mm-dd-yyyy HH:MM
   * @param endTime         End time in format mm-dd-yyyy HH:MM
   * @param recurringDays   Days when event recurs (e.g., "MWF")
   * @param occurrenceCount Number of occurrences for recurring events
   * @param recurrenceEndDate End date for recurring events
   * @param description     Event description
   * @param location        Event location
   * @param visibility      Event visibility setting
   * @param autoDecline     Whether to auto-decline conflicting events
   */
  void createEvent(String eventName, String startTime, String endTime, String recurringDays,
                   String occurrenceCount, String recurrenceEndDate, String description,
                   String location, String visibility, boolean autoDecline);

  /**
   * Edits an existing event.
   *
   * @param eventName Name of the event to edit
   * @param startTime Start time to identify the event
   * @param endTime   End time to identify the event
   * @param property  Property to change
   * @param value     New value for the property
   */
  void editEvent(String eventName, String startTime, String endTime, String property, String value);

  /**
   * Creates a new calendar.
   *
   * @param calendarName Name of the calendar
   * @param timezone     Timezone for the calendar
   */
  void createCalendar(String calendarName, String timezone);

  /**
   * Edits a calendar's property.
   *
   * @param calendarName  Name of the calendar to edit
   * @param propertyName  Property to change
   * @param propertyValue New value for the property
   */
  void editCalendar(String calendarName, String propertyName, String propertyValue);

  /**
   * Sets the active calendar.
   *
   * @param calendarName Name of the calendar to set active
   */
  void setActiveCalendar(String calendarName);

  /**
   * Copies an event to another calendar.
   *
   * @param copyRequest Request containing copy details
   */
  void copyEvent(CopyEventRequestDTO copyRequest);

  /**
   * Loads events for a specified time period.
   *
   * @param startDate Start date in format mm-dd-yyyy
   * @param endDate   End date in format mm-dd-yyyy
   * @param viewType  Type of view (day, week, month)
   */
  void loadEvents(String startDate, String endDate, String viewType);

  /**
   * Checks availability status at a specified time.
   *
   * @param dateTime Date and time to check in format mm-dd-yyyy HH:MM
   */
  void checkStatus(String dateTime);
}
