package calendarapp.model.calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import calendarapp.model.event.EventVisibility;
import calendarapp.model.event.IEvent;

public interface ICalendarApplication {

  void createEvent(String eventName, LocalDateTime startTime, LocalDateTime endTime,
                   String recurringDays, Integer occurrenceCount, LocalDate recurrenceEndDate,
                   String description, String location, EventVisibility visibility,
                   boolean autoDecline);

  void editEvent(String eventName, LocalDateTime startTime, LocalDateTime endTime, String property, String value);

  List<IEvent> printEvents(LocalDateTime startTime, LocalDateTime endTime);

  void export();

  void showStatus(LocalDateTime dateTime);

}
