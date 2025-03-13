import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import calendarapp.model.IEvent;
import calendarapp.model.impl.Event;
import calendarapp.model.impl.EventConstants;
import calendarapp.utils.TimeUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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

    String expectedToString = "Name: Single Event 1 Start Time: 2025-03-10T10:00 "
        + "End Time: 2025-03-10T11:00 Description: Some description "
        + "Location: Some location Visibility: DEFAULT Recurring Days: M "
        + "Occurrence Count: 3 Recurrence End Date: null Auto Decline: false\n";
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

    String expectedToString = "Name: Single Event 1 Start Time: 2025-03-10T10:00 "
        + "End Time: 2025-03-10T11:00 Description: Some description "
        + "Location: Some location Visibility: DEFAULT Recurring Days: M "
        + "Occurrence Count: null Recurrence End Date: 2025-04-10T00:00 Auto Decline: false\n";
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

  @Test
  public void testEventCreationWithoutEndTime() {
    IEvent eventWithoutEndTime = Event.builder()
        .name("Event Without End Time")
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .endTime(null)
        .build();

    String expectedToString = "Name: Event Without End Time Start Time: 2025-03-10T00:00 "
        + "End Time: 2025-03-11T00:00 Description: null Location: null Visibility: DEFAULT "
        + "Recurring Days: null Occurrence Count: null Recurrence End Date: null Auto "
        + "Decline: true\n";
    assertEquals(expectedToString, eventWithoutEndTime.toString());
  }

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
      assertEquals("Event end time cannot be before start time", e.getMessage());
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
      assertEquals("Invalid recurring days format. Use M,T,W,R,F,S,U for days of week",
          e.getMessage());
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
  public void testRecurringEventSpanningMoreThanOneDayThrowsException() {
    try {
      Event.builder()
          .name("Multi-Day Recurring Event")
          .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
          .endTime(LocalDateTime.of(2025, 3, 11, 12, 0))
          .recurringDays("M")
          .occurrenceCount(5)
          .build();
    } catch (IllegalArgumentException e) {
      assertEquals("Recurring events cannot span more than one day", e.getMessage());
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

  @Test
  public void testEqualsAndHashCode() {
    Event event1 = Event.builder()
        .name("Event 1")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .description("Description")
        .location("Location")
        .build();

    Event event2 = Event.builder()
        .name("Event 1")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .description("Different description")
        .location("Different location")
        .build();

    Event event3 = Event.builder()
        .name("Event 2")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .build();

    Event event4 = Event.builder()
        .name("Event 1")
        .startTime(LocalDateTime.of(2025, 3, 15, 9, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .build();

    assertEquals(event1, event2);
    assertEquals(event2, event1);
    assertNotEquals(event1, event3);
    assertNotEquals(event1, event4);

    assertEquals(event1.hashCode(), event2.hashCode());
    assertNotEquals(event1.hashCode(), event3.hashCode());
    assertNotEquals(event1.hashCode(), event4.hashCode());

    assertNotEquals(null, event1);
    assertNotEquals("Not an event", event1);
  }

  @Test
  public void testUpdateName() {
    Event event = Event.builder()
        .name("Original Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .build();

    IEvent updatedEvent = event.updateProperty(EventConstants.PropertyKeys.NAME, "Updated Event");

    assertTrue(updatedEvent.toString().contains("Updated Event"));
  }

  @Test
  public void testUpdateStartTime() {
    Event event = Event.builder()
        .name("Test Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .build();

    LocalDateTime newStartTime = LocalDateTime.of(2025, 3, 15, 9, 0);
    IEvent updatedEvent = event.updateProperty(
        EventConstants.PropertyKeys.START_TIME,
        newStartTime.toString()
    );

    assertTrue(updatedEvent.toString().contains(newStartTime.toString()));
  }

  @Test
  public void testUpdateEndTime() {
    Event event = Event.builder()
        .name("Test Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .build();

    LocalDateTime newEndTime = LocalDateTime.of(2025, 3, 15, 12, 0);
    IEvent updatedEvent = event.updateProperty(
        EventConstants.PropertyKeys.END_TIME,
        newEndTime.toString()
    );

    assertTrue(updatedEvent.toString().contains(newEndTime.toString()));
  }

  @Test
  public void testUpdateDescription() {
    Event event = Event.builder()
        .name("Test Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .description("Original description")
        .build();

    IEvent updatedEvent = event.updateProperty(
        EventConstants.PropertyKeys.DESCRIPTION,
        "Updated description"
    );

    assertTrue(updatedEvent.toString().contains("Updated description"));
  }

  @Test
  public void testUpdateLocation() {
    Event event = Event.builder()
        .name("Test Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .location("Original location")
        .build();

    IEvent updatedEvent = event.updateProperty(
        EventConstants.PropertyKeys.LOCATION,
        "Updated location"
    );

    assertTrue(updatedEvent.toString().contains("Updated location"));
  }

  @Test
  public void testUpdateVisibility() {
    Event event = Event.builder()
        .name("Test Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .build();

    IEvent updatedEvent = event.updateProperty(
        EventConstants.PropertyKeys.VISIBILITY,
        "PRIVATE"
    );

    assertTrue(updatedEvent.toString().contains("PRIVATE"));
  }
}