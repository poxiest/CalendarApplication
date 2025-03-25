package calendarapp.model.impl;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendar;
import calendarapp.model.IEventRepository;

/**
 * Represents a calendar with a name, time zone, and event repository.
 * Implements the ICalendar interface.
 */
public class Calendar implements ICalendar {
  private final String name;
  private final ZoneId zoneId;
  private final IEventRepository eventRepository;

  /**
   * Constructs a Calendar instance with the given name, time zone, and event repository.
   *
   * @param name the name of the calendar
   * @param zoneId the time zone of the calendar
   * @param eventRepository the event repository associated with the calendar
   */
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

  /**
   * Creates and returns a new Builder instance for constructing a Calendar.
   *
   * @return a new Builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder class for constructing Calendar instances.
   */
  public static class Builder {
    private String name;
    private ZoneId zoneId = ZoneId.of(Constants.Calendar.DEFAULT_TIME_ZONE);
    private IEventRepository eventRepository;

    /**
     * Sets the name of the calendar.
     *
     * @param name the calendar name
     * @return this Builder instance
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the time zone of the calendar from a string.
     *
     * @param zoneId the time zone as a string
     * @return this Builder instance
     * @throws InvalidCommandException if the time zone string is invalid or unknown
     */
    public Builder zoneId(String zoneId) {
      if (zoneId != null) {
        try {
          this.zoneId = ZoneId.of(zoneId);
        } catch (ZoneRulesException e) {
          throw new InvalidCommandException("Cannot find zone id : " + zoneId + "\n");
        } catch (DateTimeException e) {
          throw new InvalidCommandException("Invalid zone id : " + zoneId + "\n");
        }
      }
      return this;
    }

    /**
     * Sets the time zone of the calendar using a ZoneId.
     *
     * @param zoneId the ZoneId object
     * @return this Builder instance
     */
    public Builder zoneId(ZoneId zoneId) {
      if (zoneId != null) {
        this.zoneId = zoneId;
      }
      return this;
    }

    /**
     * Sets the event repository for the calendar.
     *
     * @param eventRepository the event repository
     * @return this Builder instance
     */
    public Builder eventRepository(IEventRepository eventRepository) {
      this.eventRepository = eventRepository;
      return this;
    }

    /**
     * Builds and returns a Calendar instance using the provided properties.
     *
     * @return the constructed Calendar object
     * @throws InvalidCommandException if name or event repository is missing
     */
    public Calendar build() {
      if (name == null) {
        throw new InvalidCommandException("Calendar name cannot be null or empty.\n");
      }
      if (eventRepository == null) {
        throw new InvalidCommandException("Event repository cannot be null.\n");
      }
      return new Calendar(name, zoneId, eventRepository);
    }
  }
}
