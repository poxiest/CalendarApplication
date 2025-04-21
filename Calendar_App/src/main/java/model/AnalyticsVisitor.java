package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import events.EventWeekdays;
import events.IEvent;
import events.OnlineEvent;

/**
 * AnalyticsVisitor is a visitor that collects various statistics about events in a calendar
 * for a given date range. It calculates:
 * <ul>
 *   <li>Total number of events</li>
 *   <li>Number of events per weekday</li>
 *   <li>Number of events by subject</li>
 *   <li>Percentage of online and offline events</li>
 *   <li>Average number of events per day</li>
 *   <li>Most and least busy days by event count and total duration</li>
 * </ul>
 */
public class AnalyticsVisitor implements ICalendarVisitor<Void> {

  private final LocalDate startDate;
  private final LocalDate endDate;
  private final Map<DayOfWeek, Integer> daysCount;
  private List<LocalDate> leastBusyByEvents;
  private List<LocalDate> mostBusyByEvents;
  private List<LocalDate> leastBusyByDuration;
  private List<LocalDate> mostBusyByDuration;
  private int totalCount;
  private int onlineCount;
  private double averageEventsPerDay;
  private Map<String, Long> subjectCountMap;

  /**
   * Constructs an AnalyticsVisitor with the given date range.
   *
   * @param startDate the start date of the range (inclusive)
   * @param endDate   the end date of the range (inclusive)
   */
  public AnalyticsVisitor(LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.totalCount = 0;
    this.onlineCount = 0;
    this.daysCount = new HashMap<>();
    for (DayOfWeek day : DayOfWeek.values()) {
      this.daysCount.put(day, 0);
    }
    this.subjectCountMap = new HashMap<>();
  }

  /**
   * Visits a calendar and computes analytics over the specified date range.
   *
   * @param calendar the calendar model to visit
   * @return null (this visitor does not return a value)
   */
  @Override
  public Void visitCalendarModel(CalendarModel calendar) {
    List<IEvent> events = fetchEvents(calendar);
    updateTotalCount(events);
    populateDaysCount(events);
    computeSubjectCounts(events);
    countOnlineEvents(events);
    calculateAverageEventsPerDay(events);
    calculateBusyDaysByEventCount(events);
    calculateBusyDaysByEventHours(events);
    return null;
  }

  /**
   * Returns the list of dates with the fewest events (excluding days with zero events).
   *
   * @return a list of dates representing the least busy days by event count
   */
  public List<LocalDate> getLeastBusyByEvents() {
    return leastBusyByEvents;
  }

  /**
   * Returns the list of dates with the most events.
   *
   * @return a list of dates representing the most busy days by event count
   */
  public List<LocalDate> getMostBusyByEvents() {
    return mostBusyByEvents;
  }

  /**
   * Returns the list of dates with the least total event duration.
   *
   * @return a list of dates representing the least busy days by total event duration
   */
  public List<LocalDate> getLeastBusyByDuration() {
    return leastBusyByDuration;
  }

  /**
   * Returns the list of dates with the highest total event duration.
   *
   * @return a list of dates representing the most busy days by total event duration
   */
  public List<LocalDate> getMostBusyByDuration() {
    return mostBusyByDuration;
  }

  /**
   * Returns the total number of events found in the selected date range.
   *
   * @return the number of events
   */
  public int getTotalCount() {
    return totalCount;
  }

  /**
   * Returns the percentage of events that are marked as online.
   *
   * @return a percentage value between 0.0 and 100.0 representing online events
   */
  public double getOnlinePercentage() {
    return totalCount == 0 ? 0.0 : (onlineCount * 100.0) / totalCount;
  }

  /**
   * Returns the percentage of events that are marked as offline.
   *
   * @return a percentage value between 0.0 and 100.0 representing offline events
   */
  public double getOfflinePercentage() {
    return totalCount == 0 ? 0.0 : ((totalCount - onlineCount) * 100.0) / totalCount;
  }

  /**
   * Returns the average number of event days per day across the date range.
   *
   * @return the average events per day
   */
  public double getAverageEventsPerDay() {
    return averageEventsPerDay;
  }

  /**
   * Returns an unmodifiable map of weekdays to the number of events that occur on each.
   *
   * @return map of {@link DayOfWeek} to event counts
   */
  public Map<DayOfWeek, Integer> getDaysCount() {
    return Collections.unmodifiableMap(daysCount);
  }

