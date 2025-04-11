import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventVisibility;
import calendarapp.model.IEvent;
import calendarapp.model.SearchType;
import calendarapp.model.impl.Event;
import calendarapp.model.impl.searchstrategies.ExactMatchEventsSearch;
import calendarapp.model.impl.searchstrategies.SearchEventFactory;

import static org.junit.Assert.assertEquals;

/**
 * Test class to Search strategy events.
 */
public class SearchStrategyTestCases {
  private List<IEvent> events;
  private SearchEventFactory searchStrategy;
  private ExactMatchEventsSearch exactStrategy = new ExactMatchEventsSearch();

  @Before
  public void setUp() {
    searchStrategy = new SearchEventFactory();
    events = new ArrayList<>();
    events.add(Event.builder()
        .name("Team Meeting")
        .startTime(LocalDateTime.of(2025, 4, 10, 9, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 10, 0))
        .description("Weekly team meeting to discuss project updates")
        .location("Conference Room A")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());
    events.add(Event.builder()
        .name("Doctor's Appointment")
        .startTime(LocalDateTime.of(2025, 4, 12, 14, 30))
        .endTime(LocalDateTime.of(2025, 4, 12, 15, 0))
        .description("Routine check-up with Dr. Smith")
        .location("Health Clinic")
        .visibility(String.valueOf(EventVisibility.PRIVATE))
        .build());
    events.add(Event.builder()
        .name("Yoga Class")
        .startTime(LocalDateTime.of(2025, 4, 14, 18, 0))
        .endTime(LocalDateTime.of(2025, 4, 14, 19, 0))
        .description("Evening relaxation yoga session")
        .location("Community Center")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .recurringDays("MWF")
        .occurrenceCount(10)
        .build());
    events.add(Event.builder()
        .name("Team Meeting")
        .startTime(LocalDateTime.of(2025, 4, 11, 9, 0))
        .endTime(LocalDateTime.of(2025, 4, 11, 10, 0))
        .location("Online")
        .visibility(String.valueOf(EventVisibility.DEFAULT))
        .build());
  }

  @Test
  public void testSearchByExactName() {
    List<IEvent> result = searchStrategy.search(events, "Team Meeting", null, null, false,
        SearchType.EXACT);
    assertEquals(2, result.size());
    assertEquals("Team Meeting", result.get(0).getName());
    assertEquals("Team Meeting", result.get(1).getName());
  }

