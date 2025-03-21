package calendarapp.model;

import java.time.temporal.Temporal;
import java.util.List;

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
  void createEvent(String eventName, Temporal startTime, Temporal endTime, String recurringDays,
                   String occurrenceCount, Temporal recurrenceEndDate, String description,
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
  void editEvent(String eventName, Temporal startTime, Temporal endTime, String property,
                 String value, boolean isRecurringEvent) throws EventConflictException;

  /**
   * Retrieves events that occur within the specified time range sorted in ascending order.
   *
   * @param startTime the start of the time range.
   * @param endTime   the end of the time range (if null, defaults to one day after startTime).
   * @return a list of events that intersect with the specified time range.
   */
  List<String> getEventsForPrinting(Temporal startTime, Temporal endTime);

  /**
   * Exports calendar events to a CSV file compatible with Google Calendar.
   *
   * @param filename the base name of the file to export to.
   * @return the full path to the exported file.
   */
  String export(String filename);

  /**
   * Checks the availability status at a specific time.
   *
   * @param dateTime the date and time to check.
   * @return a status string ("Busy" or "Available").
   */
  String showStatus(Temporal dateTime);

  void createCalendar(String calendarName, String timezone);

  void editCalendar(String calendarName, String propertyName, String propertyValue);

  void setCalendar(String calendarName);

  void copyEvent(String eventName, Temporal startTime, String copyCalendarName, Temporal toDate);

  void copyEvents(Temporal startTime, Temporal endTime, String copyCalendarName, Temporal toDate);
}