package events;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Constructs a new abstract event builder with methods shared by all event
 * builders.
 */
public abstract class AbstractEventBuilder<T extends AbstractEventBuilder<T>> {
  protected String subject;
  protected Boolean isAllDayEvent;
  protected LocalDateTime startDateTime;
  protected LocalDateTime endDateTime;
  protected String description;
  protected String location;
  protected Boolean isPrivate;
  protected UUID id;

  /**
   * Returns this AbstractEventBuilder.
   * @return this AbstractEventBuilder.
   */
  public abstract T returnBuilder();

  /**
   * Creates a new event which extends the base IEvent interface.
   * @return the event built by this event builder.
   */
  public abstract IEvent build();

  /**
   * Copies all event properties from the given
   * event to this EventBuilder.
   * @param event the event to copy properties from.
   * @return this EventBuilder.
   */
  public abstract T fromEvent(IEvent event);

  /**
   * Sets the property of this event builder with the string value.
   * @param property the property name to set.
   * @param value the string value of the property.
   * @return this EventBuilder.
   */
  public T setPropertyWithName(SingleEventProperties property, String value) {
    switch (property) {
      case SUBJECT:
        this.subject = value;
        break;
      case STARTDATE:
        LocalDate startDate = LocalDate.parse(value);
        LocalTime currentStartTime = this.startDateTime.toLocalTime();
        this.startDateTime = LocalDateTime.of(startDate, currentStartTime);
        if (startDate.isAfter(endDateTime.toLocalDate())) {
          this.endDateTime = LocalDateTime.of(startDate, endDateTime.toLocalTime());
        }
        break;
      case STARTTIME:
        LocalTime startTime = LocalTime.parse(value);
        Duration duration = Duration.between(startDateTime, endDateTime);
        this.startDateTime = LocalDateTime.of(startDateTime.toLocalDate(), startTime);
        endDateTime = endDateTime.plus(duration);
        break;
      case ENDDATE:
        LocalDate endDate = LocalDate.parse(value);
        LocalTime currentEndTime = this.startDateTime.toLocalTime();
        this.endDateTime = LocalDateTime.of(endDate, currentEndTime);
        break;
      case ENDTIME:
        LocalTime endTime = LocalTime.parse(value);
        LocalDate currentEndDate = this.endDateTime.toLocalDate();
        this.endDateTime = LocalDateTime.of(currentEndDate, endTime);
        break;
      case ALLDAYEVENT:
        this.isAllDayEvent = Boolean.parseBoolean(value);
        this.startDateTime = LocalDateTime.of(startDateTime.toLocalDate(), LocalTime.MIN);
        this.endDateTime = LocalDateTime.of(endDateTime.toLocalDate(), LocalTime.MAX);
        break;
      case DESCRIPTION:
        this.description = value;
        break;
      case LOCATION:
        this.location = value;
        break;
      case PRIVATE:
        this.isPrivate = Boolean.parseBoolean(value);
        break;
      default:
        break;
    }
    return returnBuilder();
  }

  /**
   * Sets the unique id associated with this event.
   * @param id the id.
   * @return this EventBuilder.
   */
  public T setId(UUID id) {
    this.id = id;
    return returnBuilder();
  }

  /**
   * Sets the subject of this EventBuilder.
   * @param subject the subject of the event.
   * @return this EventBuilder.
   */
  public T subject(String subject) {
    this.subject = subject;
    return returnBuilder();
  }

  /**
   * Sets the zoned start date time of this EventBuilder.
   * @param startDateTime the startDateTime when this event begins.
   * @return this EventBuilder.
   */
  public T startDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
    return returnBuilder();
  }

  /**
   * Sets the zoned end date time of this EventBuilder.
   * @param endDateTime the endDateTime when this event begins.
   * @return this EventBuilder.
   */
  public T endDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
    return returnBuilder();
  }

  /**
   * Sets is all day status of this EventBuilder.
   * @param isAllDayEvent true if this is an all day event, false otherwise.
   * @return this EventBuilder.
   */
  public T isAllDayEvent(Boolean isAllDayEvent) {
    this.isAllDayEvent = isAllDayEvent;
    return returnBuilder();
  }

  /**
   * Sets the description of this EventBuilder.
   * @param description the description of this event.
   * @return this EventBuilder.
   */
  public T description(String description) {
    this.description = description;
    return returnBuilder();
  }

  /**
   * Sets the location of this EventBuilder.
   * @param location the location of this event.
   * @return this EventBuilder.
   */
  public T location(String location) {
    this.location = location;
    return returnBuilder();
  }

  /**
   * Sets if this is a private event.
   * @param isPrivate true if this is a private event, false otherwise.
   * @return this EventBuilder.
   */
  public T isPrivate(Boolean isPrivate) {
    this.isPrivate = isPrivate;
    return returnBuilder();
  }
}
