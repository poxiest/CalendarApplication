package calendarapp.model;

import java.io.IOException;
import java.time.temporal.Temporal;
import java.util.List;

public interface ICalendarModel {

  void createEvent(String eventName, Temporal startTime, Temporal endTime,
                   String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
                   String description, String location, String visibility,
                   boolean autoDecline);

  void editEvent(String eventName, Temporal startTime, Temporal endTime, String property, String value);

  List<String> printEvents(Temporal startTime, Temporal endTime);

  String export(String filename) throws IOException;

  String showStatus(Temporal dateTime);
}