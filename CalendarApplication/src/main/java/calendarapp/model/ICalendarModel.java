package calendarapp.model;

import java.util.List;

import calendarapp.model.dto.CalendarExporterDTO;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.model.dto.PrintEventsResponseDTO;

/**
 * Interface for the calendar model component.
 * This interface defines the core functionality for managing events in the calendar application.
 */
public interface ICalendarModel {

  /**
   * Creates a new event in the calendar.
   *
   * @param eventName         the name of the event.
   * @param startTime         the start time of the event.
   * @param endTime           the end time of the event (can be null for all-day events).
   * @param recurringDays     string representation of days on which the event repeats
   *                          (e.g., "MWF" for Monday, Wednesday, Friday).
   * @param occurrenceCount   number of occurrences for recurring events
   *                          (can be null if recurrenceEndDate is provided).
   * @param recurrenceEndDate the end date for recurring events
   *                          (can be null if occurrenceCount is provided).
   * @param description       the description of the event (optional).
   * @param location          the location of the event (optional).
   * @param visibility        the visibility setting of the event (optional).
   * @param autoDecline       whether to automatically decline conflicting events.
   * @throws EventConflictException if autoDecline is true and the event
   *                                conflicts with an existing event.
   */
  void createEvent(String eventName, String startTime, String endTime, String recurringDays,
                   String occurrenceCount, String recurrenceEndDate, String description,
                   String location, String visibility,
                   boolean autoDecline) throws EventConflictException;

  /**
   * Edits an existing event or events that match the specified criteria.
   *
   * @param eventName the name of the event(s) to edit.
   * @param startTime the start time to use for finding matching events (optional).
   * @param endTime   the end time to use for finding matching events (optional).
   * @param property  the property to modify (subject, description, location, etc.).
   * @param value     the new value for the specified property.
   * @throws EventConflictException if the edited event would conflict with existing
   *                                events and auto-decline is enabled.
   */
  void editEvent(String eventName, String startTime, String endTime, String property,
                 String value) throws EventConflictException;

  /**
   * Retrieves events that occur within the specified time range sorted in ascending order.
   *
   * @param startTime the start of the time range.
   * @param endTime   the end of the time range (if null, defaults to one day after startTime).
   * @return a list of events that intersect with the specified time range.
   */
  List<PrintEventsResponseDTO> getEventsForPrinting(String startTime, String endTime);

  List<CalendarExporterDTO> getEventsForExport();

  /**
   * Checks the availability status at a specific time.
   *
   * @param dateTime the date and time to check.
   * @return a status string ("Busy" or "Available").
   */
  String showStatus(String dateTime);

  /**
   * Creates a new calendar with the given name and time zone.
   *
   * @param calendarName the name of the calendar to create
   * @param timezone the time zone of the new calendar
   */
  void createCalendar(String calendarName, String timezone);

  /**
   * Edits a property of the specified calendar.
   *
   * @param calendarName the name of the calendar to edit
   * @param propertyName the name of the property to change
   * @param propertyValue the new value of the property
   */
  void editCalendar(String calendarName, String propertyName, String propertyValue);

  /**
   * Sets the active calendar to the one with the specified name.
   *
   * @param calendarName the name of the calendar to set as active
   */
  void setCalendar(String calendarName);

  /**
   * Copies event(s) to another calendar based on the provided copy request.
   *
   * @param copyEventRequestDTO the request containing details for copying the event
   */
  void copyEvent(CopyEventRequestDTO copyEventRequestDTO);
}