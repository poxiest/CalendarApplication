package calendarapp.model;

import calendarapp.model.dto.CopyEventDTO;

public interface ICalendarRepository {

  void addCalendar(String name, String zoneId, IEventRepository eventRepository);

  void editCalendar(String name, String propertyName, String propertyValue);

  void copyEvents(String currentCalendarName, CopyEventDTO copyEventDTO);

  ICalendar getCalendar(String name);
}
