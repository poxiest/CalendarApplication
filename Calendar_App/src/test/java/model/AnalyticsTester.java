package model;

import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import calendar.ConflictException;
import events.IEvent;
import events.OnlineEvent;
import events.RecurringUntilEvent;
import events.SingleEventProperties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for Analytics of the events.
 */
public class AnalyticsTester {

  ICalendarModel model;

  @Before
  public void setup() {
    this.model = new CalendarModel();
  }

  @Test
  public void testAnalyticsEmptyDays() {
    AnalyticsVisitor visitor = new AnalyticsVisitor(LocalDate.of(2025, 3, 1), LocalDate.of(2025,
        3, 2));
    model.accept(visitor);
    assertEquals(0, visitor.getTotalCount());
    for (Map.Entry<DayOfWeek, Integer> entry : visitor.getDaysCount().entrySet()) {
      assertEquals(0, entry.getValue().intValue());
    }
    assertEquals(0, visitor.getSubjectCountMap().size());
    assertEquals(0, visitor.getAverageEventsPerDay(), 0);
    assertEquals(0.00, visitor.getOnlinePercentage(), 0);
    assertEquals(0.00, visitor.getOfflinePercentage(), 0);
    assertTrue(visitor.getLeastBusyByEvents().isEmpty());
    assertTrue(visitor.getMostBusyByEvents().isEmpty());
    assertTrue(visitor.getLeastBusyByDuration().isEmpty());
    assertTrue(visitor.getMostBusyByDuration().isEmpty());
  }

  @Test
  public void testAnalyticsForFewDays() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T11:00");
    model.createSingleEvent(
        "testSingleEvent",
        startDateTime,
        endDateTime,
        true
    );

    startDateTime = LocalDateTime.parse("2025-03-12T10:00");
    endDateTime = LocalDateTime.parse("2025-03-12T11:15");
    int n = 3;
    List<DayOfWeek> weekdays = new ArrayList<DayOfWeek>(0);
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.TUESDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    weekdays.add(DayOfWeek.THURSDAY);
    weekdays.add(DayOfWeek.FRIDAY);
    weekdays.add(DayOfWeek.SATURDAY);
    weekdays.add(DayOfWeek.SUNDAY);
    model.createNRecurringEvents("testRecurringEvent", startDateTime, endDateTime, weekdays, n,
        true);

