package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendar;
import calendarapp.model.ICalendarModel;
import calendarapp.model.ICalendarRepository;
import calendarapp.utils.TimeUtil;

/**
 * The CalendarModel class implements the ICalendarModel interface
 * and is responsible for managing calendar events, including creating, editing,
 * and displaying events, handling conflicts, and exporting events in a specific format.
 */
public class CalendarModel implements ICalendarModel {

  private final ICalendarRepository calendarRepository;
  private ICalendar activeCalendar;

  /**
   * Constructs a CalendarModel object, initializing the event list and day mapping.
   */
  public CalendarModel() {
    calendarRepository = new CalendarRepository();
    calendarRepository.addCalendar("default", new EventRepository());
    activeCalendar = calendarRepository.getCalendar("default");
  }

  /**
   * Creates a new event, either recurring or one-time.
   *
   * @param eventName         The name of the event.
   * @param startTime         The start time of the event.
   * @param endTime           The end time of the event.
   * @param recurringDays     A string of characters representing recurring days.
   * @param occurrenceCount   The number of occurrences of the event (for recurring events).
   * @param recurrenceEndDate The end date of the recurrence (for recurring events).
   * @param description       The description of the event.
   * @param location          The location of the event.
   * @param visibility        The visibility of the event.
   * @param autoDecline       Flag indicating whether the event should auto-decline
   *                          conflicting events.
   * @throws EventConflictException if the event conflicts with an existing event.
   */
  @Override
  public void createEvent(String eventName, Temporal startTime, Temporal endTime,
                          String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
                          String description, String location, String visibility,
                          boolean autoDecline) throws EventConflictException {

    activeCalendar.getEventRepository().create(eventName, startTime, endTime, description, location,
        visibility, recurringDays, occurrenceCount, recurrenceEndDate, true);
  }

  /**
   * Edits an event based on the specified property and value.
   *
   * @param eventName The name of the event.
   * @param startTime The start time of the event.
   * @param endTime   The end time of the event.
   * @param property  The property to be edited.
   * @param value     The new value of the property.
   */
  @Override
  public void editEvent(String eventName, Temporal startTime, Temporal endTime, String property,
                        String value, boolean isRecurringEvents) {

    activeCalendar.getEventRepository().update(eventName, startTime, endTime, property, value,
        isRecurringEvents);
  }

  /**
   * Returns a list of events that fall within the given date range, formatted for display.
   *
   * @param startDateTime The start date-time for the range.
   * @param endDateTime   The end date-time for the range.
   * @return A list of formatted event strings.
   */
  @Override
  public List<String> getEventsForPrinting(Temporal startDateTime, Temporal endDateTime) {
    if (endDateTime == null) {
      endDateTime = TimeUtil.getLocalDateTimeFromTemporal(startDateTime).toLocalDate()
          .plusDays(1).atStartOfDay();
    }

    return activeCalendar.getEventRepository().getFormattedEvents(startDateTime, endDateTime);
  }

  /**
   * Exports the events to a CSV file.
   *
   * @param filename The name of the file to export.
   * @return The file path of the exported CSV.
   */
  @Override
  public String export(String filename) {
    // TODO: Fix this
    return "";
  }

  /**
   * Checks the availability status of the user for a given date-time.
   *
   * @param dateTime The date-time to check.
   * @return The availability status ("busy" or "available").
   */
  @Override
  public String showStatus(Temporal dateTime) {
    boolean isBusy = activeCalendar.getEventRepository().isActiveAt(dateTime);
    return isBusy ? Constants.Status.BUSY : Constants.Status.AVAILABLE;
  }

  @Override
  public void createCalendar(String calendarName, String timezone) {
    calendarRepository.addCalendar(calendarName, timezone, new EventRepository());
  }

  @Override
  public void editCalendar(String calendarName, String propertyName, String propertyValue) {
    calendarRepository.editCalendar(calendarName, propertyName, propertyValue);
  }

  @Override
  public void setCalendar(String calendarName) {
    ICalendar temp = calendarRepository.getCalendar(calendarName);
    if (temp == null) {
      throw new InvalidCommandException("Calendar does not exist.\n");
    }
    activeCalendar = temp;
  }

  @Override
  public void copyEvent(String eventName, Temporal startTime, String copyCalendarName, Temporal toDate) {

  }

  @Override
  public void copyEvents(Temporal startTime, Temporal endTime, String copyCalendarName, Temporal toDate) {

  }
}
