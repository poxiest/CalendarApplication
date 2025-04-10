package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Collectors;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.Constants;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendar;
import calendarapp.model.ICalendarModel;
import calendarapp.model.ICalendarRepository;
import calendarapp.model.SearchType;
import calendarapp.model.dto.CalendarExporterDTO;
import calendarapp.model.dto.CalendarResponseDTO;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.model.dto.EditEventRequestDTO;
import calendarapp.model.dto.EventsResponseDTO;

import static calendarapp.model.Constants.Calendar.DEFAULT_CALENDAR_NAME;
import static calendarapp.utils.TimeUtil.getEndOfDayFromString;
import static calendarapp.utils.TimeUtil.getTemporalFromString;

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
    calendarRepository.addCalendar(DEFAULT_CALENDAR_NAME, null, new EventRepository());
    activeCalendar = calendarRepository.getCalendar(DEFAULT_CALENDAR_NAME);
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
  public void createEvent(String eventName, String startTime, String endTime,
                          String recurringDays, String occurrenceCount, String recurrenceEndDate,
                          String description, String location, String visibility,
                          boolean autoDecline) throws EventConflictException {
    activeCalendar.getEventRepository().create(eventName, getTemporalFromString(startTime),
        getTemporalFromString(endTime), description, location, visibility, recurringDays,
        occurrenceCount, getEndOfDayFromString(recurrenceEndDate), true);
  }

  /**
   * Edits an event based on the specified property and value.
   *
   * @param editEventRequestDTO dto object containing edit event request.
   */
  @Override
  public void editEvent(EditEventRequestDTO editEventRequestDTO) {
    activeCalendar.getEventRepository().update(editEventRequestDTO.getEventName(),
        getTemporalFromString(editEventRequestDTO.getStartTime()),
        getTemporalFromString(editEventRequestDTO.getEndTime()),
        editEventRequestDTO.getPropertyName(), editEventRequestDTO.getPropertyValue(),
        editEventRequestDTO.isMultiple());
  }

  /**
   * Retrieves and formats a list of events that fall within the specified date range for display.
   *
   * @param startDateTime The start date-time of the range (ignored if {@code on} is provided).
   * @param endDateTime   The end date-time of the range (ignored if {@code on} is provided).
   * @param on            (Optional) A specific date to retrieve all events occurring on that day.
   * @return A list of {@link EventsResponseDTO} objects containing event details.
   */
  @Override
  public List<EventsResponseDTO> getEvents(String eventName, String startDateTime,
                                           String endDateTime, String on) {
    Temporal startTemporal = getTemporalFromString(startDateTime);
    Temporal endTemporal = getTemporalFromString(endDateTime);

    // If 'on' is provided, override start and end time to cover the full day
    if (on != null) {
      startTemporal = getTemporalFromString(on);
      endTemporal = getEndOfDayFromString(on);
    }

    return activeCalendar.getEventRepository()
        .getEvents(eventName, startTemporal, endTemporal, SearchType.OVERLAPPING)
        .stream()
        .map(event -> EventsResponseDTO.builder()
            .eventName(event.getName())
            .startTime(event.getStartTime())
            .endTime(event.getEndTime())
            .location(event.getLocation())
            .description(event.getDescription())
            .recurringEndDate(event.getRecurrenceEndDate())
            .occurrenceCount(event.getOccurrenceCount())
            .recurringDays(event.getRecurringDays())
            .visibility(event.getVisibility().getValue())
            .build())
        .collect(Collectors.toList());
  }

  @Override
  public List<CalendarExporterDTO> getEventsForExport() {
    return activeCalendar.getEventRepository().getEventsForExport();
  }

  /**
   * Checks the availability status of the user for a given date-time.
   *
   * @param dateTime The date-time to check.
   * @return The availability status ("busy" or "available").
   */
  @Override
  public String showStatus(String dateTime) {
    boolean isAvailable = activeCalendar.getEventRepository()
        .getEvents(null, getTemporalFromString(dateTime),
            getTemporalFromString(dateTime), SearchType.OVERLAPPING).isEmpty();
    return isAvailable ? Constants.Status.AVAILABLE : Constants.Status.BUSY;
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
  public void copyEvent(CopyEventRequestDTO copyEventRequestDTO) {
    calendarRepository.copyCalendarEvents(activeCalendar.getName(), copyEventRequestDTO);
  }

  @Override
  public List<CalendarResponseDTO> getCalendars() {
    return calendarRepository.getCalendars()
        .stream()
        .map(e -> CalendarResponseDTO.builder()
            .name(e.getName())
            .zoneId(e.getZoneId())
            .build())
        .collect(Collectors.toList());
  }
}
