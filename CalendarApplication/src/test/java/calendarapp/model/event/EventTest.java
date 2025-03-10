//package calendarapp.model.event;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//public class EventTest {
//  private Event event1;
//  private Event event2;
//
//  @Before
//  public void setUp() {
//    event1 = Event.builder()
//        .name("Single Event 1")
//        .startTimeAndEndTime(LocalDateTime.of(2025, 3, 10, 10, 0),
//            LocalDateTime.of(2025, 3, 10, 11, 0))
//        .build();
//
//    event2 = Event.builder()
//        .name("Single Event 2")
//        .startTimeAndEndTime(LocalDateTime.of(2025, 3, 10, 12, 0),
//            LocalDateTime.of(2025, 3, 10, 13, 0))
//        .build();
//  }
//
//  @Test
//  public void testEventCreationWithRequiredFields() {
//    assertNotNull(event1);
//    String expectedToString = "Event{name='Single Event 1', startTime=2025-03-10T10:00, endTime=2025-03-10T11:00, description='null', location='null', " +
//        "visibility=PUBLIC, recurringDays='null', occurrenceCount=null, recurrenceEndDate=null, isAutoDecline=true}";
//    assertEquals(expectedToString, event1.toString());
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testEventCreationWithoutNameThrowsException() {
//    Event.builder()
//        .startTimeAndEndTime(LocalDateTime.of(2025, 3, 10, 10, 0),
//            LocalDateTime.of(2025, 3, 10, 11, 0))
//        .build();
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testEventCreationWithoutStartTimeThrowsException() {
//    Event.builder()
//        .name("Single Event 1")
//        .startTimeAndEndTime(null, LocalDateTime.of(2025, 3, 10, 10, 0))
//        .build();
//  }
//
//  @Test
//  public void testEventCreationWithoutEndTime() {
//    Event eventWithoutEndTime = Event.builder()
//        .name("Single Event 1")
//        .startTimeAndEndTime(LocalDateTime.of(2025, 3, 10, 10, 0), null)
//        .build();
//    assertEquals(LocalDateTime.of(2025, 3, 10, 0, 0), eventWithoutEndTime.getStartDateTime());
//    assertEquals(LocalDateTime.of(2025, 3, 10, 23, 59, 59), eventWithoutEndTime.getEndDateTime().truncatedTo(ChronoUnit.SECONDS));
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testEventWithEndTimeBeforeStartTimeThrowsException() {
//    Event.builder()
//        .name("Single Event 1")
//        .startTimeAndEndTime(LocalDateTime.of(2025, 3, 10, 10, 0),
//            (LocalDateTime.of(2025, 3, 10, 9, 0)))
//        .build();
//  }
//
//  @Test
//  public void testConflictsWithNoConflict() {
//    assertFalse(event1.conflictsWith(event2));
//  }
//
//  @Test
//  public void testConflictsWithOverlappingEvents() {
//    Event overlappingEvent = Event.builder()
//        .name("Overlapping Meeting")
//        .startTimeAndEndTime(LocalDateTime.of(2025, 3, 10, 10, 30),
//            LocalDateTime.of(2025, 3, 10, 11, 30))
//        .build();
//
//    assertTrue(event1.conflictsWith(overlappingEvent));
//  }
//}