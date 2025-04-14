package model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import calendar.ConflictException;
import events.Event;
import events.IEvent;
import events.SingleEventProperties;

/**
 * An implementation of a zoned calendar model which is an extension of
 * a standard calendar model that supports a time zone.
 */
public class ZonedCalendarModel extends CalendarModel implements IZonedCalendarModel {

  private ZoneId zoneId;

  /**
   * A constructor for the zoned calendar model which initializes
   * an empty list of calendar events and a time zone id
   * that applies to all events in this calendar.
   * @param zoneId the time zone id to set for this calendar.
   */
  public ZonedCalendarModel(ZoneId zoneId) {
    super();
    this.zoneId = zoneId;
  }

  @Override
  public ZoneId getZoneId() {
    return this.zoneId;
  }

  @Override
  public void updateCalendarZoneId(ZoneId newZoneId) {
    List<IEvent> events = queryDateRange(LocalDateTime.MIN, LocalDateTime.MAX);
    for (IEvent event: events) {
      ZonedDateTime startDateTime = ZonedDateTime.of(event.getStartDateTime(), getZoneId());
      ZonedDateTime endDateTime = ZonedDateTime.of(event.getEndDateTime(), getZoneId());
      startDateTime = startDateTime.withZoneSameInstant(newZoneId);
      endDateTime = endDateTime.withZoneSameInstant(newZoneId);
      event = new Event.EventBuilder()
        .fromEvent(event)
        .setPropertyWithName(SingleEventProperties.STARTDATE, 
            startDateTime.toLocalDate().toString())
        .setPropertyWithName(SingleEventProperties.STARTTIME, 
            startDateTime.toLocalTime().toString())
        .setPropertyWithName(SingleEventProperties.ENDDATE, 
            endDateTime.toLocalDate().toString())
        .setPropertyWithName(SingleEventProperties.ENDTIME, 
            endDateTime.toLocalTime().toString())
        .build();
    }
    this.zoneId = newZoneId;
  }

  @Override
  public void addEventAndCheckConflicts(IEvent event) throws ConflictException {
    checkConflict(event);
    this.events.add(event);
  }

}
