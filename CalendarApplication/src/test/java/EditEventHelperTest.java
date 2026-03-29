import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.Constants;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.model.impl.EditEventHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for EditEventHelper.
 * This class contains tests that already cover getEditEventChanges as well as additional tests that
 * directly call hasValueChanged to achieve full mutation coverage.
 */
public class EditEventHelperTest {
  private EventsResponseDTO createRecurringEvent() {
    return EventsResponseDTO.builder()
        .eventName("Team Meeting")
        .startTime(LocalDateTime.parse("2025-04-10T10:00"))
        .endTime(LocalDateTime.parse("2025-04-10T11:00"))
        .location("Room 101")
        .description("Discuss project updates")
        .visibility("Public")
        .recurringDays("Mon,Wed,Fri")
        .occurrenceCount(5)
        .recurringEndDate(LocalDateTime.parse("2025-05-10T00:00"))
        .build();
  }

  private EventsResponseDTO createSingleEvent() {
    return EventsResponseDTO.builder()
        .eventName("One-Time Meeting")
        .startTime(LocalDateTime.parse("2025-04-10T10:00"))
        .endTime(LocalDateTime.parse("2025-04-10T11:00"))
        .location("Room 102")
        .description("Single instance meeting")
        .visibility("Public")
        // No recurring information provided
        .recurringDays("")
        .occurrenceCount(null)
        .recurringEndDate(null)
        .build();
  }

  //===========================================================================
  // Already existing tests for getEditEventChanges
  //===========================================================================

  @Test
  public void testNoChange() {
    EventsResponseDTO event = createRecurringEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.LOCATION, "Room 101");
    changes.put(Constants.PropertyKeys.NAME, "Team Meeting");

