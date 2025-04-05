package calendarapp.controller.impl;

import java.util.List;

import calendarapp.controller.Features;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.model.dto.PrintEventsResponseDTO;
import calendarapp.view.GUIView;

public class GUIController implements Features {

  private final ICalendarModel model;
  private GUIView view;

  public GUIController(ICalendarModel model) {
    this.model = model;
  }

  @Override
  public void setView(GUIView view) {
    this.view = view;
    view.addFeatures(this);
    view.updateCalendarList(model.getCalendars());
  }

  @Override
  public void createEvent(String eventName, String startTime, String endTime, String recurringDays,
                          String occurrenceCount, String recurrenceEndDate, String description,
                          String location, String visibility, boolean autoDecline) {
    try {
      model.createEvent(eventName, startTime, endTime, recurringDays, occurrenceCount,
          recurrenceEndDate, description, location, visibility, autoDecline);
      view.showConfirmation("Event created successfully.");
      refreshEvents();
    } catch (EventConflictException e) {
      view.showError("Event conflicts with existing event: " + e.getMessage());
    } catch (Exception e) {
      view.showError("Error creating event: " + e.getMessage());
    }
  }

  @Override
  public void editEvent(String eventName, String startTime, String endTime, String property,
                        String value) {
    try {
      model.editEvent(eventName, startTime, endTime, property, value);
      view.showConfirmation("Event updated successfully.");
      refreshEvents();
    } catch (EventConflictException e) {
      view.showError("Event update conflicts with existing event: " + e.getMessage());
    } catch (Exception e) {
      view.showError("Error updating event: " + e.getMessage());
    }
  }

  @Override
  public void createCalendar(String calendarName, String timezone) {
    try {
      model.createCalendar(calendarName, timezone);
      view.showConfirmation("Calendar created successfully.");
      refreshCalendarList();
    } catch (Exception e) {
      view.showError("Error creating calendar: " + e.getMessage());
    }
  }

  @Override
  public void editCalendar(String calendarName, String propertyName, String propertyValue) {
    try {
      model.editCalendar(calendarName, propertyName, propertyValue);
      view.showConfirmation("Calendar updated successfully.");
      refreshCalendarList();
    } catch (Exception e) {
      view.showError("Error updating calendar: " + e.getMessage());
    }
  }

  @Override
  public void setActiveCalendar(String calendarName) {
    try {
      model.setCalendar(calendarName);
      view.setActiveCalendar(calendarName);
      refreshEvents();
    } catch (Exception e) {
      view.showError("Error setting active calendar: " + e.getMessage());
    }
  }

  @Override
  public void copyEvent(CopyEventRequestDTO copyRequest) {
    try {
      model.copyEvent(copyRequest);
      view.showConfirmation("Event copied successfully.");
      refreshEvents();
    } catch (Exception e) {
      view.showError("Error copying event: " + e.getMessage());
    }
  }

  @Override
  public void loadEvents(String startDate, String endDate) {
    try {
      List<PrintEventsResponseDTO> events = model.getEventsForPrinting(null, null, startDate);
      view.updateEvents(events);
    } catch (Exception e) {
      view.showError("Error loading events: " + e.getMessage());
    }
  }

  @Override
  public void checkStatus(String dateTime) {
    try {
      String status = model.showStatus(dateTime);
      view.showStatus(dateTime, status);
    } catch (Exception e) {
      view.showError("Error checking status: " + e.getMessage());
    }
  }

  private void refreshCalendarList() {
     view.updateCalendarList(model.getCalendars());
  }

  private void refreshEvents() {
    String today = getCurrentDateString();
    loadEvents(today, today);
  }

  private String getCurrentDateString() {
    return view.getCurrentDate().toString();
  }

  @Override
  public void navigateToPrevious() {
    view.navigateToPrevious();
    String startDate = view.getCurrentDate().format(java.time.format.DateTimeFormatter.ofPattern(
        "MM-dd-yyyy"));
    String endDate = view.getCurrentDate().plusMonths(1).minusDays(1)
        .format(java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    loadEvents(startDate, endDate);
  }

  @Override
  public void navigateToNext() {
    view.navigateToNext();
    String startDate = view.getCurrentDate().format(java.time.format.DateTimeFormatter.ofPattern(
        "MM-dd-yyyy"));
    String endDate = view.getCurrentDate().plusMonths(1).minusDays(1)
        .format(java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    loadEvents(startDate, endDate);
  }

  @Override
  public void showCreateEventForm() {
    view.showCreateEventForm();
  }

  @Override
  public void showEditEventForm(PrintEventsResponseDTO event) {
    view.showEditEventForm(event);
  }

  @Override
  public void showCreateCalendarForm() {
    view.showCreateCalendarForm();
  }

  @Override
  public List<String> getCalendars() {
    return model.getCalendars();
  }
}
