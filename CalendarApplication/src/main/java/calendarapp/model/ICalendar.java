package calendarapp.model;

import java.time.ZoneId;

/**
 * Interface representing a calendar.
 * Provides access to the calendar's name, time zone, and event repository.
 */
public interface ICalendar {

  /**
   * Returns the name of the calendar.
   *
   * @return the calendar name
   */
  String getName();

  /**
   * Returns the time zone of the calendar.
   *
   * @return the calendar's time zone
   */
  ZoneId getZoneId();

  /**
   * Returns the event repository of the calendar.
   *
   * @return the event repository
   */
  IEventRepository getEventRepository();
}
