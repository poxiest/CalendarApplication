package model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.Test;

import calendar.ConflictException;
import events.IEvent;

/**
 * A JUnit test class for evaluating the operations
 * in the multiple calendar model class.
 */
public class MultipleCalendarModelTest {

  @Test
  public void testDefaultCalendarConstruction() {
    IMultipleCalendarModel model = new MultipleCalendarModel();
    IZonedCalendarModel activeCalendar = model.getActiveCalendar();
    assertEquals(activeCalendar.getZoneId(), ZoneId.of("America/New_York"));
  }

  @Test
  public void testCopySingleEvent() throws ConflictException {
    IMultipleCalendarModel model = new MultipleCalendarModel();
    model.createCalendar("dublin", ZoneId.of("Europe/Dublin"));
    IZonedCalendarModel calendarModel = model.getActiveCalendar();
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-26T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-26T11:00");
    calendarModel.createSingleEvent(
        "test", 
        startDateTime, 
        endDateTime, 
        true
    );
    model.copyEventWithName(
        "test", 
        startDateTime, 
        "dublin", 
        LocalDateTime.parse("2025-03-26T12:00")
    );
    model.useCalendar("dublin");
    List<IEvent> events = model.getActiveCalendar().queryDate(LocalDate.parse("2025-03-26"));
    IEvent event = events.get(0);
    assertEquals(LocalDateTime.parse("2025-03-26T12:00"), event.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-03-26T13:00"), event.getEndDateTime());
    assertEquals("test", event.getSubject());
  }

}
