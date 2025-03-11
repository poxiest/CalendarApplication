package calendarapp.model.event;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import calendarapp.model.EventVisibility;
import calendarapp.model.IEvent;
import calendarapp.model.impl.Event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EventTest {
  private IEvent event1;
  private IEvent event2;

  @Before
  public void setUp() {
    event1 = Event.builder()
        .name("Single Event 1")
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
        .build();

    event2 = Event.builder()
        .name("Single Event 2")
        .startTime(LocalDateTime.of(2025, 3, 10, 12, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 13, 0))
        .build();
  }

  @Test
  public void testEventCreationWithRequiredFields() {
    assertNotNull(event1);
    String expectedToString = "Name: Single Event 1 Start Time: 2025-03-10T10:00 " +
        "End Time: 2025-03-10T11:00 Description: null " +
        "Location: null Visibility: DEFAULT Recurring Days: null " +
        "Occurrence Count: null Recurrence End Date: null Auto Decline: true\n";
    assertEquals(expectedToString, event1.toString());
  }

  @Test
  public void testEventCreationWithOptionalFieldsWithOccurrenceCount() {
    IEvent eventWithOptionalField = Event.builder()
        .name("Single Event 1")
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
        .description("Some description")
        .location("Some location")
        .recurringDays("M")
        .occurrenceCount(3)
        .isAutoDecline(false)
        .build();

    String expectedToString = "Name: Single Event 1 Start Time: 2025-03-10T10:00 " +
        "End Time: 2025-03-10T11:00 Description: Some description " +
        "Location: Some location Visibility: DEFAULT Recurring Days: M " +
        "Occurrence Count: 3 Recurrence End Date: null Auto Decline: false\n";
    assertEquals(expectedToString, eventWithOptionalField.toString());
  }

  @Test
  public void testEventCreationWithOptionalFieldsWithRecurrenceEndDate() {
    IEvent eventWithOptionalField = Event.builder()
        .name("Single Event 1")
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
        .description("Some description")
        .location("Some location")
        .recurringDays("M")
        .recurrenceEndDate(LocalDateTime.of(2025, 4, 10, 0, 0))
        .isAutoDecline(false)
        .build();

    String expectedToString = "Name: Single Event 1 Start Time: 2025-03-10T10:00 " +
        "End Time: 2025-03-10T11:00 Description: Some description " +
        "Location: Some location Visibility: DEFAULT Recurring Days: M " +
        "Occurrence Count: null Recurrence End Date: 2025-04-10T00:00 Auto Decline: false\n";
    assertEquals(expectedToString, eventWithOptionalField.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventCreationWithoutNameThrowsException() {
    Event.builder()
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void testEventCreationWithoutStartTimeThrowsException() {
    Event.builder()
        .name("Single Event 1")
        .endTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .build();
  }

//  @Test
//  public void testEventCreationWithoutEndTime() {
//    IEvent eventWithoutEndTime = Event.builder()
//        .name("Event Without End Time")
//        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
//        .endTime(null)
//        .build();
//    assertEquals(LocalDateTime.of(2025, 3, 10, 0, 0, 0), eventWithoutEndTime.getStartDateTime());
//    assertEquals(LocalDateTime.of(2025, 3, 10, 23, 59, 59), eventWithoutEndTime.getEndDateTime());
//  }

  @Test
  public void testEventCreationWithoutEventNameThrowsException() {
    try {
      Event.builder()
          .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
          .build();
    } catch (IllegalArgumentException e) {
      assertEquals("Event name cannot be empty", e.getMessage());
    }
  }

  @Test
  public void testEventWithEndTimeBeforeStartTimeThrowsException() {
    try {
      Event.builder()
          .name("Invalid Event End Time before Start Time")
          .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
          .endTime(LocalDateTime.of(2025, 3, 10, 9, 0))
          .build();
    } catch (IllegalArgumentException e) {
      assertEquals("Event start time cannot be before end time", e.getMessage());
    }
  }

  @Test
  public void testEventInvalidRecurringDaysThrowsException() {
    try {
      Event.builder()
          .name("Invalid Recurring Days")
          .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
          .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
          .recurringDays("K")
          .build();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid recurring days format. Use M,T,W,R,F,S,U for days of week", e.getMessage());
    }
  }

  @Test
  public void testEventInvalidOccurrenceCountThrowsException() {
    try {
      Event.builder()
          .name("Invalid Occurrence Count")
          .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
          .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
          .recurringDays("M")
          .occurrenceCount(-1)
          .build();
    } catch (IllegalArgumentException e) {
      assertEquals("Occurrence count must be greater than 0", e.getMessage());
    }
  }

  @Test
  public void testEventInvalidRecurrenceEndDateThrowsException() {
    try {
      Event.builder()
          .name("Invalid Recurrence End Time before End Time")
          .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
          .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
          .recurringDays("M")
          .recurrenceEndDate(LocalDateTime.of(2025, 2, 10, 0, 0))
          .build();
    } catch (IllegalArgumentException e) {
      assertEquals("Recurrence end date must be after end date", e.getMessage());
    }
  }

  @Test
  public void testEventInvalidRecurringEventWithoutDaysThrowsException() {
    try {
      Event.builder()
          .name("Invalid Recurrence End Time before Start Time")
          .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
          .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
          .recurrenceEndDate(LocalDateTime.of(2025, 4, 10, 0, 0))
          .build();
    } catch (IllegalArgumentException e) {
      assertEquals("Recurring events require recurring days of week", e.getMessage());
    }
  }

//  @Test
//  public void testEventDefaultValue() {
//    assertEquals(EventVisibility.DEFAULT, event1.getVisibility());
//    assertTrue(event1.isAutoDecline());
//  }

  @Test
  public void testConflictsWithNoConflict() {
    assertFalse(event1.conflictsWith(event2));
  }

  @Test
  public void testConflictsWithOverlappingEvents() {
    IEvent overlappingEvent = Event.builder()
        .name("Overlapping Meeting")
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 30))
        .endTime(LocalDateTime.of(2025, 3, 10, 11, 30))
        .build();

    assertTrue(event1.conflictsWith(overlappingEvent));
  }
}