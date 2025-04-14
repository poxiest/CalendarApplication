package events;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * An implementation of a recurring sequence event which outlines operations
 * that can be performed on these events. Recurring sequence events have 
 * a sequence size and an index in a recurring sequence.
 */
public class RecurringSequenceEvent 
    extends AbstractRecurringEvent<IRecurringSequenceEvent> 
    implements IRecurringSequenceEvent {

  private int sequenceSize;

  /**
   * A constructor for a recurring sequence event which initializes
   * the properties for this event.
   * @param subject the subject of this event.
   * @param startDateTime the startDateTime of this event.
   * @param endDateTime the endDateTime of this event.
   * @param isAllDayEvent if this is an all day event.
   * @param description the description of this event.
   * @param location the location for this event.
   * @param isPrivate if this is a private event.
   * @param weekdays the weekdays this event repeats on.
   * @param sequenceSize the sequence size associated with this event.
   * @param index the index of this event in its recurring sequence.
   * @param id the id of the event.
   */
  private RecurringSequenceEvent(
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      int sequenceSize,
      int index,
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
    this.sequenceSize = sequenceSize;
  }

  public int getSequenceSize() {
    return this.sequenceSize;
  }


  /**
   * An implementation of a recurring sequence event builder
   * which allows creating sequence instances according to the
   * builder design pattern.
   */
  public static class RecurringSequenceEventBuilder 
      extends AbstractEventBuilder<RecurringSequenceEventBuilder> {

    private List<DayOfWeek> weekdays;
    private int sequenceSize;
    private int index;

    @Override
    public RecurringSequenceEventBuilder returnBuilder() {
      return this;
    }

    @Override
    public RecurringSequenceEventBuilder fromEvent(IEvent event) {
      IRecurringSequenceEvent eventCopy = (RecurringSequenceEvent) event;
      this.subject = eventCopy.getSubject();
      this.isAllDayEvent = eventCopy.isAllDayEvent();
      this.startDateTime = eventCopy.getStartDateTime();
      this.endDateTime = eventCopy.getEndDateTime();
      this.description = eventCopy.getDescription();
      this.location = eventCopy.getLocation();
      this.isPrivate = eventCopy.isPrivate();
      this.weekdays = eventCopy.getWeekdays();
      this.sequenceSize = eventCopy.getSequenceSize();
      return this;
    }

    public RecurringSequenceEventBuilder weekdays(List<DayOfWeek> weekdays) {
      this.weekdays = weekdays;
      return this;
    }

    public RecurringSequenceEventBuilder sequenceSize(int sequenceSize) {
      this.sequenceSize = sequenceSize;
      return this;
    }
    
    @Override
    public RecurringSequenceEvent build() {
      return new RecurringSequenceEvent(
        this.subject,
        this.startDateTime,
        this.endDateTime,
        this.isAllDayEvent,
        this.description,
        this.location,
        this.isPrivate,
        this.weekdays,
        this.sequenceSize,
        this.index,
        this.id
      );
    }

  }

}
