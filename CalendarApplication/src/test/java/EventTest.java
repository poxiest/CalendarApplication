import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import calendarapp.model.impl.Constants;
import calendarapp.model.impl.Event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link Event}.
 */
public class EventTest {
  private Event event1;

  @Before
  public void setUp() {
    event1 = Event.builder()
        .name("Single Event 1")
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
        .build();
  }

  @Test
  public void testEventCreationWithRequiredFields() {
    assertNotNull(event1);
    assertEquals("Single Event 1", event1.getName());
    assertEquals(LocalDateTime.parse("2025-03-10T10:00"), event1.getStartTime());
    assertEquals(LocalDateTime.parse("2025-03-10T11:00"), event1.getEndTime());
    assertNull(event1.getDescription());
    assertNull(event1.getLocation());
    assertEquals("default", event1.getVisibility().getValue());
    assertNull(event1.getRecurringDays());
    assertNull(event1.getOccurrenceCount());
    assertNull(event1.getRecurrenceEndDate());
  }

  @Test
  public void testEventCreationWithOptionalFieldsWithOccurrenceCount() {
    Event eventWithOptionalField = Event.builder()
        .name("Single Event 1")
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
        .description("Some description")
        .location("Some location")
        .recurringDays("M")
        .occurrenceCount(3)
        .isAutoDecline(false)
        .build();

    assertNotNull(eventWithOptionalField);
    assertEquals("Single Event 1", eventWithOptionalField.getName());
    assertEquals(LocalDateTime.parse("2025-03-10T10:00"), eventWithOptionalField.getStartTime());
    assertEquals(LocalDateTime.parse("2025-03-10T11:00"), eventWithOptionalField.getEndTime());
    assertEquals("Some description", eventWithOptionalField.getDescription());
    assertEquals("Some location", eventWithOptionalField.getLocation());
    assertEquals("default", eventWithOptionalField.getVisibility().getValue());
    assertEquals("M", eventWithOptionalField.getRecurringDays());
    assertEquals(Integer.valueOf(3), eventWithOptionalField.getOccurrenceCount());
    assertNull(eventWithOptionalField.getRecurrenceEndDate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEVentWithWrongVisibility() {
    try {
      Event eventWithOptionalField = Event.builder()
          .name("Single Event 1")
          .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
          .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
          .visibility("Wrong")
          .build();
    } catch (IllegalArgumentException e) {
      assertEquals("Unknown Visibility input\n", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testOcurrenceWithZero() {
    try {
      Event eventWithOptionalField = Event.builder()
          .name("Single Event 1")
          .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
          .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
          .recurringDays("MRW")
          .occurrenceCount(0)
          .build();
    } catch (IllegalArgumentException e) {
      assertEquals("Occurrence count must be greater than 0", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void updateWrongProperty() {
    try {
      Event eventWithOptionalField = Event.builder()
          .name("Single Event 1")
          .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
          .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
          .build();
      eventWithOptionalField.updateProperty("wrong", "value");
    } catch (IllegalArgumentException e) {
      assertEquals("Cannot edit property: wrong\n", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testEventCreationWithOptionalFieldsWithRecurrenceEndDate() {
    Event eventWithOptionalField = Event.builder()
        .name("Single Event 1")
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 11, 0))
        .description("Some description")
        .location("Some location")
        .recurringDays("M")
        .recurrenceEndDate(LocalDateTime.of(2025, 4, 10, 0, 0))
        .isAutoDecline(false)
        .build();

    assertNotNull(eventWithOptionalField);
    assertEquals("Single Event 1", eventWithOptionalField.getName());
    assertEquals(LocalDateTime.parse("2025-03-10T10:00"), eventWithOptionalField.getStartTime());
    assertEquals(LocalDateTime.parse("2025-03-10T11:00"), eventWithOptionalField.getEndTime());
    assertEquals("Some description", eventWithOptionalField.getDescription());
    assertEquals("Some location", eventWithOptionalField.getLocation());
    assertEquals("default", eventWithOptionalField.getVisibility().getValue());
    assertEquals("M", eventWithOptionalField.getRecurringDays());
    assertNull(eventWithOptionalField.getOccurrenceCount());
    assertEquals(LocalDateTime.parse("2025-04-10T00:00"),
        eventWithOptionalField.getRecurrenceEndDate());
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
    Event eventWithoutEndTime = Event.builder()
        .name("Event Without End Time")
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .endTime(null)
        .build();

    assertNotNull(eventWithoutEndTime);
    assertEquals("Event Without End Time", eventWithoutEndTime.getName());
    assertEquals(LocalDateTime.parse("2025-03-10T00:00"), eventWithoutEndTime.getStartTime());
    assertEquals(LocalDateTime.parse("2025-03-11T00:00"), eventWithoutEndTime.getEndTime());
    assertEquals("default", eventWithoutEndTime.getVisibility().getValue());
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
  public void testUpdateName() {
    Event event = Event.builder()
        .name("Original Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .build();

    Event updatedEvent = event.updateProperty(Constants.PropertyKeys.NAME, "Updated Event");

    assertEquals("Updated Event", updatedEvent.getName());
  }

  @Test
  public void testUpdateStartTime() {
    Event event = Event.builder()
        .name("Test Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .build();

    LocalDateTime newStartTime = LocalDateTime.of(2025, 3, 15, 9, 0);
    Event updatedEvent = event.updateProperty(
        Constants.PropertyKeys.START_TIME,
        newStartTime.toString()
    );

    assertEquals(newStartTime, updatedEvent.getStartTime());
  }

  @Test
  public void testUpdateEndTime() {
    Event event = Event.builder()
        .name("Test Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .build();

    LocalDateTime newEndTime = LocalDateTime.of(2025, 3, 15, 12, 0);
    Event updatedEvent = event.updateProperty(
        Constants.PropertyKeys.END_TIME,
        newEndTime.toString()
    );

    assertEquals(newEndTime, updatedEvent.getEndTime());
  }

  @Test
  public void testUpdateDescription() {
    Event event = Event.builder()
        .name("Test Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .description("Original description")
        .build();

    Event updatedEvent = event.updateProperty(
        Constants.PropertyKeys.DESCRIPTION,
        "Updated description"
    );

    assertEquals("Updated description", updatedEvent.getDescription());
  }

  @Test
  public void testUpdateLocation() {
    Event event = Event.builder()
        .name("Test Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .location("Original location")
        .build();

    Event updatedEvent = event.updateProperty(
        Constants.PropertyKeys.LOCATION,
        "Updated location"
    );

    assertEquals("Updated location", updatedEvent.getLocation());
  }

  @Test
  public void testUpdateVisibility() {
    Event event = Event.builder()
        .name("Test Event")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .build();

    Event updatedEvent = event.updateProperty(
        Constants.PropertyKeys.VISIBILITY,
        "PRIVATE"
    );

    assertEquals("private", updatedEvent.getVisibility().getValue());
  }

  @Test
  public void testEquals_reflexive() {
    Event event = Event.builder()
        .name("Sample Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Conference Room")
        .description("Annual Meeting")
        .visibility("Public")
        .recurringDays("MRW")
        .recurrenceEndDate(LocalDateTime.of(2025, 12, 31, 0, 0))
        .isAutoDecline(true).build();

    // Reflexive: An object must equal itself
    assertTrue(event.equals(event));
    assertEquals(event.hashCode(), event.hashCode());
  }

  @Test
  public void testEquals_symmetric() {
    Event event1 = Event.builder()
        .name("Sample Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Conference Room")
        .description("Annual Meeting")
        .visibility("Public")
        .recurringDays("MRW")
        .occurrenceCount(5)
        .isAutoDecline(true).build();

    Event event2 = Event.builder()
        .name("Sample Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Conference Room")
        .description("Annual Meeting")
        .visibility("Public")
        .recurringDays("MRW")
        .occurrenceCount(5)
        .isAutoDecline(true).build();

    // Symmetric: If event1 equals event2, then event2 should equal event1
    assertTrue(event1.equals(event2));
    assertTrue(event2.equals(event1));

    assertEquals(event1.hashCode(), event2.hashCode());
    assertEquals(event2.hashCode(), event1.hashCode());
  }

  @Test
  public void testEquals_transitive() {
    Event event1 = Event.builder()
        .name("Sample Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Conference Room")
        .description("Annual Meeting")
        .visibility("Public")
        .recurringDays("MRW")
        .occurrenceCount(5)
        .isAutoDecline(true).build();

    Event event2 = Event.builder()
        .name("Sample Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Conference Room")
        .description("Annual Meeting")
        .visibility("Public")
        .recurringDays("MRW")
        .occurrenceCount(5)
        .isAutoDecline(true).build();

    Event event3 = Event.builder()
        .name("Sample Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Conference Room")
        .description("Annual Meeting")
        .visibility("Public")
        .recurringDays("MRW")
        .occurrenceCount(5)
        .isAutoDecline(true).build();

    // Transitive: If event1 equals event2 and event2 equals event3, then event1 should equal event3
    assertTrue(event1.equals(event2));
    assertTrue(event2.equals(event3));
    assertTrue(event1.equals(event3));

    assertEquals(event1.hashCode(), event2.hashCode());
    assertEquals(event2.hashCode(), event3.hashCode());
    assertEquals(event1.hashCode(), event3.hashCode());
  }

  @Test
  public void testEquals_consistent() {
    Event event1 = Event.builder()
        .name("Sample Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Conference Room")
        .description("Annual Meeting")
        .visibility("Public")
        .recurringDays("MRW")
        .recurrenceEndDate(LocalDateTime.of(2025, 12, 31, 0, 0))
        .isAutoDecline(true).build();

    Event event2 = Event.builder()
        .name("Sample Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Conference Room")
        .description("Annual Meeting")
        .visibility("Public")
        .recurringDays("MRW")
        .recurrenceEndDate(LocalDateTime.of(2025, 12, 31, 0, 0))
        .isAutoDecline(true).build();

    // Consistent: Equals should consistently return the same result unless a property is changed
    assertTrue(event1.equals(event2));
    assertTrue(event1.equals(event2));

    assertEquals(event1.hashCode(), event2.hashCode());
    assertEquals(event2.hashCode(), event1.hashCode());
  }

  @Test
  public void testEquals_differentClass() {
    Event event = Event.builder()
        .name("Sample Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Conference Room").build();

    String nonEventObject = "Non-event object";

    // Different class: Should return false
    assertFalse(event.equals(nonEventObject));
    assertNotEquals(event.hashCode(), nonEventObject.hashCode());
  }

  @Test
  public void testEquals_differentValues() {
    Event event1 = Event.builder()
        .name("Sample Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Conference Room").build();

    Event event2 = Event.builder()
        .name("Different Event")
        .startTime(LocalDateTime.of(2025, 3, 13, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 13, 12, 0))
        .location("Meeting Room").build();

    // Different property values: Should return false
    assertFalse(event1.equals(event2));
    assertNotEquals(event1.hashCode(), event2.hashCode());
  }
}