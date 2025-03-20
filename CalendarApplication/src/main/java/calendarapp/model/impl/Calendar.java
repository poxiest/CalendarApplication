package calendarapp.model.impl;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendar;
import calendarapp.model.IEventRepository;

public class Calendar implements ICalendar {
  private String name;
  private ZoneId zoneId;
  private IEventRepository eventRepository;

  Calendar(String name, ZoneId zoneId, IEventRepository eventRepository) {
    this.name = name;
    this.zoneId = zoneId;
    this.eventRepository = eventRepository;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ZoneId getZoneId() {
    return zoneId;
  }

  @Override
  public IEventRepository getEventRepository() {
    return eventRepository;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String name;
    private ZoneId zoneId;
    private IEventRepository eventRepository;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder zoneId(String zoneId) {
      try {
        this.zoneId = ZoneId.of(zoneId);
      } catch (ZoneRulesException e) {
        throw new InvalidCommandException("Cannot find zone id : " + zoneId + "\n");
      } catch (DateTimeException e) {
        throw new InvalidCommandException("Invalid zone id : " + zoneId + "\n");
      }
      return this;
    }

    public Builder zoneId(ZoneId zoneId) {
      this.zoneId = zoneId;
      return this;
    }

    public Builder eventRepository(IEventRepository eventRepository) {
      this.eventRepository = eventRepository;
      return this;
    }

    public Calendar build() {
      return new Calendar(name, zoneId, eventRepository);
    }
  }
}
