package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import calendarapp.model.ICalendarRepository;

public class CalendarRepository implements ICalendarRepository {

  private List<Calendar> calendars;

  CalendarRepository(List<Calendar> calendars) {
    this.calendars = new ArrayList<>(calendars);
  }

  @Override
  public void addCalendar(String name, String zoneId) {
    if (getCalendar(name) != null) {
      throw new IllegalArgumentException("Calendar already exists.\n");
    }
    calendars.add(new Calendar(name, zoneId, new EventRepository()));
  }

  @Override
  public void editCalendar(String name, String propertyName, String propertyValue) {
    BiConsumer<Calendar, String> updater = CalendarPropertyUpdater.getUpdater(propertyName);
    if (propertyValue == null) {
      throw new IllegalArgumentException("Invalid property value.\n");
    }
    updater.accept(getCalendar(name), propertyValue);
  }

  @Override
  public void copyEvents(String calendarFrom, String calendarTo, Temporal startTime, Temporal endTime) {

  }

  private Calendar getCalendar(String calendarName) {
    return calendars.stream()
        .filter(calendar -> calendar.getName().equals(calendarName))
        .findFirst().orElse(null);
  }
}
