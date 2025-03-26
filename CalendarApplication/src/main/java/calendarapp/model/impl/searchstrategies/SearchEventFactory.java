package calendarapp.model.impl.searchstrategies;

import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.model.IEvent;
import calendarapp.model.SearchEventsStrategy;
import calendarapp.model.SearchType;

/**
 * Factory class for selecting and executing different event search strategies.
 * Based on the provided {@link SearchType}, it applies the corresponding search strategy
 * to filter and retrieve matching events.
 */
public class SearchEventFactory {

  /**
   * Searches for events based on the specified criteria and search type.
   *
   * @param events      The list of available events to search through.
   * @param eventName   The name of the event to search for (optional).
   * @param startTime   The start time to filter events (optional).
   * @param endTime     The end time to filter events (optional).
   * @param isRecurring Whether to filter only recurring events.
   * @param searchType  The type of search strategy to apply.
   * @return A list of events matching the search criteria.
   * @throws IllegalArgumentException If an invalid search type is provided.
   */
  public List<IEvent> search(List<IEvent> events, String eventName, Temporal startTime,
                             Temporal endTime, boolean isRecurring, SearchType searchType) {
    SearchEventsStrategy strategy = selectStrategy(searchType);
    return strategy.search(events, eventName, startTime, endTime, isRecurring);
  }

  /**
   * Selects the appropriate search strategy based on the provided search type.
   *
   * @param searchType The type of search to be performed.
   * @return The corresponding {@link SearchEventsStrategy} implementation.
   * @throws IllegalArgumentException If an unsupported search type is given.
   */
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
