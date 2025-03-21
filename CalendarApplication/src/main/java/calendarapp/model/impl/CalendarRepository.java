package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendar;
import calendarapp.model.ICalendarRepository;
import calendarapp.model.IEventRepository;

public class CalendarRepository implements ICalendarRepository {

  private List<ICalendar> calendars;

  CalendarRepository() {
    this.calendars = new ArrayList<>();
  }

  @Override
  public void addCalendar(String name, IEventRepository eventRepository) {
    if (getCalendar(name) != null) {
      throw new InvalidCommandException("Calendar already exists.\n");
    }
    calendars.add(Calendar.builder()
        .name(name)
        .eventRepository(eventRepository).build());
  }

  @Override
  public void addCalendar(String name, String zoneId, IEventRepository eventRepository) {
    if (getCalendar(name) != null) {
      throw new InvalidCommandException("Calendar already exists.\n");
    }
    calendars.add(Calendar.builder()
        .name(name)
        .zoneId(zoneId)
        .eventRepository(eventRepository).build());
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
  public void copyEvents(String calendarFrom, String calendarTo, Temporal startTime,
                         Temporal endTime) {

  }

  @Override
  public ICalendar getCalendar(String calendarName) {
    return calendars.stream()
        .filter(calendar -> calendar.getName().equals(calendarName))
        .findFirst().orElse(null);
  }
}
