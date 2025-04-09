package calendarapp.controller;

import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.view.GUIView;

public interface Features {
  void setView(GUIView view);

  void createEvent();

  void editEvent(EventsResponseDTO eventDTO);

  void createCalendar();

  void setActiveCalendar(String calendarName);

  void loadEvents(String startDate, String endDate, String on);

  void navigateToPrevious();

  void navigateToNext();

  void findEvents();

  void exportCalendar();

  void importCalendar();
}
