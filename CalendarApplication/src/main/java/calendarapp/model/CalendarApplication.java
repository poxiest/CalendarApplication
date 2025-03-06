package calendarapp.model;

import java.util.List;
import java.util.TreeSet;

public class CalendarApplication implements ICalendarApplication {

  private TreeSet<IEvent> data = new TreeSet<>();

  @Override
  public void createEvent() {
    IEvent newEvent = new Event.Builder()
        .from(from)
        .build();

  }

  @Override
  public void editEvent() {

  }

  @Override
  public List<IEvent> print() {
    return List.of();
  }

  @Override
  public void export() {

  }

  @Override
  public void show() {

  }
}
