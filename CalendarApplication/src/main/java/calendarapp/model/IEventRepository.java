package calendarapp.model;

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.model.dto.CopyEventRequestDTO;

public interface IEventRepository {
  void create(String eventName, Temporal startTime, Temporal endTime,
              String description, String location, String visibility,
              String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
              boolean autoDecline) throws EventConflictException;

  void update(String eventName, Temporal startTime, Temporal endTime, String property,
              String value);

  List<IEvent> getInBetweenEvents(String eventName, Temporal startTime, Temporal endTime);

  List<IEvent> getOverlappingEvents(Temporal startTime, Temporal endTime);

  void copyEvents(List<IEvent> eventsToCopy, CopyEventRequestDTO copyEventRequestDTO,
                  ZoneId fromZoneId, ZoneId toZoneId);

  void changeTimeZone(ZoneId from, ZoneId to);

  boolean isActiveAt(Temporal time);

  String export(String fileName, ICalendarExporter exporter);
}
