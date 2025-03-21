package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendar;
import calendarapp.model.ICalendarRepository;
import calendarapp.model.IEventRepository;
import calendarapp.model.dto.CopyEventDTO;

public class CalendarRepository implements ICalendarRepository {

  private List<ICalendar> calendars;

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
    ICalendar calendar = getCalendar(name);
    if (calendar == null) {
      throw new InvalidCommandException("Calendar does not exist.\n");
    }

    BiConsumer<Calendar.Builder, String> updater = CalendarPropertyUpdater.getUpdater(propertyName);
    if (updater == null) {
      throw new InvalidCommandException("Invalid property name.\n");
    }

    Calendar.Builder calendarBuilder = Calendar.builder()
        .name(calendar.getName())
        .zoneId(calendar.getZoneId())
        .eventRepository(calendar.getEventRepository());
    updater.accept(calendarBuilder, propertyValue);
    calendars.remove(calendar);
    calendars.add(calendarBuilder.build());
  }

  @Override
  public void copyEvents(String currentCalendarName, CopyEventDTO copyEventDTO) {
    ICalendar currentCalendar = getCalendar(currentCalendarName);
    ICalendar toCalendar = getCalendar(copyEventDTO.getCopyCalendarName());

    if (currentCalendar == null || toCalendar == null) {
      throw new InvalidCommandException("Calendar does not exist.\n");
    }

    IEventRepository currentEventRepository = currentCalendar.getEventRepository();
    IEventRepository toEventRepository = toCalendar.getEventRepository();

    IEventRepository dataToCopy = currentEventRepository.get(copyEventDTO.getEventName(),
        copyEventDTO.getStartTime(), copyEventDTO.getEndTime());
  }

  @Override
  public ICalendar getCalendar(String calendarName) {
    return calendars.stream()
        .filter(calendar -> calendar.getName().equals(calendarName))
        .findFirst().orElse(null);
  }
}
