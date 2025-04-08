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
import static calendarapp.utils.Constants.EVENT_DESCRIPTION;
import static calendarapp.utils.Constants.EVENT_END_DATE;
import static calendarapp.utils.Constants.EVENT_LOCATION;
import static calendarapp.utils.Constants.EVENT_NAME;
import static calendarapp.utils.Constants.EVENT_RECURRING_COUNT;
import static calendarapp.utils.Constants.EVENT_RECURRING_DAYS;
import static calendarapp.utils.Constants.EVENT_RECURRING_END_DATE;
import static calendarapp.utils.Constants.EVENT_START_DATE;
import static calendarapp.utils.Constants.EVENT_VISIBILITY;
import static calendarapp.utils.Constants.FIND_END_TIME;
import static calendarapp.utils.Constants.FIND_EVENT_NAME;
import static calendarapp.utils.Constants.FIND_ON;
import static calendarapp.utils.Constants.FIND_START_TIME;

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
    setActiveCalendar(model.getCalendars().get(0));
  }

  @Override
  public void createEvent() {
    try {
      Map<String, String> results = view.showCreateEventForm();
      if (results == null) {
        return;
      }
      model.createEvent(results.get(EVENT_NAME), results.get(EVENT_START_DATE),
          results.get(EVENT_END_DATE), results.get(EVENT_RECURRING_DAYS),
          results.get(EVENT_RECURRING_COUNT), results.get(EVENT_RECURRING_END_DATE),
          results.get(EVENT_DESCRIPTION), results.get(EVENT_LOCATION),
          results.get(EVENT_VISIBILITY), true);
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
      if (results == null) {
        return;
      }
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
  public void loadEvents(String startDate, String endDate, String on) {
    try {
      List<EventsResponseDTO> events = model.getEvents(null, startDate, endDate, on);
      view.updateEvents(events);
    } catch (Exception e) {
      view.showError("Error loading events: " + e.getMessage());
    }
  }

  @Override
  public void navigateToPrevious() {
    view.navigateToPrevious(view.getCurrentDate().minusMonths(1));
    String startDate = view.getCurrentDate().format(java.time.format.DateTimeFormatter.ofPattern(
        "yyyy-MM-dd"));
    String endDate = view.getCurrentDate().plusMonths(1).minusDays(1)
        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    loadEvents(startDate, endDate, null);
  }

  @Override
  public void navigateToNext() {
    view.navigateToNext(view.getCurrentDate().plusMonths(1));
    String startDate = view.getCurrentDate().format(java.time.format.DateTimeFormatter.ofPattern(
        "yyyy-MM-dd"));
    String endDate = view.getCurrentDate().plusMonths(1).minusDays(1)
        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    loadEvents(startDate, endDate, null);
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
    try {
      Map<String, String> results = view.findEvents();
      if (results == null) {
        return;
      }
      List<EventsResponseDTO> events = model.getEvents(results.get(FIND_EVENT_NAME),
          results.get(FIND_START_TIME), results.get(FIND_END_TIME), results.get(FIND_ON));
      view.updateEvents(events);
    } catch (Exception e) {
      view.showError("Error creating calendar: " + e.getMessage());
    }
  }

  private void refreshCalendarList() {
    view.updateCalendarList(model.getCalendars());
  }

  private void refreshEvents() {
    String today = getCurrentDateString();
    loadEvents(null, null, today);
  }

  private String getCurrentDateString() {
    return view.getCurrentDate().toString();
  }
}
