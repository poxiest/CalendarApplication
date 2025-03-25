package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Collectors;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendar;
import calendarapp.model.ICalendarModel;
import calendarapp.model.ICalendarRepository;
import calendarapp.model.dto.CalendarExporterDTO;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.model.dto.PrintEventsResponseDTO;
import calendarapp.utils.TimeUtil;

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
    calendarRepository.addCalendar("default", null, new EventRepository());
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
   * @param eventName The name of the event.
   * @param startTime The start time of the event.
   * @param endTime   The end time of the event.
   * @param property  The property to be edited.
   * @param value     The new value of the property.
   */
  @Override
  public void editEvent(String eventName, String startTime, String endTime, String property,
                        String value) {
    activeCalendar.getEventRepository().update(eventName, getTemporalFromString(startTime),
        getTemporalFromString(endTime), property, value);
  }

  /**
   * Returns a list of events that fall within the given date range, formatted for display.
   *
   * @param startDateTime The start date-time for the range.
   * @param endDateTime   The end date-time for the range.
   * @return A list of formatted event strings.
   */
  @Override
  public List<PrintEventsResponseDTO> getEventsForPrinting(String startDateTime,
                                                           String endDateTime) {
    Temporal startTemporal = getTemporalFromString(startDateTime);
    Temporal endTemporal;
    if (endDateTime == null) {
      endTemporal = TimeUtil.GetStartOfNextDay(startTemporal);
    } else {
      endTemporal = getTemporalFromString(endDateTime);
    }
    return activeCalendar.getEventRepository()
        .getOverlappingEvents(startTemporal, endTemporal)
        .stream().map(event -> PrintEventsResponseDTO.builder()
            .eventName(event.getName())
            .startTime(event.getStartTime())
            .endTime(event.getEndTime())
            .location(event.getLocation())
            .build()).collect(Collectors.toList());
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
    boolean isBusy = activeCalendar.getEventRepository()
        .isActiveAt(getTemporalFromString(dateTime));
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
  public void copyEvent(CopyEventRequestDTO copyEventRequestDTO) {
    calendarRepository.copyCalendarEvents(activeCalendar.getName(), copyEventRequestDTO);
  }
}
