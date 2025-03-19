package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.IEvent;
import calendarapp.utils.TimeUtil;

import static calendarapp.model.impl.CalendarExporter.exportEventAsGoogleCalendarCsv;

/**
 * The CalendarModel class implements the ICalendarModel interface
 * and is responsible for managing calendar events, including creating, editing,
 * and displaying events, handling conflicts, and exporting events in a specific format.
 */
public class CalendarModel implements ICalendarModel {

  private final List<IEvent> events;

  /**
   * Constructs a CalendarModel object, initializing the event list and day mapping.
   */
  public CalendarModel() {
    this.events = new ArrayList<>();
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

    // TODO: Call Event Repository to create Event
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

    // TODO: Call Event Repository to edit events
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

    // TODO: Call findEvents function to get all the events, and write format logic
    return null;
  }

  /**
   * Exports the events to a CSV file.
   *
   * @param filename The name of the file to export.
   * @return The file path of the exported CSV.
   */
  @Override
  public String export(String filename) {
    return exportEventAsGoogleCalendarCsv(events, filename);
  }

  /**
   * Checks the availability status of the user for a given date-time.
   *
   * @param dateTime The date-time to check.
   * @return The availability status ("busy" or "available").
   */
  @Override
  public String showStatus(Temporal dateTime) {
    boolean isBusy = events.stream().anyMatch(event ->
        TimeUtil.isActiveAt(dateTime, event.getStartTime(), event.getEndTime()));
    return isBusy ? EventConstants.Status.BUSY : EventConstants.Status.AVAILABLE;
  }
}
