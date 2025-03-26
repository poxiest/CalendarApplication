package calendarapp.model;

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.model.dto.CalendarExporterDTO;
import calendarapp.model.impl.searchstrategies.SearchType;

/**
 * Interface for managing events in a calendar.
 * Provides methods to create, update, retrieve, copy, and export events.
 */
public interface IEventRepository {
  /**
   * Creates a new event with the specified details.
   *
   * @param eventName         the name of the event
   * @param startTime         the start time of the event
   * @param endTime           the end time of the event
   * @param description       the description of the event
   * @param location          the location of the event
   * @param visibility        the visibility of the event (Public/Private)
   * @param recurringDays     the days of recurrence (if any)
   * @param occurrenceCount   the number of occurrences (optional)
   * @param recurrenceEndDate the end date of recurrence (optional)
   * @param autoDecline       whether to decline creation on*
   */
  void create(String eventName, Temporal startTime, Temporal endTime,
              String description, String location, String visibility,
              String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
              boolean autoDecline) throws EventConflictException;

  /**
   * Updates a specific property of an event identified by its name and time range.
   *
   * @param eventName the name of the event
   * @param startTime the start time of the event
   * @param endTime   the end time of the event
   * @param property  the property to update (e.g., description, location)
   * @param value     the new value to set for the property
   */
  void update(String eventName, Temporal startTime, Temporal endTime, String property,
              String value);

  /**
   * Returns all events with the given name between the specified start and end time.
   *
   * @param eventName the name of the event
   * @param startTime the start time of the range
   * @param endTime   the end time of the range
   * @return list of matching events
   */
  List<IEvent> getEvents(String eventName, Temporal startTime,
                         Temporal endTime, SearchType type);

  /**
   * Copies the given events to a new calendar based on the copy request.
   *
   * @param eventsToCopy the events to copy
   * @param toStartTime  time to copy to.
   * @param fromZoneId   the original time zone
   * @param toZoneId     the target time zone
   */
  void copyEvents(List<IEvent> eventsToCopy, Temporal toStartTime,
                  ZoneId fromZoneId, ZoneId toZoneId);

  /**
   * Updates all event times to a new time zone.
   *
   * @param from the current time zone
   * @param to   the new time zone
   */
  void changeTimeZone(ZoneId from, ZoneId to);

  List<CalendarExporterDTO> getEventsForExport();
}
