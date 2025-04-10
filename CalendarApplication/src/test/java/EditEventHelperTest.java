import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.Constants;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.model.impl.EditEventHelper;

/**
 * Test class for edit event helper.
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
}
