package events;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Implementation of the IEvent which allows accessing event
 * properties and an event builder. The event builder allows
 * constructing new events according to the builder design
 * pattern.
 */
public class Event implements IEvent {
  private String subject;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private Boolean isAllDayEvent;
  private String description;
  private String location;
  private Boolean isPrivate;
  private UUID id;

  /**
   * Constructor for the Event class which creates an
   * Event instance.
   * @param subject the subject of the event.
   * @param startDateTime the zoned date time when this event begins.
   * @param endDateTime the zoned date time when this event ends.
   * @param isAllDayEvent true if this is an all day event, false otherwise.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate true if this event is private, false otherwise.
   * @param id the id of the event.
   */
  protected Event(
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      UUID id
  ) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.isAllDayEvent = isAllDayEvent;
    this.description = description;
    this.location = location;
    this.isPrivate = isPrivate;
    this.id = id;
  }

  @Override
  public UUID getId() {
    return this.id;
  }

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public LocalDate getStartDate() {
    return this.startDateTime.toLocalDate();
  }

  @Override
  public LocalDate getEndDate() {
    return this.endDateTime.toLocalDate();
  }

  @Override
  public LocalTime getStartTime() {
    return this.startDateTime.toLocalTime();
  }

  @Override
  public LocalTime getEndTime() {
    return this.endDateTime.toLocalTime();
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return this.startDateTime;
  }

  @Override
  public LocalDateTime getEndDateTime() {
    return this.endDateTime;
  }

  @Override
  public Boolean isAllDayEvent() {
    return this.isAllDayEvent;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getLocation() {
    return this.location;
  }

  @Override
  public Boolean isPrivate() {
    return this.isPrivate;
  }

  /**
   * This is an EventBuilder class which allows setting
   * event properties according to the Builder design
   * pattern.
   */
  public static class EventBuilder extends AbstractEventBuilder<EventBuilder> {

    @Override
    public EventBuilder returnBuilder() {
      return this;
    }

    @Override
    public EventBuilder fromEvent(IEvent event) {
      this.subject = event.getSubject();
      this.isAllDayEvent = event.isAllDayEvent();
      this.startDateTime = event.getStartDateTime();
      this.endDateTime = event.getEndDateTime();
      this.description = event.getDescription();
      this.location = event.getLocation();
      this.isPrivate = event.isPrivate();
      return returnBuilder();
    }

    /**
     * Creates a new Event instance from the properties set in this EventBuilder.
     * @return the new Event instance.
     */
    @Override
    public IEvent build() {
      return new Event(
        this.subject,
        this.startDateTime,
        this.endDateTime,
        this.isAllDayEvent,
        this.description,
        this.location,
        this.isPrivate,
        this.id
      );
    }
  }
}