  @Test(expected = InvalidCommandException.class)
  public void testSearchByExactName1() {
    try {
      List<IEvent> result = searchStrategy.search(events, "Team Meeting", LocalDateTime.of(2025,
              4, 11, 10, 0),
          LocalDateTime.of(2025, 3, 11, 10, 0), false,
          SearchType.EXACT);
    } catch (InvalidCommandException e) {
      assertEquals("Start time must be before end time.", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testSearchByExactName2() {
    List<IEvent> result = searchStrategy.search(events, null, null, null, false,
        SearchType.EXACT);
    assertEquals(4, result.size());
    assertEquals("Team Meeting", result.get(0).getName());
    assertEquals("Team Meeting", result.get(1).getName());
    assertEquals("Doctor's Appointment", result.get(2).getName());
    assertEquals("Yoga Class", result.get(3).getName());
  }

  @Test
  public void testSearchByExactName3() {
    List<IEvent> result = searchStrategy.search(events, "Team Meeting", LocalDateTime.of(2025, 4,
            10, 9, 0), null, false,
        SearchType.EXACT);
    assertEquals(1, result.size());
    assertEquals("Team Meeting", result.get(0).getName());
  }

  @Test
  public void testSearchByExactStartTime() {
    List<IEvent> result = searchStrategy.search(events, null, LocalDateTime.of(2025, 4, 10, 9, 0)
        , null, false, SearchType.EXACT);
    assertEquals(1, result.size());
    assertEquals("Team Meeting", result.get(0).getName());
  }

  @Test
  public void testSearchByExactEndTime() {
    List<IEvent> result = searchStrategy.search(events, null, null, LocalDateTime.of(2025, 4, 12,
        15, 0), false, SearchType.EXACT);
    assertEquals(1, result.size());
    assertEquals("Doctor's Appointment", result.get(0).getName());
  }

  @Test
  public void testSearchByExactNameAndStartTime() {
    List<IEvent> result = searchStrategy.search(events, "Yoga Class", LocalDateTime.of(2025, 4,
        14, 18, 0), null, false, SearchType.EXACT);
    assertEquals(1, result.size());
    assertEquals("Yoga Class", result.get(0).getName());
  }

  @Test
  public void testSearchByExactNameStartTimeAndEndTime() {
    List<IEvent> result = searchStrategy.search(events, "Team Meeting", LocalDateTime.of(2025, 4,
        10, 9, 0), LocalDateTime.of(2025, 4, 10, 10, 0), false, SearchType.EXACT);
    assertEquals(1, result.size());
    assertEquals("Team Meeting", result.get(0).getName());
  }

  @Test
  public void testSearchNoMatch() {
    List<IEvent> result = searchStrategy.search(events, "Nonexistent Event", null, null, false,
        SearchType.EXACT);
    assertEquals(0, result.size());
  }

  @Test
  public void testSearchWithSorting() {
    List<IEvent> result = searchStrategy.search(events, "Team Meeting", null, null, false,
        SearchType.EXACT);
    assertEquals(2, result.size());
    assertEquals(LocalDateTime.of(2025, 4, 10, 9, 0), result.get(0).getStartTime());
    assertEquals(LocalDateTime.of(2025, 4, 11, 9, 0), result.get(1).getStartTime());
  }

  @Test
  public void testAllEvents() {
    List<IEvent> result = searchStrategy.search(events, null, null, null, false,
        SearchType.EXACT);
    assertEquals(4, result.size());
    assertEquals(LocalDateTime.of(2025, 4, 10, 9, 0), result.get(0).getStartTime());
    assertEquals(LocalDateTime.of(2025, 4, 11, 9, 0), result.get(1).getStartTime());
    assertEquals(LocalDateTime.of(2025, 4, 12, 14, 30), result.get(2).getStartTime());
    assertEquals(LocalDateTime.of(2025, 4, 14, 18, 0), result.get(3).getStartTime());
  }

  @Test
  public void testSearchByTimeRange() {
    List<IEvent> results = searchStrategy.search(events,
        null,
        LocalDateTime.of(2025, 4, 10, 0, 0),
        LocalDateTime.of(2025, 4, 12, 23, 59),
        false, SearchType.BETWEEN);

    assertEquals(3, results.size());
    assertEquals("Team Meeting", results.get(0).getName());
    assertEquals("Team Meeting", results.get(1).getName());
    assertEquals("Doctor's Appointment", results.get(2).getName());
  }

  @Test
  public void testSearchByEventName() {
    List<IEvent> results = searchStrategy.search(events,
        "Team Meeting",
        null,
        null,
        false, SearchType.BETWEEN);

    assertEquals(2, results.size());
    assertEquals(LocalDateTime.of(2025, 4, 11, 9, 0), results.get(1).getStartTime());
    assertEquals(LocalDateTime.of(2025, 4, 10, 9, 0), results.get(0).getStartTime());
  }

  @Test
  public void testSearchWithRecurringFilter() {
    List<IEvent> results = searchStrategy.search(events,
        null,
        null,
        null,
        true, SearchType.BETWEEN);

    assertEquals(1, results.size());
    assertEquals("Yoga Class", results.get(0).getName());
  }

  @Test
  public void testSearchWithNoResults() {
    List<IEvent> results = searchStrategy.search(events,
        null,
        LocalDateTime.of(2025, 5, 1, 0, 0),
        LocalDateTime.of(2025, 5, 2, 0, 0),
        false, SearchType.BETWEEN);

    assertEquals(0, results.size());
  }

  @Test(expected = InvalidCommandException.class)
  public void testSearchByOverlappingName1() {
    try {
      List<IEvent> result = searchStrategy.search(events, "Team Meeting", LocalDateTime.of(2025,
              4, 11, 10, 0),
          LocalDateTime.of(2025, 3, 11, 10, 0), false,
          SearchType.OVERLAPPING);
    } catch (InvalidCommandException e) {
      assertEquals("Start time must be before end time.", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testSearchByOverlappingName2() {
    List<IEvent> result = searchStrategy.search(events, null, null, null, false,
        SearchType.OVERLAPPING);
    assertEquals(4, result.size());
    assertEquals("Team Meeting", result.get(0).getName());
    assertEquals("Team Meeting", result.get(1).getName());
    assertEquals("Doctor's Appointment", result.get(2).getName());
    assertEquals("Yoga Class", result.get(3).getName());
  }

  @Test
  public void testSearchByOverlappingName3() {
    List<IEvent> result = searchStrategy.search(events, "Team Meeting", null, null, false,
        SearchType.OVERLAPPING);
    assertEquals(2, result.size());
    assertEquals("Team Meeting", result.get(0).getName());
    assertEquals("Team Meeting", result.get(1).getName());
  }

  @Test(expected = InvalidCommandException.class)
  public void testInvalidTimeRangeThrowsException() {
    List<IEvent> events = new ArrayList<>();
    LocalDateTime startTime = LocalDateTime.of(2025, 4, 10, 12, 0);
    LocalDateTime endTime = LocalDateTime.of(2025, 4, 10, 10, 0);  // Earlier than startTime
    exactStrategy.search(events, null, startTime, endTime, false);
  }

  @Test
  public void testSearchByName() {
    List<IEvent> events = new ArrayList<>();
    events.add(Event.builder()
        .name("Team Meeting")
        .startTime(LocalDateTime.of(2025, 4, 10, 9, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 10, 0))
        .description("Weekly team meeting to discuss project updates")
        .location("Conference Room A")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());
    events.add(Event.builder()
        .name("Conference")
        .startTime(LocalDateTime.of(2025, 4, 10, 11, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 12, 0))
        .description("Annual Conference")
        .location("Main Hall")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());
    events.add(Event.builder()
        .name("Team Meeting")
        .startTime(LocalDateTime.of(2025, 4, 10, 13, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 14, 0))
        .description("Weekly team meeting to discuss project updates")
        .location("Conference Room A")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());

    List<IEvent> result = exactStrategy.search(events, "Team Meeting", null, null, false);
    assertEquals("Expected two events with matching name", 2, result.size());
    assertEquals(LocalDateTime.of(2025, 4, 10, 9, 0), result.get(0).getStartTime());
    assertEquals(LocalDateTime.of(2025, 4, 10, 13, 0), result.get(1).getStartTime());
  }

  @Test
  public void testSearchByStartTime() {
    List<IEvent> events = new ArrayList<>();
    events.add(Event.builder()
        .name("Meeting")
        .startTime(LocalDateTime.of(2025, 4, 10, 8, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 9, 0))
        .description("Early meeting")
        .location("Room 1")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());
    events.add(Event.builder()
        .name("Meeting")
        .startTime(LocalDateTime.of(2025, 4, 10, 10, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 11, 0))
        .description("Mid meeting")
        .location("Room 2")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());
    events.add(Event.builder()
        .name("Meeting")
        .startTime(LocalDateTime.of(2025, 4, 10, 12, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 13, 0))
        .description("Late meeting")
        .location("Room 3")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());

    LocalDateTime searchStartTime = LocalDateTime.of(2025, 4, 10, 10, 0);
    List<IEvent> result = exactStrategy.search(events, null, searchStartTime, null, false);
    assertEquals("Expected one event matching start time", 1, result.size());
    assertEquals(searchStartTime, result.get(0).getStartTime());
  }

  @Test
  public void testSearchByEndTime() {
    List<IEvent> events = new ArrayList<>();
    events.add(Event.builder()
        .name("Workshop")
        .startTime(LocalDateTime.of(2025, 4, 10, 9, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 10, 0))
        .description("Morning workshop")
        .location("Lab A")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());
    events.add(Event.builder()
        .name("Workshop")
        .startTime(LocalDateTime.of(2025, 4, 10, 11, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 12, 0))
        .description("Noon workshop")
        .location("Lab B")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());
    events.add(Event.builder()
        .name("Workshop")
        .startTime(LocalDateTime.of(2025, 4, 10, 13, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 14, 0))
        .description("Afternoon workshop")
        .location("Lab C")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());

    LocalDateTime searchEndTime = LocalDateTime.of(2025, 4, 10, 12, 0);
    List<IEvent> result = exactStrategy.search(events, null, null, searchEndTime, false);
    assertEquals("Expected one event matching end time", 1, result.size());
    assertEquals(searchEndTime, result.get(0).getEndTime());
  }

  @Test
  public void testCombinedSearchParameters() {
    List<IEvent> events = new ArrayList<>();
    events.add(Event.builder()
        .name("Seminar")
        .startTime(LocalDateTime.of(2025, 4, 10, 10, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 11, 0))
        .description("Technical seminar")
        .location("Hall 1")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());
    events.add(Event.builder()
        .name("Seminar")
        .startTime(LocalDateTime.of(2025, 4, 10, 11, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 12, 0))
        .description("Technical seminar continuation")
        .location("Hall 2")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());
    events.add(Event.builder()
        .name("Lecture")
        .startTime(LocalDateTime.of(2025, 4, 10, 11, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 12, 0))
        .description("Guest lecture")
        .location("Hall 3")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build());

    LocalDateTime searchStartTime = LocalDateTime.of(2025, 4, 10, 11, 0);
    LocalDateTime searchEndTime = LocalDateTime.of(2025, 4, 10, 12, 0);
    List<IEvent> result = exactStrategy.search(events, "Seminar", searchStartTime, searchEndTime,
        false);
    assertEquals("Expected one event matching the combined criteria", 1, result.size());
    IEvent matchingEvent = result.get(0);
    assertEquals("Seminar", matchingEvent.getName());
    assertEquals(searchStartTime, matchingEvent.getStartTime());
    assertEquals(searchEndTime, matchingEvent.getEndTime());
  }

  @Test
  public void testNullParametersReturnAllSorted() {
    List<IEvent> events = new ArrayList<>();
    IEvent event1 = Event.builder()
        .name("Event1")
        .startTime(LocalDateTime.of(2025, 4, 10, 8, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 9, 0))
        .description("Morning event")
        .location("Room 101")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build();
    IEvent event2 = Event.builder()
        .name("Event2")
        .startTime(LocalDateTime.of(2025, 4, 10, 13, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 14, 0))
        .description("Afternoon event")
        .location("Room 202")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build();
    IEvent event3 = Event.builder()
        .name("Event3")
        .startTime(LocalDateTime.of(2025, 4, 10, 10, 0))
        .endTime(LocalDateTime.of(2025, 4, 10, 11, 0))
        .description("Mid-morning event")
        .location("Room 303")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build();
    events.add(event1);
    events.add(event2);
    events.add(event3);

    List<IEvent> result = exactStrategy.search(events, null, null, null, false);
    assertEquals("Expected all events to be returned", 3, result.size());
    assertEquals(LocalDateTime.of(2025, 4, 10, 8, 0), result.get(0).getStartTime());
    assertEquals(LocalDateTime.of(2025, 4, 10, 10, 0), result.get(1).getStartTime());
    assertEquals(LocalDateTime.of(2025, 4, 10, 13, 0), result.get(2).getStartTime());
  }

  @Test(expected = NullPointerException.class)
  public void invalid() {
    searchStrategy.search(events, null, null, null, false, null);
  }
}
