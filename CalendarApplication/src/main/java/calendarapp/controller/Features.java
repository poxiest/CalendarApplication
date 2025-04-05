package calendarapp.controller;

import java.util.List;

import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.model.dto.PrintEventsResponseDTO;
import calendarapp.view.GUIView;

public interface Features {

  // Called by the view so that the controller can register itself with the view.
  void setView(GUIView view);

  // Event-related operations
  void createEvent(String eventName, String startTime, String endTime, String recurringDays,
                   String occurrenceCount, String recurrenceEndDate, String description,
                   String location, String visibility, boolean autoDecline);

  void editEvent(String eventName, String startTime, String endTime, String property, String value);

  // Calendar-related operations
  void createCalendar(String calendarName, String timezone);

  void editCalendar(String calendarName, String propertyName, String propertyValue);

  void setActiveCalendar(String calendarName);

  void copyEvent(CopyEventRequestDTO copyRequest);

  // Loading and status
  void loadEvents(String startDate, String endDate, String viewType);

  void checkStatus(String dateTime);

  void navigateToPrevious();

  void navigateToNext();

  // Dialogs – these are delegated to the view so that the appropriate forms appear.
  void showCreateEventForm();

  void showEditEventForm(PrintEventsResponseDTO event);

  void showCreateCalendarForm();

  List<String> getCalendars();
}
