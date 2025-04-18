package events;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A visitor that returns the list of {@link DayOfWeek}s that an event spans.
 */
public class EventWeekdays implements IEventVisitor<List<DayOfWeek>> {
  @Override
  public List<DayOfWeek> visitEvent(Event e) {
    return getDayLettersBetween(e.getStartDateTime(), e.getEndDateTime());
  }

  @Override
  public List<DayOfWeek> visitRecurringSequence(RecurringSequenceEvent e) {
    return getDayLettersBetween(e.getStartDateTime(), e.getEndDateTime());
  }

  @Override
  public List<DayOfWeek> visitRecurringUntil(RecurringUntilEvent e) {
    return getDayLettersBetween(e.getStartDateTime(), e.getEndDateTime());
  }

  /**
   * Returns a list of {@link DayOfWeek}s between the start and end date (inclusive).
   *
   * @param start the starting date-time
   * @param end   the ending date-time
   * @return a list of days of the week the event spans
   */
  public static List<DayOfWeek> getDayLettersBetween(LocalDateTime start, LocalDateTime end) {
    List<DayOfWeek> days = new ArrayList<>();
    for (LocalDateTime date = start; !date.isAfter(end); date = date.plusDays(1)) {
      days.add(date.getDayOfWeek());
    }
    return days;
  }
}
