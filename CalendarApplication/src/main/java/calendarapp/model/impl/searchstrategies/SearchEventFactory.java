package calendarapp.model.impl.searchstrategies;

import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.model.IEvent;
import calendarapp.model.SearchEventsStrategy;

public class SearchEventFactory {
  public List<IEvent> search(List<IEvent> events, String eventName, Temporal startTime,
                             Temporal endTime, boolean isRecurring, SearchType searchType) {
    SearchEventsStrategy strategy = selectStrategy(searchType);
    return strategy.search(events, eventName, startTime, endTime, isRecurring);
  }

  private SearchEventsStrategy selectStrategy(SearchType searchType) {
    switch (searchType) {
      case OVERLAPPING:
        return new OverlappingEventsSearch();
      case MATCHING:
        return new InBetweenEventsSearch();
      case EXACT:
        return new ExactMatchEventsSearch();
      default:
        throw new IllegalArgumentException("Unknown search type: " + searchType);
    }
  }
}
