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

public class InBetweenEventsSearch implements SearchEventsStrategy {
  @Override
  public List<IEvent> search(List<IEvent> events, String eventName, Temporal startTime,
                             Temporal endTime, boolean isRecurring) {
    return events.stream()
        .filter(event -> eventName == null || event.getName().equals(eventName))
        .filter(event -> startTime == null || isFirstAfterSecond(event.getStartTime(), startTime) || isEqual(event.getStartTime(), startTime))
        .filter(event -> endTime == null || isFirstBeforeSecond(event.getEndTime(), endTime) || isEqual(event.getEndTime(), endTime))
        .filter(event -> !isRecurring || (event.getRecurringDays() != null))
        .sorted((event1, event2) ->
            Math.toIntExact(TimeUtil.difference(event2.getStartTime(), event1.getStartTime())))
        .collect(Collectors.toList());
  }
}
