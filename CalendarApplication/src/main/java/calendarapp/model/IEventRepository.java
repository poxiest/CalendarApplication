package calendarapp.model;

import java.time.temporal.Temporal;
import java.util.List;

public interface IEventRepository {
  void create(String eventName, Temporal startTime, Temporal endTime,
              String description, String location, String visibility,
              String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
              boolean autoDecline) throws EventConflictException;

  void update(String eventName, Temporal startTime, Temporal endTime, String property,
              String value, boolean isRecurring);

  List<IEvent> get(String eventName, Temporal startTime, Temporal endTime);

  List<String> getFormattedEvents(Temporal startTime, Temporal endTime);

  boolean isActiveAt(Temporal time);
}
