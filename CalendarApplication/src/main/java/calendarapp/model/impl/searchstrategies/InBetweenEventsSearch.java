package calendarapp.model.impl.searchstrategies;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Collectors;

import calendarapp.model.IEvent;
import calendarapp.model.SearchEventsStrategy;
import calendarapp.utils.TimeUtil;

import static calendarapp.utils.TimeUtil.isEqual;
import static calendarapp.utils.TimeUtil.isFirstAfterSecond;
import static calendarapp.utils.TimeUtil.isFirstBeforeSecond;

/**
 * Implementation of {@link SearchEventsStrategy} for finding events within a specified time range.
 * This strategy searches for events that start and end within the given time range.
 */
public class InBetweenEventsSearch implements SearchEventsStrategy {

  /**
   * Searches for events that fall within the specified time range.
   *
   * @param events      The list of available events to search through.
   * @param eventName   The name of the event to search for (optional, null to ignore).
   * @param startTime   The start time of the search range (optional, null to ignore).
   * @param endTime     The end time of the search range (optional, null to ignore).
   * @param isRecurring Whether to filter only recurring events (true to include only recurring
   *                    events).
   * @return A list of events that match the given criteria, sorted in descending order of start
   * time.
   */
  @Override
  public List<IEvent> search(List<IEvent> events, String eventName, Temporal startTime,
                             Temporal endTime, boolean isRecurring) {
    return events.stream()
        .filter(event -> eventName == null || event.getName().equals(eventName))
        .filter(event -> startTime == null
            || isFirstAfterSecond(event.getStartTime(), startTime)
            || isEqual(event.getStartTime(), startTime))
        .filter(event -> endTime == null
            || isFirstBeforeSecond(event.getEndTime(), endTime)
            || isEqual(event.getEndTime(), endTime))
        .filter(event -> !isRecurring || (event.getRecurringDays() != null))
        .sorted((event1, event2) ->
            Math.toIntExact(TimeUtil.difference(event2.getStartTime(), event1.getStartTime())))
        .collect(Collectors.toList());
  }
}
