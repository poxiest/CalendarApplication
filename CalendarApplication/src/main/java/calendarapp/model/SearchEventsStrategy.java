package calendarapp.model;

import java.time.temporal.Temporal;
import java.util.List;

public interface SearchEventsStrategy {
  List<IEvent> search(List<IEvent> events, String eventName, Temporal startTime, Temporal endTime,
                      boolean isRecurring);
}
