package model;

import static org.junit.Assert.assertEquals;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import calendar.ConflictException;
import events.IEvent;
import events.SingleEventProperties;

/**
 * This is a JUnit test class for the calendar model.
 */
public class CalendarModelTest {

  ICalendarModel model;

  @Before
  public void setup() {
    this.model = new CalendarModel();
  }

  @Test
  public void testCreateSingle() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T11:00");
    model.createSingleEvent(
        "test", 
        startDateTime, 
        endDateTime, 
        true
    );
    List<IEvent> events = model.queryDate(startDateTime.toLocalDate());
    IEvent event = events.get(0);
    assertEquals("test", event.getSubject());
    assertEquals(startDateTime, event.getStartDateTime());
    assertEquals(endDateTime, event.getEndDateTime());
  }

  @Test(expected = ConflictException.class)
  public void testCreateSingleConflict() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T11:00");
    model.createSingleEvent(
        "test", 
        startDateTime, 
        endDateTime, 
        true
    );
    model.createSingleEvent(
        "test", 
        startDateTime, 
        endDateTime, 
        true
    );
  }

  @Test
  public void testCreateNRecurring() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-24T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-24T11:00");
    int n = 4;
    List<DayOfWeek> weekdays = new ArrayList<DayOfWeek>(0);
    weekdays.add(DayOfWeek.TUESDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    model.createNRecurringEvents("test", startDateTime, endDateTime, weekdays, n, true);
    List<IEvent> events = model.queryDateRange(startDateTime, endDateTime.plusWeeks(2));
    IEvent event0 = events.get(0);
    IEvent event1 = events.get(1);
    IEvent event2 = events.get(2);
    IEvent event3 = events.get(3);
    assertEquals("test", event0.getSubject());
    assertEquals(LocalDateTime.parse("2025-03-25T10:00"), event0.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-03-25T11:00"), event0.getEndDateTime());
    assertEquals("test", event1.getSubject());
    assertEquals(LocalDateTime.parse("2025-03-26T10:00"), event1.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-03-26T11:00"), event1.getEndDateTime());
    assertEquals("test", event2.getSubject());
    assertEquals(LocalDateTime.parse("2025-04-01T10:00"), event2.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-04-01T11:00"), event2.getEndDateTime());
    assertEquals("test", event3.getSubject());
    assertEquals(LocalDateTime.parse("2025-04-02T10:00"), event3.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-04-02T11:00"), event3.getEndDateTime());
  }

  @Test(expected = ConflictException.class)
  public void testCreateNRecurringConflict() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-24T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-24T11:00");
    int n = 2;
    List<DayOfWeek> weekdays = new ArrayList<DayOfWeek>(0);
    weekdays.add(DayOfWeek.MONDAY);
    model.createSingleEvent(
        "test", 
        startDateTime, 
        endDateTime, 
        true
    );
    model.createNRecurringEvents("test", startDateTime, endDateTime, weekdays, n, true);
  }

  @Test
  public void testCreateRecurringUntil() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-24T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-24T11:00");
    List<DayOfWeek> weekdays = new ArrayList<DayOfWeek>(0);
    weekdays.add(DayOfWeek.TUESDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    LocalDateTime untilDateTime = LocalDateTime.parse("2025-04-03T09:00");
    model.createRecurringEventsUntil(
        "test", 
        startDateTime, 
        endDateTime, 
        weekdays, 
        untilDateTime, 
        true
    );
    List<IEvent> events = model.queryDateRange(startDateTime, endDateTime.plusWeeks(2));
    IEvent event0 = events.get(0);
    IEvent event1 = events.get(1);
    IEvent event2 = events.get(2);
    IEvent event3 = events.get(3);
    assertEquals("test", event0.getSubject());
    assertEquals(LocalDateTime.parse("2025-03-25T10:00"), event0.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-03-25T11:00"), event0.getEndDateTime());
    assertEquals("test", event1.getSubject());
    assertEquals(LocalDateTime.parse("2025-03-26T10:00"), event1.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-03-26T11:00"), event1.getEndDateTime());
    assertEquals("test", event2.getSubject());
    assertEquals(LocalDateTime.parse("2025-04-01T10:00"), event2.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-04-01T11:00"), event2.getEndDateTime());
    assertEquals("test", event3.getSubject());
    assertEquals(LocalDateTime.parse("2025-04-02T10:00"), event3.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-04-02T11:00"), event3.getEndDateTime());
  }


  @Test (expected = ConflictException.class)
  public void testRecurringUntilConflict() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-24T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-24T11:00");
    LocalDateTime untilDateTime = LocalDateTime.parse("2025-04-02T10:00");
    List<DayOfWeek> weekdays = new ArrayList<DayOfWeek>(0);
    weekdays.add(DayOfWeek.MONDAY);
    model.createSingleEvent(
        "test", 
        startDateTime, 
        endDateTime, 
        true
    );
    model.createRecurringEventsUntil(
        "test", 
        startDateTime, 
        endDateTime, 
        weekdays, 
        untilDateTime, 
        true
    );
  }

  @Test
  public void testCreateSingleAllDay() throws ConflictException {
    LocalDate date = LocalDate.parse("2025-03-24");
    LocalTime startTime = LocalTime.MIN;
    LocalTime endTime = LocalTime.MAX;
    model.createSingleAllDayEvent("test", date, true);
    List<IEvent> events = model.queryDate(date);
    IEvent event = events.get(0);
    assertEquals("test", event.getSubject());
    assertEquals(true, event.isAllDayEvent());
    assertEquals(date, event.getStartDate());
    assertEquals(date, event.getEndDate());
    assertEquals(startTime, event.getStartTime());
    assertEquals(endTime, event.getEndTime());
  }

  @Test(expected = ConflictException.class)
  public void testCreateSingleAllDayConflict() throws ConflictException {
    LocalDate date = LocalDate.parse("2025-03-24");
    model.createSingleAllDayEvent("test", date, true);
    model.createSingleAllDayEvent("test", date, true);
  }

  @Test
  public void testCreateNRecurringAllDay() throws ConflictException {
    int n = 2;
    LocalDate date = LocalDate.parse("2025-03-24");
    List<DayOfWeek> weekdays = new ArrayList<DayOfWeek>(0);
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-24T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-24T11:00");
    weekdays.add(DayOfWeek.TUESDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    model.createNRecurringAllDayEvents("test", date, weekdays, n);
    List<IEvent> events = model.queryDateRange(startDateTime, endDateTime.plusWeeks(2));
    IEvent event0 = events.get(0);
    IEvent event1 = events.get(1);
    IEvent event2 = events.get(2);
    IEvent event3 = events.get(3);
    assertEquals("test", event0.getSubject());
    assertEquals(LocalDateTime.parse("2025-03-25T" + LocalTime.MIN), event0.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-03-25T" + LocalTime.MAX), event0.getEndDateTime());
    assertEquals(true, event0.isAllDayEvent());
    assertEquals("test", event1.getSubject());
    assertEquals(LocalDateTime.parse("2025-03-26T" + LocalTime.MIN), event1.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-03-26T" + LocalTime.MAX), event1.getEndDateTime());
    assertEquals(true, event2.isAllDayEvent());
    assertEquals("test", event2.getSubject());
    assertEquals(LocalDateTime.parse("2025-04-01T" + LocalTime.MIN), event2.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-04-01T" + LocalTime.MAX), event2.getEndDateTime());
    assertEquals(true, event2.isAllDayEvent());
    assertEquals("test", event3.getSubject());
    assertEquals(LocalDateTime.parse("2025-04-02T" + LocalTime.MIN), event3.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-04-02T" + LocalTime.MAX), event3.getEndDateTime());
    assertEquals(true, event3.isAllDayEvent());
  }

  @Test(expected = ConflictException.class)
  public void testCreateNRecurringAllDayConflict() throws ConflictException {
    LocalDate date = LocalDate.parse("2025-03-24");
    int n = 2;
    List<DayOfWeek> weekdays = new ArrayList<DayOfWeek>(0);
    weekdays.add(DayOfWeek.MONDAY);
    model.createSingleAllDayEvent("test", date, true);
    model.createNRecurringAllDayEvents("test", date, weekdays, n);
  }

  @Test
  public void testCreateRecurringAllDayUntil() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-25T" + LocalTime.MIN);
    LocalDate date = LocalDate.parse("2025-03-24");
    LocalDate endDate = LocalDate.parse("2025-04-03");
    List<DayOfWeek> weekdays = new ArrayList<DayOfWeek>(0);
    weekdays.add(DayOfWeek.TUESDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    model.createRecurringAllDayEventsUntil("test", date, weekdays, endDate);
    List<IEvent> events = model.queryDateRange(startDateTime, startDateTime.plusWeeks(2));
    IEvent event0 = events.get(0);
    IEvent event1 = events.get(1);
    IEvent event2 = events.get(2);
    IEvent event3 = events.get(3);
    assertEquals("test", event0.getSubject());
    assertEquals(LocalDateTime.parse("2025-03-25T" + LocalTime.MIN), event0.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-03-25T" + LocalTime.MAX), event0.getEndDateTime());
    assertEquals(true, event0.isAllDayEvent());
    assertEquals("test", event1.getSubject());
    assertEquals(LocalDateTime.parse("2025-03-26T" + LocalTime.MIN), event1.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-03-26T" + LocalTime.MAX), event1.getEndDateTime());
    assertEquals(true, event2.isAllDayEvent());
    assertEquals("test", event2.getSubject());
    assertEquals(LocalDateTime.parse("2025-04-01T" + LocalTime.MIN), event2.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-04-01T" + LocalTime.MAX), event2.getEndDateTime());
    assertEquals(true, event2.isAllDayEvent());
    assertEquals("test", event3.getSubject());
    assertEquals(LocalDateTime.parse("2025-04-02T" + LocalTime.MIN), event3.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-04-02T" + LocalTime.MAX), event3.getEndDateTime());
  }

  @Test(expected = ConflictException.class)
  public void testCreateRecurringAllDayConflict() throws ConflictException {
    LocalDate date = LocalDate.parse("2025-03-24");
    LocalDate endDate = LocalDate.parse("2025-04-03");
    List<DayOfWeek> weekdays = new ArrayList<DayOfWeek>(0);
    weekdays.add(DayOfWeek.MONDAY);
    model.createSingleAllDayEvent("test", date, true);
    model.createRecurringAllDayEventsUntil("test", date, weekdays, endDate);
  }

  @Test
  public void testEditSingle() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-25T" + LocalTime.MIN);
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-25T" + LocalTime.MAX);
    LocalDate date = LocalDate.parse("2025-03-25");
    model.createSingleAllDayEvent("test", date, true);
    model.editSingle(SingleEventProperties.SUBJECT, "test", startDateTime, endDateTime, "updated");
    IEvent event = model.queryDate(date).get(0);
    assertEquals(startDateTime, event.getStartDateTime());
    assertEquals(endDateTime, event.getEndDateTime());
    assertEquals(true, event.isAllDayEvent());
    assertEquals("updated", event.getSubject());
  }

  @Test(expected = ConflictException.class)
  public void testEditSingleConflict() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-25T" + LocalTime.MIN);
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-25T" + LocalTime.MAX);
    LocalDate date = LocalDate.parse("2025-03-25");
    LocalDate nextDay = date.plusDays(1);
    model.createSingleAllDayEvent("test", date, true);
    model.createSingleAllDayEvent("test1", nextDay, true);
    model.editSingle(
        SingleEventProperties.STARTDATE, 
        "test", 
        startDateTime, 
        endDateTime, 
        nextDay.toString()
    );
  }

  @Test
  public void testEditFromDate() throws ConflictException {
    LocalDate date = LocalDate.parse("2025-03-25");
    LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MIN);
    LocalDateTime endDateTime = startDateTime.plusDays(4);
    model.createSingleAllDayEvent("test", date, true);
    model.createSingleAllDayEvent("test", date.plusDays(1), true);
    model.createSingleAllDayEvent("test", date.plusDays(2), true);
    model.editFromDate(SingleEventProperties.SUBJECT, "test", startDateTime, "updated");
    List<IEvent> events = model.queryDateRange(startDateTime, endDateTime);
    for (IEvent event: events) {
      assertEquals("updated", event.getSubject());
    }
  }

  @Test(expected = ConflictException.class)
  public void testEditFromDateConflict() throws ConflictException {
    LocalDate date = LocalDate.parse("2025-03-25");
    LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MAX);
    model.createSingleAllDayEvent("test", date, true);
    model.createSingleAllDayEvent("test", date.plusDays(1), true);
    model.editFromDate(SingleEventProperties.STARTDATE, "test", startDateTime, "2025-03-25");
  }

  @Test
  public void testEditAll() throws ConflictException {
    LocalDate date = LocalDate.parse("2025-03-25");
    LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MIN);
    LocalDateTime endDateTime = startDateTime.plusDays(4);
    model.createSingleAllDayEvent("test", date, true);
    model.createSingleAllDayEvent("test", date.plusDays(1), true);
    model.createSingleAllDayEvent("test", date.plusDays(2), true);
    model.editAll(SingleEventProperties.SUBJECT, "test", "updated");
    List<IEvent> events = model.queryDateRange(startDateTime, endDateTime);
    for (IEvent event: events) {
      assertEquals("updated", event.getSubject());
    }
  }

  @Test(expected = ConflictException.class)
  public void testEditAllConflict() throws ConflictException {
    LocalDate date = LocalDate.parse("2025-03-25");
    LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MAX);
    model.createSingleAllDayEvent("test", date, true);
    model.createSingleAllDayEvent("test1", date.plusDays(1), true);
    model.editFromDate(SingleEventProperties.STARTDATE, "test1", startDateTime, "2025-03-25");
  }

  @Test
  public void testPrintOnDate() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T11:00");
    model.createSingleEvent(
        "test", 
        startDateTime, 
        endDateTime, 
        true
    );
    model.createSingleEvent(
        "test1", 
        startDateTime.plusHours(2), 
        endDateTime.plusHours(2), 
        true
    );
    String actual = model.printEventsOnDate(LocalDate.parse("2025-03-11"));
    String expected = "• test 10:00-11:00\n";
    expected += "• test1 12:00-13:00\n";
    assertEquals(expected, actual);
  }

  @Test
  public void testPrintFromTo() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T11:00");
    model.createSingleEvent(
        "test", 
        startDateTime, 
        endDateTime, 
        true
    );
    model.createSingleEvent(
        "test1", 
        startDateTime.plusDays(1), 
        endDateTime.plusDays(1), 
        true
    );
    model.createSingleEvent(
        "test2", 
        startDateTime.plusDays(2), 
        endDateTime.plusDays(2), 
        true
    );
    String actual = model.printEventsFromTo(startDateTime, endDateTime.plusDays(3));
    String expected = "• test 2025-03-11T10:00-2025-03-11T11:00\n";
    expected += "• test1 2025-03-12T10:00-2025-03-12T11:00\n";
    expected += "• test2 2025-03-13T10:00-2025-03-13T11:00\n";
    assertEquals(expected, actual);
  }

  @Test
  public void testShowBusy() throws ConflictException {
    LocalDate date = LocalDate.parse("2025-03-25");
    model.createSingleAllDayEvent("test", date, true);
    String status = model.show(LocalDateTime.of(date, LocalTime.NOON));
    assertEquals("Busy\n", status);
  }

  @Test
  public void testShowAvailable() throws ConflictException {
    LocalDate date = LocalDate.parse("2025-03-25");
    String status = model.show(LocalDateTime.of(date, LocalTime.NOON));
    assertEquals("Available\n", status);
  }
}