    Map<String, String> update = EditEventHelper.getEditEventChanges(event, changes);
    assertTrue("No update expected when values remain unchanged", update.isEmpty());
  }

  @Test
  public void testSinglePropertyChange() {
    EventsResponseDTO event = createRecurringEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.LOCATION, "Room 202");

    Map<String, String> update = EditEventHelper.getEditEventChanges(event, changes);
    assertEquals("Expected one property to be updated", 1, update.size());
    assertEquals("Room 202", update.get(Constants.PropertyKeys.LOCATION));
  }

  @Test(expected = InvalidCommandException.class)
  public void testEditingRecurrenceOnSingleEventOccurrenceCount() {
    EventsResponseDTO event = createSingleEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.OCCURRENCE_COUNT, "10");
    EditEventHelper.getEditEventChanges(event, changes);
  }

  @Test(expected = InvalidCommandException.class)
  public void testEditingRecurrenceOnSingleEventRecurrenceEndDate() {
    EventsResponseDTO event = createSingleEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.RECURRENCE_END_DATE, "2025-05-15T00:00");
    EditEventHelper.getEditEventChanges(event, changes);
  }

  @Test(expected = InvalidCommandException.class)
  public void testMultipleTimePropertiesUpdateThrowsException() {
    EventsResponseDTO event = createRecurringEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.NAME, "Updated Team Meeting");
    changes.put(Constants.PropertyKeys.START_TIME, "2025-04-10T12:00");
    EditEventHelper.getEditEventChanges(event, changes);
  }

  @Test(expected = InvalidCommandException.class)
  public void testMultipleTimePropertiesUpdateThrowsException1() {
    EventsResponseDTO event = createRecurringEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.NAME, "Updated Team Meeting");
    changes.put(Constants.PropertyKeys.END_TIME, "2025-04-10T12:00");
    EditEventHelper.getEditEventChanges(event, changes);
  }

  @Test(expected = InvalidCommandException.class)
  public void testMultipleTimePropertiesUpdateThrowsException2() {
    EventsResponseDTO event = createRecurringEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.START_TIME, "2025-04-10T12:00");
    changes.put(Constants.PropertyKeys.END_TIME, "2025-04-10T12:00");
    EditEventHelper.getEditEventChanges(event, changes);
  }

  @Test
  public void testMultipleAllowedPropertyUpdates() {
    EventsResponseDTO event = createRecurringEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.LOCATION, "Room 303");
    changes.put(Constants.PropertyKeys.DESCRIPTION, "Updated project discussion");

    Map<String, String> update = EditEventHelper.getEditEventChanges(event, changes);
    assertEquals("Room 303", update.get(Constants.PropertyKeys.LOCATION));
    assertEquals("Updated project discussion", update.get(Constants.PropertyKeys.DESCRIPTION));
  }

  @Test(expected = InvalidCommandException.class)
  public void testOccurrenceCountNullUpdateNotAllowed() {
    EventsResponseDTO event = createRecurringEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.OCCURRENCE_COUNT, null);
    EditEventHelper.getEditEventChanges(event, changes);
  }

  @Test(expected = InvalidCommandException.class)
  public void testRecurrenceEndDateNullUpdateNotAllowed() {
    EventsResponseDTO event = createRecurringEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.RECURRENCE_END_DATE, null);
    EditEventHelper.getEditEventChanges(event, changes);
  }

  @Test
  public void testRecurrencePropertiesUpdateAllowedWhenValueIsChanging() {
    EventsResponseDTO event = createRecurringEvent();
    Map<String, String> changes = new HashMap<>();
    changes.put(Constants.PropertyKeys.OCCURRENCE_COUNT, "7");
    changes.put(Constants.PropertyKeys.RECURRENCE_END_DATE, "2025-05-15T00:00");

    Map<String, String> update = EditEventHelper.getEditEventChanges(event, changes);
    assertEquals("7", update.get(Constants.PropertyKeys.OCCURRENCE_COUNT));
    assertEquals("2025-05-15T00:00", update.get(Constants.PropertyKeys.RECURRENCE_END_DATE));
  }

  @Test
  public void testHasValueChangedForName() {
    EventsResponseDTO event = createRecurringEvent();
    assertFalse(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.NAME, "Team "
        + "Meeting"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.NAME, "Updated Team "
        + "Meeting"));
  }

  @Test
  public void testHasValueChangedForStartTime() {
    EventsResponseDTO event = createRecurringEvent();
    assertFalse(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.START_TIME, "2025"
        + "-04-10T10:00"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.START_TIME, "2025-04"
        + "-10T10:30"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.START_TIME, null));

    EventsResponseDTO eventWithNullStart = EventsResponseDTO.builder()
        .eventName("Null Start")
        .startTime(null)
        .endTime(LocalDateTime.parse("2025-04-10T11:00"))
        .location("Room 101")
        .description("Test event")
        .visibility("Public")
        .recurringDays("Mon,Wed,Fri")
        .occurrenceCount(5)
        .recurringEndDate(LocalDateTime.parse("2025-05-10T00:00"))
        .build();
    assertFalse(EditEventHelper.hasValueChanged(eventWithNullStart,
        Constants.PropertyKeys.START_TIME, null));
    assertTrue(EditEventHelper.hasValueChanged(eventWithNullStart,
        Constants.PropertyKeys.START_TIME, "2025-04-10T10:00"));
  }

  @Test
  public void testHasValueChangedForEndTime() {
    EventsResponseDTO event = createRecurringEvent();
    assertFalse(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.END_TIME, "2025-04"
        + "-10T11:00"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.END_TIME, "2025-04"
        + "-10T11:30"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.END_TIME, null));

    EventsResponseDTO eventWithNullEnd = EventsResponseDTO.builder()
        .eventName("Null End")
        .startTime(LocalDateTime.parse("2025-04-10T10:00"))
        .endTime(null)
        .location("Room 102")
        .description("Test event")
        .visibility("Public")
        .recurringDays("Mon,Wed,Fri")
        .occurrenceCount(5)
        .recurringEndDate(LocalDateTime.parse("2025-05-10T00:00"))
        .build();
    assertFalse(EditEventHelper.hasValueChanged(eventWithNullEnd, Constants.PropertyKeys.END_TIME
        , null));
    assertTrue(EditEventHelper.hasValueChanged(eventWithNullEnd, Constants.PropertyKeys.END_TIME,
        "2025-04-10T11:00"));
  }

  @Test
  public void testHasValueChangedForLocation() {
    EventsResponseDTO event = createRecurringEvent();
    assertFalse(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.LOCATION, "Room "
        + "101"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.LOCATION, "Room 202"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.LOCATION, null));

    EventsResponseDTO eventWithNullLocation = EventsResponseDTO.builder()
        .eventName("No Location")
        .startTime(LocalDateTime.parse("2025-04-10T10:00"))
        .endTime(LocalDateTime.parse("2025-04-10T11:00"))
        .location(null)
        .description("Test event")
        .visibility("Public")
        .recurringDays("Mon,Wed,Fri")
        .occurrenceCount(5)
        .recurringEndDate(LocalDateTime.parse("2025-05-10T00:00"))
        .build();
    assertFalse(EditEventHelper.hasValueChanged(eventWithNullLocation,
        Constants.PropertyKeys.LOCATION, null));
    assertTrue(EditEventHelper.hasValueChanged(eventWithNullLocation,
        Constants.PropertyKeys.LOCATION, "Room 101"));
  }

  @Test
  public void testHasValueChangedForRecurringDays() {
    EventsResponseDTO event = createRecurringEvent();
    assertFalse(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.RECURRING_DAYS,
        "Mon,Wed,Fri"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.RECURRING_DAYS,
        "Tue,Thu"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.RECURRING_DAYS, null));

    EventsResponseDTO singleEvent = createSingleEvent();
    assertFalse(EditEventHelper.hasValueChanged(singleEvent,
        Constants.PropertyKeys.RECURRING_DAYS, ""));
    assertTrue(EditEventHelper.hasValueChanged(singleEvent, Constants.PropertyKeys.RECURRING_DAYS
        , "Mon,Wed,Fri"));
  }

  @Test
  public void testHasValueChangedForOccurrenceCount() {
    EventsResponseDTO event = createRecurringEvent();
    assertFalse(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.OCCURRENCE_COUNT,
        "5"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.OCCURRENCE_COUNT, "7"
    ));

    EventsResponseDTO singleEvent = createSingleEvent();
    assertFalse(EditEventHelper.hasValueChanged(singleEvent,
        Constants.PropertyKeys.OCCURRENCE_COUNT, null));
    assertTrue(EditEventHelper.hasValueChanged(singleEvent,
        Constants.PropertyKeys.OCCURRENCE_COUNT, "10"));
  }

  @Test
  public void testHasValueChangedForRecurrenceEndDate() {
    EventsResponseDTO event = createRecurringEvent();
    assertFalse(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.RECURRENCE_END_DATE
        , "2025-05-10T00:00"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.RECURRENCE_END_DATE,
        "2025-06-10T00:00"));

    EventsResponseDTO singleEvent = createSingleEvent();
    assertFalse(EditEventHelper.hasValueChanged(singleEvent,
        Constants.PropertyKeys.RECURRENCE_END_DATE, null));
    assertTrue(EditEventHelper.hasValueChanged(singleEvent,
        Constants.PropertyKeys.RECURRENCE_END_DATE, "2025-05-10T00:00"));
  }

  @Test
  public void testHasValueChangedForDescription() {
    EventsResponseDTO event = createRecurringEvent();
    assertFalse(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.DESCRIPTION,
        "Discuss project updates"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.DESCRIPTION, "New "
        + "description"));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.DESCRIPTION, null));

    EventsResponseDTO eventWithNullDesc = EventsResponseDTO.builder()
        .eventName("No Description")
        .startTime(LocalDateTime.parse("2025-04-10T10:00"))
        .endTime(LocalDateTime.parse("2025-04-10T11:00"))
        .location("Room 101")
        .description(null)
        .visibility("Public")
        .recurringDays("Mon,Wed,Fri")
        .occurrenceCount(5)
        .recurringEndDate(LocalDateTime.parse("2025-05-10T00:00"))
        .build();
    assertFalse(EditEventHelper.hasValueChanged(eventWithNullDesc,
        Constants.PropertyKeys.DESCRIPTION, null));
    assertTrue(EditEventHelper.hasValueChanged(eventWithNullDesc,
        Constants.PropertyKeys.DESCRIPTION, "Something"));
  }

  @Test
  public void testHasValueChangedForVisibility() {
    EventsResponseDTO event = createRecurringEvent();
    assertFalse(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.VISIBILITY, "Public"
    ));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.VISIBILITY, "Private"
    ));
    assertTrue(EditEventHelper.hasValueChanged(event, Constants.PropertyKeys.VISIBILITY, null));

    EventsResponseDTO eventWithNullVisibility = EventsResponseDTO.builder()
        .eventName("No Visibility")
        .startTime(LocalDateTime.parse("2025-04-10T10:00"))
        .endTime(LocalDateTime.parse("2025-04-10T11:00"))
        .location("Room 101")
        .description("Test")
        .visibility(null)
        .recurringDays("Mon,Wed,Fri")
        .occurrenceCount(5)
        .recurringEndDate(LocalDateTime.parse("2025-05-10T00:00"))
        .build();
    assertFalse(EditEventHelper.hasValueChanged(eventWithNullVisibility,
        Constants.PropertyKeys.VISIBILITY, null));
    assertTrue(EditEventHelper.hasValueChanged(eventWithNullVisibility,
        Constants.PropertyKeys.VISIBILITY, "Public"));
  }

  @Test
  public void testHasValueChangedWithUnknownProperty() {
    EventsResponseDTO event = createRecurringEvent();
    assertFalse(EditEventHelper.hasValueChanged(event, "unknown_property", "anyValue"));
  }
}
