package calendarapp.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import calendarapp.controller.Features;
import calendarapp.model.dto.EventsResponseDTO;

public interface GUIView {

  void addFeatures(Features features);

  void updateEvents(List<EventsResponseDTO> events);

  void updateCalendarList(List<String> calendarNames);

  void setActiveCalendar(String calendarName);

  void showConfirmation(String message);

  void showError(String errorMessage);

  Map<String, String> showCreateEventForm();

  void showEditEventForm(EventsResponseDTO event);

  Map<String, String> showCreateCalendarForm();

  LocalDate getCurrentDate();

  void navigateToPrevious();

  void navigateToNext();

  void findEvents();
}
