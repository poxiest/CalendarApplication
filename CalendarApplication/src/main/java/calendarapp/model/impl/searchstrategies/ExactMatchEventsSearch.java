package calendarapp.model.impl.searchstrategies;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Collectors;

import calendarapp.model.IEvent;
import calendarapp.model.SearchEventsStrategy;
import calendarapp.utils.TimeUtil;

import static calendarapp.utils.TimeUtil.isEqual;

/**
 * Implementation of {@link SearchEventsStrategy} for finding events that exactly match
 * the given name, start time, and end time.
 * This strategy returns events that fully align with the provided criteria.
 */
public class ExactMatchEventsSearch implements SearchEventsStrategy {

  /**
   * Searches for events that exactly match the specified name, start time, and end time.
   *
   * @param events      The list of available events to search through.
   * @param eventName   The name of the event to search for (optional, null to ignore).
   * @param startTime   The exact start time to match (optional, null to ignore).
   * @param endTime     The exact end time to match (optional, null to ignore).
   * @param isRecurring This parameter is ignored in this strategy since recurrence is not relevant.
   * @return A list of events that match the given criteria, sorted in descending order of start
   *     time.
   */
  @Override
  public List<IEvent> search(List<IEvent> events, String eventName, Temporal startTime,
                             Temporal endTime, boolean isRecurring) {
    return events.stream()
        .filter(event -> eventName == null || event.getName().equals(eventName))
        .filter(event -> startTime == null || isEqual(event.getStartTime(), startTime))
        .filter(event -> endTime == null || isEqual(event.getEndTime(), endTime))
        .sorted((event1, event2) ->
            Math.toIntExact(TimeUtil.difference(event2.getStartTime(), event1.getStartTime())))
        .collect(Collectors.toList());
  }
}
