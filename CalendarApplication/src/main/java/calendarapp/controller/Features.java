package calendarapp.controller;

import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.view.GUIView;

public interface Features {
  void setView(GUIView view);

  void createEvent();

  void editEvent(String eventName, String startTime, String endTime, String property, String value);

  void createCalendar();

  void setActiveCalendar(String calendarName);

  void loadEvents(String startDate, String endDate);

  void navigateToPrevious();

  void navigateToNext();

  void showCreateEventForm();

  void showEditEventForm(EventsResponseDTO event);

  void findEvents();

  void exportCalendar();

  void importCalendar();
}
