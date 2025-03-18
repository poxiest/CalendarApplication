package calendarapp.model.impl;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.IEventRepository;

public class Calendar {
  private String name;
  private ZoneId zoneId;
  private IEventRepository eventRepository;

  Calendar(String name, String zoneId, IEventRepository eventRepository) {
    this.name = name;
    setZoneId(zoneId);
    this.eventRepository = eventRepository;
  }

  String getName() {
    return name;
  }

  ZoneId getZoneId() {
    return zoneId;
  }

  IEventRepository getEventRepository() {
    return eventRepository;
  }

  void setName(String name) {
    this.name = name;
  }

  void setZoneId(String zoneId) {
    try {
      this.zoneId = ZoneId.of(zoneId);
    } catch (ZoneRulesException e) {
      throw new InvalidCommandException("Cannot find zone id : " + zoneId + "\n");
    } catch (DateTimeException e) {
      throw new InvalidCommandException("Invalid zone id : " + zoneId + "\n");
    }
  }
}
