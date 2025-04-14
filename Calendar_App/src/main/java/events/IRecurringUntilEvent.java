package events;

import java.time.LocalDateTime;

/**
 * An interface for recurring until events which have an until
 * date time reflecting when the series ends.
 */
public interface IRecurringUntilEvent extends IRecurringEvent<IRecurringUntilEvent> {

  /**
   * Gets the end dateTime of this recurring sequence.
   * @return the dateTime to stop recurring this event.
   */
  public LocalDateTime getUntilDatetime();
  
}
