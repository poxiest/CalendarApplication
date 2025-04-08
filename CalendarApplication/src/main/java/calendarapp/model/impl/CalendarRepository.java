package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.CalendarUpdateOperation;
import calendarapp.model.ICalendar;
import calendarapp.model.ICalendarRepository;
import calendarapp.model.IEventRepository;
import calendarapp.model.SearchType;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.utils.TimeUtil;

/**
 * Implementation of ICalendarRepository that manages a list of calendars.
 * Supports adding, editing, retrieving, and copying events between calendars.
 */
public class CalendarRepository implements ICalendarRepository {
  private List<ICalendar> calendars;

  /**
   * Constructs a CalendarRepository with an empty list of calendars.
   */
  CalendarRepository() {
    this.calendars = new ArrayList<>();
  }

  @Override
  public void addCalendar(String name, String zoneId, IEventRepository eventRepository) {
    if (getCalendar(name) != null) {
      throw new InvalidCommandException("Calendar already exists.\n");
    }
    calendars.add(Calendar.builder()
        .name(name)
        .zoneId(zoneId)
        .eventRepository(eventRepository)
        .build());
  }

  @Override
  public void editCalendar(String name, String propertyName, String propertyValue) {
    ICalendar calendarToEdit = getCalendar(name);
    if (calendarToEdit == null) {
      throw new InvalidCommandException("Calendar does not exist.\n");
    }

    CalendarUpdateOperation operation = CalendarPropertyUpdater.getUpdater(propertyName);
    if (operation == null) {
      throw new InvalidCommandException("Invalid property name.\n");
    }

    calendars.remove(calendarToEdit);
    try {
      ICalendar newCalendar = operation.update(calendarToEdit, propertyValue);
      addCalendar(newCalendar.getName(), newCalendar.getZoneId().toString(),
          newCalendar.getEventRepository());
    } catch (Exception e) {
      calendars.add(calendarToEdit);
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Override
  public void copyCalendarEvents(String currentCalendarName,
                                 CopyEventRequestDTO copyEventRequestDTO) {
    ICalendar currentCalendar = getCalendar(currentCalendarName);
    ICalendar toCalendar = getCalendar(copyEventRequestDTO.getCopyCalendarName());

    if (currentCalendar == null || toCalendar == null) {
      throw new InvalidCommandException("Calendar does not exist.\n");
    }

    Temporal endTime = copyEventRequestDTO.getEndTime();
    if (copyEventRequestDTO.getEventName() == null && copyEventRequestDTO.getEndTime() == null) {
      endTime = TimeUtil.getEndOfDayFromString(copyEventRequestDTO.getStartTime().toString());
    }

    toCalendar.getEventRepository().copyEvents(currentCalendar.getEventRepository()
            .getEvents(copyEventRequestDTO.getEventName(),
                copyEventRequestDTO.getStartTime(), endTime,
                copyEventRequestDTO.getEventName() == null ? SearchType.BETWEEN :
                    SearchType.EXACT),
        copyEventRequestDTO.getCopyToDate(), currentCalendar.getZoneId(), toCalendar.getZoneId());
  }

  @Override
  public ICalendar getCalendar(String calendarName) {
    return calendars.stream()
        .filter(calendar -> calendar.getName().equals(calendarName))
        .findFirst().orElse(null);
  }

  @Override
  public List<String> getCalendars() {
    return calendars.stream().map(ICalendar::getName).collect(Collectors.toList());
  }
}
