package calendarapp.model.impl.searchstrategies;

import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
   * Map to hold the different types of search.
   */
  private static final Map<SearchType, Supplier<SearchEventsStrategy>> STRATEGY_MAP;

  /**
   * Static block to initialize the strategy map.
   */
  static {
    STRATEGY_MAP = new HashMap<>();
    STRATEGY_MAP.put(SearchType.OVERLAPPING, OverlappingEventsSearch::new);
    STRATEGY_MAP.put(SearchType.MATCHING, InBetweenEventsSearch::new);
    STRATEGY_MAP.put(SearchType.EXACT, ExactMatchEventsSearch::new);
  }

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
    SearchEventsStrategy strategy = STRATEGY_MAP.getOrDefault(searchType, null).get();
    if (strategy == null) {
      throw new IllegalArgumentException("No strategy found for search type: " + searchType);
    }
    return strategy.search(events, eventName, startTime, endTime, isRecurring);
  }
}
