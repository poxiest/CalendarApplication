package calendarapp.model;

import calendarapp.model.dto.CopyEventRequestDTO;

public interface ICalendarRepository {

  void addCalendar(String name, String zoneId, IEventRepository eventRepository);

  void editCalendar(String name, String propertyName, String propertyValue);

  void copyCalendarEvents(String currentCalendarName, CopyEventRequestDTO copyEventRequestDTO);

  ICalendar getCalendar(String name);
}
