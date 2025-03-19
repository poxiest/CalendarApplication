package calendarapp.model;

import java.time.temporal.Temporal;

public interface IEventRepository {
  void create(String eventName, Temporal startTime, Temporal endTime,
              String description, String location, String visibility,
              String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
              boolean autoDecline) throws EventConflictException;

  void update(String eventName, Temporal startTime, Temporal endTime, String property,
              String value, boolean isRecurring);

  IEventRepository get(String eventName, Temporal startTime, Temporal endTime);
}
