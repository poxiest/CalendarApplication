package calendarapp.model;

import java.util.List;

public interface ICalendarApplication {

  void createEvent();

  void editEvent();

  List<IEvent> print();

  void export();

  void show();

}
