package calendarapp.view;

import java.time.LocalDate;
import java.util.List;

import calendarapp.controller.Features;
import calendarapp.model.dto.PrintEventsResponseDTO;

public interface GUIView {

  void addFeatures(Features features);

  void updateEvents(List<PrintEventsResponseDTO> events);

  void updateCalendarList(List<String> calendarNames);

  void setActiveCalendar(String calendarName);

  void showConfirmation(String message);

  void showError(String errorMessage);

  void showStatus(String dateTime, String status);

  void showCreateEventForm();

  void showEditEventForm(PrintEventsResponseDTO event);

  void showCreateCalendarForm();

  LocalDate getCurrentDate();

  void navigateToPrevious();

  void navigateToNext();
}
