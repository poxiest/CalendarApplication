package calendarapp.model;

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.model.dto.CalendarExporterDTO;

/**
 * Interface for managing events in a calendar.
 * Provides methods to create, update, retrieve, copy, and export events.
 */
public interface IEventRepository {
  /**
   * Creates a new event with the specified details.
   *
   * @param eventName         The name of the event.
   * @param startTime         The start time of the event.
   * @param endTime           The end time of the event.
   * @param description       A brief description of the event.
   * @param location          The location where the event takes place.
   * @param visibility        The visibility of the event (Public/Private).
   * @param recurringDays     The days on which the event recurs (if applicable).
   * @param occurrenceCount   The total number of occurrences (optional).
   * @param recurrenceEndDate The date when recurrence ends (optional).
   * @param autoDecline       Whether to automatically decline conflicting events.
   * @throws EventConflictException If the event conflicts with an existing one.
   */
  void create(String eventName, Temporal startTime, Temporal endTime,
              String description, String location, String visibility,
              String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
              boolean autoDecline) throws EventConflictException;

  /**
   * Updates a specific property of an existing event identified by its name and time range.
   *
   * @param eventName The name of the event.
   * @param startTime The start time of the event.
   * @param endTime   The end time of the event.
   * @param property  The property to update (e.g., description, location).
   * @param value     The new value to be set for the property.
   */
  void update(String eventName, Temporal startTime, Temporal endTime, String property,
              String value);

  /**
   * Retrieves a list of events based on the specified search criteria.
   *
   * @param eventName The name of the event (optional).
   * @param startTime The start time of the search range (optional).
   * @param endTime   The end time of the search range (optional).
   * @param type      The search type to apply (e.g., overlapping, exact match).
   * @return A list of deep copied events that match the given criteria.
   */
  List<IEvent> getEvents(String eventName, Temporal startTime,
                         Temporal endTime, SearchType type);

  /**
   * Copies a list of events to a new calendar while adjusting for time zones.
   *
   * @param eventsToCopy The list of events to copy.
   * @param toStartTime  The new start time for the copied events.
   * @param fromZoneId   The current time zone of the events.
   * @param toZoneId     The target time zone for the copied events.
   */
  void copyEvents(List<IEvent> eventsToCopy, Temporal toStartTime,
                  ZoneId fromZoneId, ZoneId toZoneId);

  /**
   * Adjusts the time zone of all events to a new time zone.
   *
   * @param from The current time zone.
   * @param to   The new target time zone.
   */
  void changeTimeZone(ZoneId from, ZoneId to);

  /**
   * Retrieves all events formatted as CalendarExporterDTO objects for export purposes.
   *
   * @return A list of export-ready event DTOs.
   */
  List<CalendarExporterDTO> getEventsForExport();
}
