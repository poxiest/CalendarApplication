package calendarapp.model.calendar;


import java.io.IOException;
import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.model.event.IEvent;

public interface ICalendarApplication {

  void createEvent(String eventName, Temporal startTime, Temporal endTime,
                   String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
                   String description, String location, String visibility,
                   boolean autoDecline);

  void editEvent(String eventName, Temporal startTime, Temporal endTime, String property, String value);

  List<IEvent> printEvents(Temporal startTime, Temporal endTime);

  void export(String filename) throws IOException;

  String showStatus(Temporal dateTime);
}
