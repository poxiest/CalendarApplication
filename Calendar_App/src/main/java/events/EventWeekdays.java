package events;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

  public static List<DayOfWeek> getDayLettersBetween(LocalDateTime start, LocalDateTime end) {
    List<DayOfWeek> days = new ArrayList<>();
    for (LocalDateTime date = start; !date.isAfter(end); date = date.plusDays(1)) {
      days.add(date.getDayOfWeek());
    }
    return days;
  }
}