    AnalyticsVisitor visitor = new AnalyticsVisitor(LocalDate.of(2025, 3, 11), LocalDate.of(2025,
        3, 15));
    model.accept(visitor);
    assertEquals(4, visitor.getTotalCount());

    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.MONDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.TUESDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.WEDNESDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.THURSDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.FRIDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.SATURDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.SUNDAY));


    assertEquals(Long.valueOf(1), visitor.getSubjectCountMap().get("testSingleEvent"));
    assertEquals(Long.valueOf(3), visitor.getSubjectCountMap().get("testRecurringEvent"));
    assertEquals(0.8, visitor.getAverageEventsPerDay(), 0);
    assertEquals(0.00, visitor.getOnlinePercentage(), 0);
    assertEquals(100.0, visitor.getOfflinePercentage(), 0);

    List<LocalDate> expectedMostBusyByEvents = List.of(
        LocalDate.of(2025, 3, 11),
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13),
        LocalDate.of(2025, 3, 14)
    );

    List<LocalDate> expectedLeastBusyByEvents = List.of(
        LocalDate.of(2025, 3, 11),
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13),
        LocalDate.of(2025, 3, 14)
    );

    List<LocalDate> expectedLeastBusyByHours = List.of(
        LocalDate.of(2025, 3, 11)
    );

    List<LocalDate> expectedMostBusyByHours = List.of(
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13),
        LocalDate.of(2025, 3, 14)
    );

    assertEquals(expectedMostBusyByEvents, visitor.getMostBusyByEvents());
    assertEquals(expectedLeastBusyByEvents, visitor.getLeastBusyByEvents());
    assertEquals(expectedMostBusyByHours, visitor.getMostBusyByDuration());
    assertEquals(expectedLeastBusyByHours, visitor.getLeastBusyByDuration());
  }

  @Test
  public void testAnalyticsFor1Days() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T11:00");
    model.createSingleEvent(
        "testSingleEvent",
        startDateTime,
        endDateTime,
        true
    );
    model.editSingle(SingleEventProperties.LOCATION, "testSingleEvent", startDateTime,
        endDateTime, "online");

    AnalyticsVisitor visitor = new AnalyticsVisitor(LocalDate.of(2025, 3, 11), LocalDate.of(2025,
        3, 12));
    model.accept(visitor);
    assertEquals(1, visitor.getTotalCount());

    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.MONDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.TUESDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.WEDNESDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.THURSDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.FRIDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.SATURDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.SUNDAY));


    assertEquals(Long.valueOf(1), visitor.getSubjectCountMap().get("testSingleEvent"));

    assertEquals(0.5, visitor.getAverageEventsPerDay(), 0);
    assertEquals(100.0, visitor.getOnlinePercentage(), 0);
    assertEquals(0.0, visitor.getOfflinePercentage(), 0);

    List<LocalDate> expectedMostBusyByEvents = List.of(
        LocalDate.of(2025, 3, 11)
    );

    List<LocalDate> expectedLeastBusyByEvents = List.of(
        LocalDate.of(2025, 3, 11)
    );

    List<LocalDate> expectedLeastBusyByHours = List.of(
        LocalDate.of(2025, 3, 11)
    );

    List<LocalDate> expectedMostBusyByHours = List.of(
        LocalDate.of(2025, 3, 11)
    );

    assertEquals(expectedMostBusyByEvents, visitor.getMostBusyByEvents());
    assertEquals(expectedLeastBusyByEvents, visitor.getLeastBusyByEvents());
    assertEquals(expectedMostBusyByHours, visitor.getMostBusyByDuration());
    assertEquals(expectedLeastBusyByHours, visitor.getLeastBusyByDuration());
  }


  @Test
  public void testAnalyticsForMultipleDays() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T12:00");
    model.createSingleEvent(
        "testSingleEvent",
        startDateTime,
        endDateTime,
        true
    );
    model.editSingle(SingleEventProperties.LOCATION, "testSingleEvent", startDateTime,
        endDateTime, "online");

    startDateTime = LocalDateTime.parse("2025-03-11T12:00");
    endDateTime = LocalDateTime.parse("2025-03-11T12:45");
    int n = 5;
    List<DayOfWeek> weekdays = new ArrayList<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.TUESDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    weekdays.add(DayOfWeek.THURSDAY);
    weekdays.add(DayOfWeek.FRIDAY);
    weekdays.add(DayOfWeek.SATURDAY);
    weekdays.add(DayOfWeek.SUNDAY);
    model.createNRecurringEvents("testRecurringEvent", startDateTime, endDateTime, weekdays, n,
        true);
    model.editAll(SingleEventProperties.LOCATION, "testRecurringEvent", "hall");

    startDateTime = LocalDateTime.parse("2025-03-11T15:00");
    endDateTime = LocalDateTime.parse("2025-03-11T16:00");
    weekdays = new ArrayList<>();
    weekdays.add(DayOfWeek.FRIDAY);
    weekdays.add(DayOfWeek.SATURDAY);
    weekdays.add(DayOfWeek.THURSDAY);
    LocalDateTime untilDateTime = LocalDateTime.parse("2025-03-20T09:00");
    model.createRecurringEventsUntil(
        "testUntilEvent",
        startDateTime,
        endDateTime,
        weekdays,
        untilDateTime,
        true
    );
    model.editAll(SingleEventProperties.LOCATION, "testUntilEvent", "online");

    AnalyticsVisitor visitor = new AnalyticsVisitor(LocalDate.of(2025, 3, 11), LocalDate.of(2025,
        3, 21));
    model.accept(visitor);
    assertEquals(9, visitor.getTotalCount());

    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.MONDAY));
    assertEquals(Integer.valueOf(2), visitor.getDaysCount().get(DayOfWeek.TUESDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.WEDNESDAY));
    assertEquals(Integer.valueOf(2), visitor.getDaysCount().get(DayOfWeek.THURSDAY));
    assertEquals(Integer.valueOf(2), visitor.getDaysCount().get(DayOfWeek.FRIDAY));
    assertEquals(Integer.valueOf(2), visitor.getDaysCount().get(DayOfWeek.SATURDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.SUNDAY));

    assertEquals(Long.valueOf(1), visitor.getSubjectCountMap().get("testSingleEvent"));
    assertEquals(Long.valueOf(3), visitor.getSubjectCountMap().get("testUntilEvent"));
    assertEquals(Long.valueOf(5), visitor.getSubjectCountMap().get("testRecurringEvent"));

    assertEquals(0.81, visitor.getAverageEventsPerDay(), 0.02);
    assertEquals(44.44, visitor.getOnlinePercentage(), 0.02);
    assertEquals(55.56, visitor.getOfflinePercentage(), 0.02);

    List<LocalDate> expectedMostBusyByEvents = List.of(
        LocalDate.of(2025, 3, 11),
        LocalDate.of(2025, 3, 13),
        LocalDate.of(2025, 3, 14),
        LocalDate.of(2025, 3, 15)
    );

    List<LocalDate> expectedLeastBusy = List.of(
        LocalDate.of(2025, 3, 12)
    );

    List<LocalDate> expectedMostBusyByHours = List.of(
        LocalDate.of(2025, 3, 11)
    );

    assertEquals(expectedMostBusyByEvents, visitor.getMostBusyByEvents());
    assertEquals(expectedLeastBusy, visitor.getLeastBusyByEvents());
    assertEquals(expectedMostBusyByHours, visitor.getMostBusyByDuration());
    assertEquals(expectedLeastBusy, visitor.getLeastBusyByDuration());
  }

  @Test
  public void testAnalyticsFor3Days() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-13T11:00");
    model.createSingleEvent(
        "testSingleEvent",
        startDateTime,
        endDateTime,
        true
    );
    model.editSingle(SingleEventProperties.LOCATION, "testSingleEvent", startDateTime,
        endDateTime, "online");

    AnalyticsVisitor visitor = new AnalyticsVisitor(LocalDate.of(2025, 3, 11), LocalDate.of(2025,
        3, 14));
    model.accept(visitor);
    assertEquals(1, visitor.getTotalCount());
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.MONDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.TUESDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.WEDNESDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.THURSDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.FRIDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.SATURDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.SUNDAY));

    assertEquals(Long.valueOf(1), visitor.getSubjectCountMap().get("testSingleEvent"));
    assertEquals(0.75, visitor.getAverageEventsPerDay(), 0);
    assertEquals(100.0, visitor.getOnlinePercentage(), 0);
    assertEquals(0.0, visitor.getOfflinePercentage(), 0);


    List<LocalDate> expectedMostBusyByEvents = List.of(
        LocalDate.of(2025, 3, 11),
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13)
    );

    List<LocalDate> expectedLeastBusyByEvents = List.of(
        LocalDate.of(2025, 3, 11),
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13)
    );

    List<LocalDate> expectedLeastBusyByHours = List.of(
        LocalDate.of(2025, 3, 13)
    );

    List<LocalDate> expectedMostBusyByHours = List.of(
        LocalDate.of(2025, 3, 12)
    );

    assertEquals(expectedMostBusyByEvents, visitor.getMostBusyByEvents());
    assertEquals(expectedLeastBusyByEvents, visitor.getLeastBusyByEvents());
    assertEquals(expectedMostBusyByHours, visitor.getMostBusyByDuration());
    assertEquals(expectedLeastBusyByHours, visitor.getLeastBusyByDuration());
    assertFalse(visitor.getMostBusyByEvents().contains(LocalDate.of(2025, 3, 14)));
  }

  @Test
  public void testAnalyticsForUntilEvent() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T11:00");

    List<DayOfWeek> weekdays = new ArrayList<>();
    weekdays.add(DayOfWeek.FRIDAY);
    weekdays.add(DayOfWeek.SATURDAY);
    weekdays.add(DayOfWeek.THURSDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    LocalDateTime untilDateTime = LocalDateTime.parse("2025-03-20T09:00");
    model.createRecurringEventsUntil(
        "testUntilEvent",
        startDateTime,
        endDateTime,
        weekdays,
        untilDateTime,
        true
    );

    AnalyticsVisitor visitor = new AnalyticsVisitor(LocalDate.of(2025, 3, 11), LocalDate.of(2025,
        3, 20));
    model.accept(visitor);
    assertEquals(5, visitor.getTotalCount());

    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.MONDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.TUESDAY));
    assertEquals(Integer.valueOf(2), visitor.getDaysCount().get(DayOfWeek.WEDNESDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.THURSDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.FRIDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.SATURDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.SUNDAY));

    assertEquals(Long.valueOf(5), visitor.getSubjectCountMap().get("testUntilEvent"));

    assertEquals(0.5, visitor.getAverageEventsPerDay(), 0.02);
    assertEquals(100.0, visitor.getOfflinePercentage(), 0.02);
    assertEquals(0.0, visitor.getOnlinePercentage(), 0.02);

    List<LocalDate> expectedMostBusyByEvents = List.of(
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13),
        LocalDate.of(2025, 3, 14),
        LocalDate.of(2025, 3, 15),
        LocalDate.of(2025, 3, 19)
    );

    List<LocalDate> expectedLeastBusy = List.of(
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13),
        LocalDate.of(2025, 3, 14),
        LocalDate.of(2025, 3, 15),
        LocalDate.of(2025, 3, 19)
    );

    List<LocalDate> expectedMostBusyByHours = List.of(
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13),
        LocalDate.of(2025, 3, 14),
        LocalDate.of(2025, 3, 15),
        LocalDate.of(2025, 3, 19)
    );

    assertEquals(expectedMostBusyByEvents, visitor.getMostBusyByEvents());
    assertEquals(expectedLeastBusy, visitor.getLeastBusyByEvents());
    assertEquals(expectedMostBusyByHours, visitor.getMostBusyByDuration());
    assertEquals(expectedLeastBusy, visitor.getLeastBusyByDuration());
  }


  @Test
  public void testAnalyticsForUntilEventOnline() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T11:00");

    List<DayOfWeek> weekdays = new ArrayList<>();
    weekdays.add(DayOfWeek.FRIDAY);
    weekdays.add(DayOfWeek.SATURDAY);
    weekdays.add(DayOfWeek.THURSDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    LocalDateTime untilDateTime = LocalDateTime.parse("2025-03-20T09:00");
    model.createRecurringEventsUntil(
        "testUntilEvent",
        startDateTime,
        endDateTime,
        weekdays,
        untilDateTime,
        true
    );
    model.editAll(SingleEventProperties.LOCATION, "testUntilEvent", "online");

    AnalyticsVisitor visitor = new AnalyticsVisitor(LocalDate.of(2025, 3, 11), LocalDate.of(2025,
        3, 20));
    model.accept(visitor);
    assertEquals(5, visitor.getTotalCount());

    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.MONDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.TUESDAY));
    assertEquals(Integer.valueOf(2), visitor.getDaysCount().get(DayOfWeek.WEDNESDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.THURSDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.FRIDAY));
    assertEquals(Integer.valueOf(1), visitor.getDaysCount().get(DayOfWeek.SATURDAY));
    assertEquals(Integer.valueOf(0), visitor.getDaysCount().get(DayOfWeek.SUNDAY));

    assertEquals(Long.valueOf(5), visitor.getSubjectCountMap().get("testUntilEvent"));

    assertEquals(0.5, visitor.getAverageEventsPerDay(), 0.02);
    assertEquals(100.0, visitor.getOnlinePercentage(), 0.02);
    assertEquals(0.0, visitor.getOfflinePercentage(), 0.02);

    List<LocalDate> expectedMostBusyByEvents = List.of(
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13),
        LocalDate.of(2025, 3, 14),
        LocalDate.of(2025, 3, 15),
        LocalDate.of(2025, 3, 19)
    );

    List<LocalDate> expectedLeastBusy = List.of(
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13),
        LocalDate.of(2025, 3, 14),
        LocalDate.of(2025, 3, 15),
        LocalDate.of(2025, 3, 19)
    );

    List<LocalDate> expectedMostBusyByHours = List.of(
        LocalDate.of(2025, 3, 12),
        LocalDate.of(2025, 3, 13),
        LocalDate.of(2025, 3, 14),
        LocalDate.of(2025, 3, 15),
        LocalDate.of(2025, 3, 19)
    );

    assertEquals(expectedMostBusyByEvents, visitor.getMostBusyByEvents());
    assertEquals(expectedLeastBusy, visitor.getLeastBusyByEvents());
    assertEquals(expectedMostBusyByHours, visitor.getMostBusyByDuration());
    assertEquals(expectedLeastBusy, visitor.getLeastBusyByDuration());
  }

  @Test
  public void testOnlineRecurringEvent() throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T11:00");
    List<DayOfWeek> weekdays = new ArrayList<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.TUESDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    weekdays.add(DayOfWeek.THURSDAY);
    weekdays.add(DayOfWeek.FRIDAY);
    weekdays.add(DayOfWeek.SATURDAY);
    weekdays.add(DayOfWeek.SUNDAY);
    LocalDateTime untilDateTime = LocalDateTime.parse("2025-03-12T09:00");
    model.createRecurringEventsUntil(
        "testUntilEvent",
        startDateTime,
        endDateTime,
        weekdays,
        untilDateTime,
        true
    );
    List<IEvent> events = model.queryDate(LocalDate.of(2025, 3, 11));
    OnlineEvent onlineEvent = new OnlineEvent();
    for (IEvent e : events) {
      assertFalse(onlineEvent.visitRecurringUntil((RecurringUntilEvent) e));
    }
  }

  @Test
  public void testEventOnOnlyEndDateIncluded() throws ConflictException {
    LocalDate onlyDay = LocalDate.of(2025, 3, 15);
    LocalDateTime startDateTime = onlyDay.atTime(10, 0);
    LocalDateTime endDateTime = onlyDay.atTime(11, 0);
    model.createSingleEvent("onlyDayEvent", startDateTime, endDateTime, true);

    AnalyticsVisitor visitor = new AnalyticsVisitor(onlyDay, onlyDay);
    model.accept(visitor);

    assertEquals(List.of(onlyDay), visitor.getMostBusyByEvents());
    assertEquals(List.of(onlyDay), visitor.getLeastBusyByEvents());
    assertEquals(List.of(onlyDay), visitor.getMostBusyByDuration());
    assertEquals(List.of(onlyDay), visitor.getLeastBusyByDuration());
    assertEquals(1, visitor.getTotalCount());
  }
}
