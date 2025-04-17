package model;

import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import calendar.ConflictException;

import static org.junit.Assert.assertEquals;

public class AnalyticsTester {

  ICalendarModel model;

  @Before
  public void setup() {
    this.model = new CalendarModel();
  }

  //Total Events: 0
  //Events by Day of Week:
  //	MONDAY: 0
  //	TUESDAY: 0
  //	WEDNESDAY: 0
  //	THURSDAY: 0
  //	FRIDAY: 0
  //	SATURDAY: 0
  //	SUNDAY: 0
  //Events by Subject:
  //Average Events per Day: 0.00
  //Online Events: 0.00%
  //Offline Events: 0.00%
  //Most Busy Date(s) by Event Count:
  //Least Busy Date(s) by Event Count:
  //Most Busy Date(s) by Total Hours:
  //Least Busy Date(s) by Total Hours:


  @Test
  public void testAnalyticsEmptyDays() {
    AnalyticsVisitor visitor = new AnalyticsVisitor(LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 2));
    model.accept(visitor);
    assertEquals(0, visitor.getTotalCount());
    for(Map.Entry<DayOfWeek, Integer> entry : visitor.getDaysCount().entrySet()) {
      assertEquals(0, entry.getValue().intValue());
    }
    assertEquals(0, visitor.getSubjectCountMap().size());
    assertEquals(0, visitor.getAverageEventsPerDay(), 0);
    assertEquals(0.00, visitor.getOnlinePercentage(), 0);
    assertEquals(0.00, visitor.getOfflinePercentage(), 0);
    assertEquals(0, visitor.getLeastBusyByEvents().size());
    assertEquals(0, visitor.getMostBusyByEvents().size());
    assertEquals(0, visitor.getLeastBusyByHours().size());
    assertEquals(0, visitor.getMostBusyByHours().size());
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
    model.createNRecurringEvents("testRecurringEvent", startDateTime, endDateTime, weekdays, n, true);

    AnalyticsVisitor visitor = new AnalyticsVisitor(LocalDate.of(2025, 3, 11), LocalDate.of(2025, 3, 15));
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
    assertEquals(4, visitor.getLeastBusyByEvents().size());
    assertEquals(4, visitor.getMostBusyByEvents().size());
    assertEquals(1, visitor.getLeastBusyByHours().size());
    assertEquals(3, visitor.getMostBusyByHours().size());
  }
}
