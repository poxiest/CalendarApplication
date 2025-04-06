package calendarapp.controller.impl;

import java.util.List;
import java.util.Map;

import calendarapp.controller.Features;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.view.GUIView;

import static calendarapp.utils.Constants.CALENDAR_NAME;
import static calendarapp.utils.Constants.CALENDAR_TIME_ZONE;
import static calendarapp.utils.Constants.EVENT_END_DATE;
import static calendarapp.utils.Constants.EVENT_LOCATION;
import static calendarapp.utils.Constants.EVENT_NAME;
import static calendarapp.utils.Constants.EVENT_START_DATE;

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
  public void createEvent() {
    try {
      Map<String, String> results = view.showCreateEventForm();
      model.createEvent(results.get(EVENT_NAME), results.get(EVENT_START_DATE), results.get(EVENT_END_DATE), null, null,
          null, null, results.get(EVENT_LOCATION), null, true);
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
  public void createCalendar() {
    try {
      Map<String, String> results = view.showCreateCalendarForm();
      model.createCalendar(results.get(CALENDAR_NAME), results.get(CALENDAR_TIME_ZONE));
      view.showConfirmation("Calendar created successfully.");
      refreshCalendarList();
    } catch (Exception e) {
      view.showError("Error creating calendar: " + e.getMessage());
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
  public void loadEvents(String startDate, String endDate) {
    try {
      List<EventsResponseDTO> events = model.getEvents(null, null, null, startDate);
      view.updateEvents(events);
    } catch (Exception e) {
      view.showError("Error loading events: " + e.getMessage());
    }
  }

  @Override
  public void navigateToPrevious() {
    view.navigateToPrevious();
    String startDate = view.getCurrentDate().format(java.time.format.DateTimeFormatter.ofPattern(
        "yyyy-MM-dd"));
    String endDate = view.getCurrentDate().plusMonths(1).minusDays(1)
        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    loadEvents(startDate, endDate);
  }

  @Override
  public void navigateToNext() {
    view.navigateToNext();
    String startDate = view.getCurrentDate().format(java.time.format.DateTimeFormatter.ofPattern(
        "yyyy-MM-dd"));
    String endDate = view.getCurrentDate().plusMonths(1).minusDays(1)
        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    loadEvents(startDate, endDate);
  }

  @Override
  public void showCreateEventForm() {
    view.showCreateEventForm();
  }

  @Override
  public void showEditEventForm(EventsResponseDTO event) {
    view.showEditEventForm(event);
  }

  @Override
  public void findEvents() {

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
}
