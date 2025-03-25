package calendarapp.model;

import calendarapp.model.dto.CopyEventRequestDTO;

/**
 * Interface for managing calendars in the application.
 * Provides methods to add, edit, retrieve, and copy calendar events.
 */
public interface ICalendarRepository {

  /**
   * Adds a new calendar with the given name, time zone, and event repository.
   *
   * @param name the name of the calendar
   * @param zoneId the time zone of the calendar
   * @param eventRepository the event repository for the calendar
   */
  void addCalendar(String name, String zoneId, IEventRepository eventRepository);

  /**
   * Edits a property of the specified calendar.
   *
   * @param name the name of the calendar to edit
   * @param propertyName the property to update
   * @param propertyValue the new value for the property
   */
  void editCalendar(String name, String propertyName, String propertyValue);

  /**
   * Copies events from the specified calendar based on the copy request.
   *
   * @param currentCalendarName the name of the source calendar
   * @param copyEventRequestDTO the request containing copy details
   */
  void copyCalendarEvents(String currentCalendarName, CopyEventRequestDTO copyEventRequestDTO);

  /**
   * Returns the calendar with the specified name.
   *
   * @param name the name of the calendar
   * @return the calendar with the given name
   */
  ICalendar getCalendar(String name);
}
