import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import calendarapp.controller.Features;
import calendarapp.model.dto.CalendarResponseDTO;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.view.GUIView;

/**
 * Mock implementation of the GUIView interface for testing the calendar application.
 * This class records every method call in a log, allows preset responses for input forms,
 * and stores messages or values for verification.
 */
public class MockGUIView implements GUIView {

  private final StringBuilder log;
  private final List<List<EventsResponseDTO>> updateEventsCalls = new ArrayList<>();
  private final List<List<CalendarResponseDTO>> updateCalendarListCalls = new ArrayList<>();
  private Map<String, String> createEventResponse;
  private Map<String, String> editEventFormResponse;
  private Map<String, String> createCalendarFormResponse;
  private Map<String, String> findEventsResponse;
  private Map<String, String> exportCalendarFormResponse;
  private Map<String, String> importCalendarDialogResponse;
  private LocalDate currentDate = LocalDate.of(2025, 11, 11);

  public MockGUIView(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void addFeatures(Features features) {
    log.append("addFeatures called\n");
  }

  @Override
  public void updateEvents(List<EventsResponseDTO> events) {
    log.append("updateEvents called with: ");
    for (EventsResponseDTO event : events) {
      log.append(event.getEventName()).append(" ").append(event.getStartTime()).append(" ")
          .append(event.getEndTime()).append(" ").append(event.getLocation()).append(" ")
          .append(event.getDescription()).append(" ").append(event.getRecurringDays())
          .append(" ").append(event.getOccurrenceCount()).append(" ")
          .append(event.getRecurrenceEndDate()).append(" ")
          .append(event.getVisibility()).append("\n");
    }
    updateEventsCalls.add(events);
  }

  @Override
  public void updateCalendarList(List<CalendarResponseDTO> calendarNames) {
    log.append("updateCalendarList called with: ");
    for (CalendarResponseDTO calendar : calendarNames) {
      log.append(calendar.getName()).append(" ").append(calendar.getZoneId()).append("\n");
    }
    updateCalendarListCalls.add(calendarNames);
  }

  // Setter methods to simulate user input.
  public void setCreateEventResponse(Map<String, String> createEventResponse) {
    this.createEventResponse = createEventResponse;
  }

  public void setEditEventFormResponse(Map<String, String> editEventFormResponse) {
    this.editEventFormResponse = editEventFormResponse;
  }

  public void setCreateCalendarFormResponse(Map<String, String> createCalendarFormResponse) {
    this.createCalendarFormResponse = createCalendarFormResponse;
  }

  public void setFindEventsResponse(Map<String, String> findEventsResponse) {
    this.findEventsResponse = findEventsResponse;
  }

  public void setExportCalendarFormResponse(Map<String, String> exportCalendarFormResponse) {
    this.exportCalendarFormResponse = exportCalendarFormResponse;
  }

  public void setImportCalendarDialogResponse(Map<String, String> importCalendarDialogResponse) {
    this.importCalendarDialogResponse = importCalendarDialogResponse;
  }

  @Override
  public void setActiveCalendar(String calendarName) {
    log.append("setActiveCalendar called with: ").append(calendarName).append("\n");
  }

  @Override
  public void showConfirmation(String message) {
    log.append("showConfirmation called with message: ").append(message).append("\n");
  }

  @Override
  public void showError(String errorMessage) {
    log.append("showError called with message: ").append(errorMessage).append("\n");
  }

  @Override
  public Map<String, String> showCreateEventForm() {
    log.append("showCreateEventForm called\n");
    return createEventResponse;
  }

  @Override
  public Map<String, String> showEditEventForm(EventsResponseDTO event) {
    log.append("showEditEventForm called for event: ").append(event.getEventName())
        .append(" ").append(event.getStartTime()).append(" ")
        .append(event.getEndTime()).append(" ").append(event.getLocation()).append(" ")
        .append(event.getDescription()).append(" ").append(event.getRecurringDays())
        .append(" ").append(event.getOccurrenceCount()).append(" ")
        .append(event.getRecurrenceEndDate()).append(" ")
        .append(event.getVisibility()).append("\n");
    return editEventFormResponse;
  }

  @Override
  public Map<String, String> showCreateCalendarForm() {
    log.append("showCreateCalendarForm called\n");
    return createCalendarFormResponse;
  }

  @Override
  public LocalDate getCurrentDate() {
    log.append("getCurrentDate called, returning: ").append(currentDate.toString()).append("\n");
    return currentDate;
  }

  @Override
  public void navigateToPrevious(LocalDate date) {
    log.append("navigateToPrevious called with date: ").append(date.toString()).append("\n");
  }

  @Override
  public void navigateToNext(LocalDate date) {
    log.append("navigateToNext called with date: ").append(date.toString()).append("\n");
  }

  @Override
  public Map<String, String> findEvents() {
    log.append("findEvents called\n");
    return findEventsResponse;
  }

  @Override
  public Map<String, String> showExportCalendarForm() {
    log.append("showExportCalendarForm called\n");
    return exportCalendarFormResponse;
  }

  @Override
  public Map<String, String> showImportCalendarDialog() {
    log.append("showImportCalendarDialog called\n");
    return importCalendarDialogResponse;
  }

  public void setCurrentDate(LocalDate currentDate) {
    this.currentDate = currentDate;
  }
}
