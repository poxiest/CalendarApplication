package events;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * An implementation of a recurring until event which is a recurring
 * event with an until dateTime indicating when the recurring series
 * ends.
 */
public class RecurringUntilEvent 
    extends AbstractRecurringEvent<IRecurringUntilEvent> 
    implements IRecurringUntilEvent {

  private LocalDateTime untilDateTime;

  /**
   * A constructor for a recurring until event which initializes
   * the properties of this event.
   * @param subject the subject of this event.
   * @param startDateTime the startDateTime of this event.
   * @param endDateTime the endDateTime of this event.
   * @param isAllDayEvent if this is an all day event.
   * @param description the description of this event.
   * @param location the location for this event.
   * @param isPrivate if this is a private event.
   * @param weekdays the weekdays this event repeats on.
   * @param untilDateTime the date which this recurring series ends.
   * @param id the id of the event.
   */
  private RecurringUntilEvent(
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      LocalDateTime untilDateTime,
      UUID id
  ) {
    super(
        subject, 
        startDateTime, 
        endDateTime, 
        isAllDayEvent, 
        description, 
        location, 
        isPrivate, 
        weekdays,
        id
    );
    this.untilDateTime = untilDateTime;
  }

  public LocalDateTime getUntilDatetime() {
    return this.untilDateTime;
  }

  /**
   * An implementation of a recurring until event builder
   * which allows creating sequence instances according to the
   * builder design pattern.
   */
  public static class RecurringUntilEventBuilder 
      extends AbstractEventBuilder<RecurringUntilEventBuilder> {

    private List<DayOfWeek> weekdays;
    private LocalDateTime untilDateTime;

    @Override
    public RecurringUntilEventBuilder returnBuilder() {
      return this;
    }

    @Override
    public RecurringUntilEventBuilder fromEvent(IEvent event) {
      IRecurringUntilEvent eventCopy = (RecurringUntilEvent) event;
      this.subject = eventCopy.getSubject();
      this.isAllDayEvent = eventCopy.isAllDayEvent();
      this.startDateTime = eventCopy.getStartDateTime();
      this.endDateTime = eventCopy.getEndDateTime();
      this.description = eventCopy.getDescription();
      this.location = eventCopy.getLocation();
      this.isPrivate = eventCopy.isPrivate();
      this.weekdays = eventCopy.getWeekdays();
      this.untilDateTime = eventCopy.getUntilDatetime();
      return this;
    }

    public RecurringUntilEventBuilder weekdays(List<DayOfWeek> weekdays) {
      this.weekdays = weekdays;
      return this;
    }

    public RecurringUntilEventBuilder untilDateTime(LocalDateTime untilDateTime) {
      this.untilDateTime = untilDateTime;
      return this;
    }

    @Override
    public RecurringUntilEvent build() {
      return new RecurringUntilEvent(
        this.subject,
        this.startDateTime,
        this.endDateTime,
        this.isAllDayEvent,
        this.description,
        this.location,
        this.isPrivate,
        this.weekdays,
        this.untilDateTime,
        this.id
      );
    }
  }
}
