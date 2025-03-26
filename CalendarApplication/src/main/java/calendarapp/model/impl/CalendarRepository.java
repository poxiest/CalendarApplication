package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import calendarapp.controller.InvalidCommandException;
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

    BiConsumer<Calendar.Builder, String> updater = CalendarPropertyUpdater.getUpdater(propertyName);
    if (updater == null) {
      throw new InvalidCommandException("Invalid property name.\n");
    }

    Calendar.Builder calendarBuilder = Calendar.builder()
        .name(calendarToEdit.getName())
        .zoneId(calendarToEdit.getZoneId())
        .eventRepository(calendarToEdit.getEventRepository());
    updater.accept(calendarBuilder, propertyValue);

    ICalendar newCalendar = calendarBuilder.build();
    if (propertyName.equals(Constants.Calendar.CALENDAR_TIME_ZONE)) {
      newCalendar.getEventRepository().changeTimeZone(calendarToEdit.getZoneId(),
          newCalendar.getZoneId());
    }
    calendars.add(newCalendar);
    calendars.remove(calendarToEdit);
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
                copyEventRequestDTO.getEventName() == null ? SearchType.MATCHING :
                    SearchType.EXACT),
        copyEventRequestDTO.getCopyToDate(), currentCalendar.getZoneId(), toCalendar.getZoneId());
  }

  @Override
  public ICalendar getCalendar(String calendarName) {
    return calendars.stream()
        .filter(calendar -> calendar.getName().equals(calendarName))
        .findFirst().orElse(null);
  }
}
