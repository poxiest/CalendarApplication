package calendarapp.model;

import java.time.temporal.Temporal;
import java.util.List;

/**
 * Strategy interface for searching events based on different criteria.
 */
public interface SearchEventsStrategy {

  /**
   * Searches for events based on the given parameters.
   *
   * @param events      The list of available events.
   * @param eventName   The name of the event (optional).
   * @param startTime   The start time filter (optional).
   * @param endTime     The end time filter (optional).
   * @param isRecurring Whether to filter only recurring events.
   * @return A list of matching events.
   */
  List<IEvent> search(List<IEvent> events, String eventName, Temporal startTime, Temporal endTime,
                      boolean isRecurring);
}
