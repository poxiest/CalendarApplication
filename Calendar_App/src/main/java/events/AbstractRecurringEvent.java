package events;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An abstract class which implements the methods shared
 * by recurring events.
 */
public class AbstractRecurringEvent<T extends IRecurringEvent<T>> extends Event {

  private List<DayOfWeek> weekdays;
  private List<T> eventsInSeries;
  private UUID recurringSequenceId;

  /**
   * A constructor for an abstract recurring event
   * which initializes properties common between
   * recurring events.
   * @param subject the event subject.
   * @param startDateTime the start date time.
   * @param endDateTime the end date time.
   * @param isAllDayEvent if this is an all day event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate this this is a private event.
   * @param weekdays the weekdays this event repeats on.
   * @param id the id of the event.
   */
  protected AbstractRecurringEvent(
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      UUID id
  ) {
    super(subject, startDateTime, endDateTime, isAllDayEvent, description, location, isPrivate, id);
    this.weekdays = weekdays;
  }

  public void setEventsInSeries(List<T> eventsInSeries) {
    this.eventsInSeries = eventsInSeries;
  }

  public UUID getRecurringSequenceId() {
    return this.recurringSequenceId;
  }

  public void setRecurringSequenceId(UUID id) {
    this.recurringSequenceId = id;
  }

  public List<DayOfWeek> getWeekdays() {
    return this.weekdays;
  }

  public List<T> getEventsInSeries() {
    return this.eventsInSeries;
  }

  /**
   * Gets all events in the series after and including
   * this event.
   * @return the list of all relevant recurring events.
   */
  public List<T> getThisAndFollowing() {
    List<T> eventsFromThis = new ArrayList<T>(0);
    for (T event: eventsInSeries) {
      if (
          event.getStartDateTime().isEqual(this.getStartDateTime())
          || event.getStartDateTime().isAfter(this.getStartDateTime())
      ) {
        eventsFromThis.add(event);
      }
    }
    return eventsFromThis;
  }

}
