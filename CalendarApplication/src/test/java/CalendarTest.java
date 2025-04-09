import org.junit.Assert;
import org.junit.Test;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.impl.Calendar;
import calendarapp.model.impl.EventRepository;

/**
 * Test class for {@link Calendar} class.
 */
public class CalendarTest {
  @Test(expected = InvalidCommandException.class)
  public void CalendarNameNull() {
    try {
      Calendar.builder().zoneId("Europe/Berlin").eventRepository(new EventRepository()).build();
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar name cannot be null or empty.\n", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EventRepositoryNull() {
    try {
      Calendar.builder().name("New Cal").zoneId("Europe/Berlin").build();
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Event repository cannot be null.\n", e.getMessage());
      throw e;
    }
  }
}