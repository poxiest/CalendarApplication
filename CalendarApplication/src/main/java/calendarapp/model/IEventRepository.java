package calendarapp.model;

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.model.dto.CopyEventDTO;

public interface IEventRepository {
  void create(String eventName, Temporal startTime, Temporal endTime,
              String description, String location, String visibility,
              String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
              boolean autoDecline) throws EventConflictException;

  void update(String eventName, Temporal startTime, Temporal endTime, String property,
              String value, boolean isRecurring);

  List<IEvent> get(String eventName, Temporal startTime, Temporal endTime);

  void copyEvents(List<IEvent> eventsToCopy, CopyEventDTO copyEventDTO,
                  ZoneId fromZoneId, ZoneId toZoneId);

  void changeTimeZone(ZoneId from, ZoneId to);

  List<String> getFormattedEvents(Temporal startTime, Temporal endTime);

  boolean isActiveAt(Temporal time);

  String export(String fileName, ICalendarExporter exporter);
}