  /**
   * Returns a sorted map of subjects to the number of times they appear across events.
   *
   * @return map of subject names to their frequency count
   */
  public Map<String, Long> getSubjectCountMap() {
    return subjectCountMap.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByKey())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));
  }

  // Private helpers

  private List<IEvent> fetchEvents(CalendarModel calendar) {
    return calendar.queryDateRange(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
  }

  private void updateTotalCount(List<IEvent> events) {
    this.totalCount += events.size();
  }

  private void populateDaysCount(List<IEvent> events) {
    for (IEvent event : events) {
      List<DayOfWeek> days = event.accept(new EventWeekdays());
      for (DayOfWeek day : days) {
        daysCount.put(day, daysCount.get(day) + 1);
      }
    }
  }

  private void computeSubjectCounts(List<IEvent> events) {
    subjectCountMap = events.stream()
        .collect(Collectors.groupingBy(IEvent::getSubject, Collectors.counting()));
  }

  private void countOnlineEvents(List<IEvent> events) {
    for (IEvent event : events) {
      if (event.accept(new OnlineEvent())) {
        onlineCount++;
      }
    }
  }

  private void calculateAverageEventsPerDay(List<IEvent> events) {
    long totalEventDays = 0;
    for (IEvent event : events) {
      LocalDate eventStart = event.getStartDateTime().toLocalDate();
      LocalDate eventEnd = event.getEndDateTime().toLocalDate();
      LocalDate actualStart = eventStart.isBefore(startDate) ? startDate : eventStart;
      LocalDate actualEnd = eventEnd.isAfter(endDate) ? endDate : eventEnd;
      if (!actualStart.isAfter(actualEnd)) {
        totalEventDays += ChronoUnit.DAYS.between(actualStart, actualEnd) + 1;
      }
    }
    long totalDaysInRange = ChronoUnit.DAYS.between(startDate, endDate) + 1;
    averageEventsPerDay = totalDaysInRange == 0 ? 0 : (double) totalEventDays / totalDaysInRange;
  }

  private void calculateBusyDaysByEventCount(List<IEvent> events) {
    Map<LocalDate, Integer> eventsPerDay = new TreeMap<>();
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      eventsPerDay.put(date, 0);
    }

    for (IEvent event : events) {
      LocalDate eventStart = event.getStartDateTime().toLocalDate();
      LocalDate eventEnd = event.getEndDateTime().toLocalDate();
      LocalDate actualStart = eventStart.isBefore(startDate) ? startDate : eventStart;
      LocalDate actualEnd = eventEnd.isAfter(endDate) ? endDate : eventEnd;
      for (LocalDate date = actualStart; !date.isAfter(actualEnd); date = date.plusDays(1)) {
        eventsPerDay.put(date, eventsPerDay.getOrDefault(date, 0) + 1);
      }
    }

    List<Map.Entry<LocalDate, Integer>> nonZeroDays = eventsPerDay.entrySet().stream()
        .filter(entry -> entry.getValue() > 0)
        .collect(Collectors.toList());

    if (nonZeroDays.isEmpty()) {
      leastBusyByEvents = Collections.emptyList();
      mostBusyByEvents = Collections.emptyList();
      return;
    }

    int minCount = nonZeroDays.stream().mapToInt(Map.Entry::getValue).min().orElse(0);
    int maxCount = nonZeroDays.stream().mapToInt(Map.Entry::getValue).max().orElse(0);

    leastBusyByEvents = nonZeroDays.stream()
        .filter(entry -> entry.getValue() == minCount)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    mostBusyByEvents = nonZeroDays.stream()
        .filter(entry -> entry.getValue() == maxCount)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  private void calculateBusyDaysByEventHours(List<IEvent> events) {
    Map<LocalDate, Double> hoursPerDay = new TreeMap<>();
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      hoursPerDay.put(date, calculateOverlapHoursOnDate(events, date));
    }

    List<Map.Entry<LocalDate, Double>> nonZeroDays = hoursPerDay.entrySet().stream()
        .filter(entry -> entry.getValue() > 0.0)
        .collect(Collectors.toList());

    if (nonZeroDays.isEmpty()) {
      leastBusyByDuration = Collections.emptyList();
      mostBusyByDuration = Collections.emptyList();
      return;
    }

    double minHours = nonZeroDays.stream().mapToDouble(Map.Entry::getValue).min().orElse(0.0);
    double maxHours = nonZeroDays.stream().mapToDouble(Map.Entry::getValue).max().orElse(0.0);

    leastBusyByDuration = nonZeroDays.stream()
        .filter(entry -> entry.getValue() == minHours)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    mostBusyByDuration = nonZeroDays.stream()
        .filter(entry -> entry.getValue() == maxHours)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  private double calculateOverlapHoursOnDate(List<IEvent> events, LocalDate date) {
    LocalDateTime dayStart = date.atStartOfDay();
    LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

    return events.stream()
        .mapToDouble(event -> {
          LocalDateTime eventStart = event.getStartDateTime();
          LocalDateTime eventEnd = event.getEndDateTime();
          LocalDateTime overlapStart = eventStart.isBefore(dayStart) ? dayStart : eventStart;
          LocalDateTime overlapEnd = eventEnd.isAfter(dayEnd) ? dayEnd : eventEnd;
          return overlapStart.isBefore(overlapEnd)
              ? ChronoUnit.MINUTES.between(overlapStart, overlapEnd)
              : 0.0;
        })
        .sum();
  }
}
