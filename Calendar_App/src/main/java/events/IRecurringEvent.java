package events;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

/**
 * A generic interface for recurring events which provides method
 * signatures for operations that can be performed on these events.
 */
public interface IRecurringEvent<T> extends IEvent {

  /**
   * Gets the weekdays relevent to this recurring event.
   * @return a list of weekdays this recurring event falls on.
   */
  public List<DayOfWeek> getWeekdays();

  /**
   * Gets all events in the series.
   * @return the list of all recurring events in this series.
   */
  public List<T> getEventsInSeries();

  /**
   * Sets the events in this recurring event series.
   * @param eventsInSeries a list of all events in this recurring series.
   */
  public void setEventsInSeries(List<T> eventsInSeries);

  /**
   * Gets all events in the series after and including
   * this event.
   * @return the list of all relevant recurring events.
   */
  public List<T> getThisAndFollowing();


  /**
   * Gets the unique identifier associated with this
   * sequence.
   * @return the UUID.
   */
  public UUID getRecurringSequenceId();

  /**
   * Sets the unique identifier associated with this
   * sequence.
   * @param id the UUID to set.
   */
  public void setRecurringSequenceId(UUID id);

}
