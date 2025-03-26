package calendarapp.model.impl.searchstrategies;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Collectors;

import calendarapp.model.IEvent;
import calendarapp.model.SearchEventsStrategy;
import calendarapp.utils.TimeUtil;

import static calendarapp.utils.TimeUtil.isEqual;

public class ExactMatchEventsSearch implements SearchEventsStrategy {
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
