package calendarapp.model.impl.searchstrategies;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Collectors;

import calendarapp.model.IEvent;
import calendarapp.model.SearchEventsStrategy;
import calendarapp.utils.TimeUtil;

public class OverlappingEventsSearch implements SearchEventsStrategy {
  @Override
  public List<IEvent> search(List<IEvent> events, String eventName, Temporal startTime,
                             Temporal endTime, boolean isRecurring) {
    return events.stream()
        .filter(event -> TimeUtil.isConflicting(event.getStartTime(),
            event.getEndTime(), startTime, endTime))
        .sorted((event1, event2) ->
            Math.toIntExact(TimeUtil.difference(event2.getStartTime(), event1.getStartTime())))
        .map(IEvent::deepCopyEvent)
        .collect(Collectors.toList());
  }
}
