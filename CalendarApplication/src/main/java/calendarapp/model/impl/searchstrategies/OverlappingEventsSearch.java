package calendarapp.model.impl.searchstrategies;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Collectors;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.IEvent;
import calendarapp.model.SearchEventsStrategy;
import calendarapp.utils.TimeUtil;

/**
 * Implementation of {@link SearchEventsStrategy} for finding overlapping events.
 * This strategy filters events that conflict with the given time range.
 */
public class OverlappingEventsSearch implements SearchEventsStrategy {

  /**
   * Searches for events that overlap with the specified time range.
   *
   * @param events      The list of available events to search through.
   * @param eventName   The name of the event to search for (optional, unused in this strategy).
   * @param startTime   The start time of the time range to check for conflicts.
   * @param endTime     The end time of the time range to check for conflicts.
   * @param isRecurring This parameter is ignored in this strategy since recurrence is not relevant.
   * @return A list of overlapping events sorted in descending order of start time.
   */
  @Override
  public List<IEvent> search(List<IEvent> events, String eventName, Temporal startTime,
                             Temporal endTime, boolean isRecurring) {
    if (endTime != null && startTime != null && TimeUtil.isFirstAfterSecond(startTime, endTime)) {
      throw new InvalidCommandException("Start time must be before end time.");
    }
    return events.stream()
        .filter(event -> eventName == null || event.getName().equals(eventName))
        .filter(event -> (startTime == null && endTime == null)
            || TimeUtil.isConflicting(event.getStartTime(), event.getEndTime(), startTime, endTime)
            || TimeUtil.isEqual(event.getStartTime(), startTime)
            || TimeUtil.isEqual(event.getEndTime(), startTime))
        .sorted((event1, event2) ->
            Math.toIntExact(TimeUtil.difference(event2.getStartTime(), event1.getStartTime())))
        .map(IEvent::deepCopyEvent)
        .collect(Collectors.toList());
  }
}
