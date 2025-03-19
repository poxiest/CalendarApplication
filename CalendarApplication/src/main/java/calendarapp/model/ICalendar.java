package calendarapp.model;

import java.time.ZoneId;

public interface ICalendar {

  String getName();

  ZoneId getZoneId();

  IEventRepository getEventRepository();
}
