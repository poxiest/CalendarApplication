package calendarapp.view;

import java.util.List;

import calendarapp.model.event.IEvent;

public interface ICalendarView {

  void displayMessage(String message);

  void displayEvents(List<IEvent> events);
}
