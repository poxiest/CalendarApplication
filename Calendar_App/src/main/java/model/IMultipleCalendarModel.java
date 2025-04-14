package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import calendar.ConflictException;

/**
 * An interface outlining operations that can
 * be performed on a collection of calendars.
 */
public interface IMultipleCalendarModel {

  /**
   * Returns the set of valid calendar keys.
   * @return the calendar keys.
   */
  public Set<String> getCalendarKeys();

  /**
   * Creates a calendar with the given name
   * that is set to the given timezone.
   * @param name the name of the calendar.
   * @param timezone the timezone to set this calendar to.
   */
  public void createCalendar(
      String name,
      ZoneId timezone
  );

  /**
   * Updates the property (name | timezone) of an
   * existing calendar with the value.
   * @param name the name of the calendar.
   * @param property the property to update.
   * @param value the value to update the property with.
   */
  public void editCalendar(
      String name,
      CalendarProperty property,
      String value
  );

  /**
   * Sets the active calendar to the calendar
   * with the given name.
   * @param name the name of the calendar.
   */
  public void useCalendar(
      String name
  );

  /**
   * Copies an event in the active calendar
   * to the specified calendar at given dateTime.
   * @param eventName the event name to copy.
   * @param eventDateTime the event dateTime.
   * @param targetCalendarName the target calendar name.
   * @param targetDateTime the date to copy the event to.
   * @throws ConflictException if a conflict occurs while copying.
   */
  public void copyEventWithName(
      String eventName,
      LocalDateTime eventDateTime,
      String targetCalendarName,
      LocalDateTime targetDateTime
  ) throws ConflictException;

  /**
   * Copies all events scheduled on the given date
   * to the target date in the target calendar.
   * @param date the date to copy events from.
   * @param targetCalendarName the name of the calendar to copy events to.
   * @param targetDate the target date in the target calendar.
   * @throws ConflictException if a conflict occurs while copying.
   */
  public void copyEventsOnDate(
      LocalDate date,
      String targetCalendarName,
      LocalDate targetDate
  ) throws ConflictException;

  /**
   * Copy all events specified in the specified
   * interval to begin at the start date of the
   * target calendar.
   * @param startDate the start date to query events from.
   * @param endDate the end date to stop querying events.
   * @param targetCalendarName the target calendar name.
   * @param targetCalendarStartDate the start date in the target calendar.
   * @throws ConflictException if a conflict occurs while copying.
   */
  public void copyEventsBetween(
      LocalDate startDate,
      LocalDate endDate,
      String targetCalendarName,
      LocalDate targetCalendarStartDate
  ) throws ConflictException;

  /**
   * Gets the active calendar in this multiple
   * calendar model.
   * @return the currently active calendar.
   */
  public IExtendedZonedCalendarModel getActiveCalendar();
  
}
