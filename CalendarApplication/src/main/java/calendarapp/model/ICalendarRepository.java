package calendarapp.model;

import java.time.temporal.Temporal;

public interface ICalendarRepository {

  void addCalendar(String name, String zoneId);

  void editCalendar(String name, String propertyName, String propertyValue);

  void copyEvents(String calendarFrom, String calendarTo, Temporal startTime, Temporal endTime);
}
