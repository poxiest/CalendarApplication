package calendarapp.controller.impl;

import java.util.List;

import calendarapp.controller.Features;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.model.dto.PrintEventsResponseDTO;
import calendarapp.view.GUIView;

public class GUIController implements Features {

  /**
   * The calendar model that stores and manages calendar data.
   */
  private final ICalendarModel model;

  /**
   * The view used for displaying information to the user.
   */
  private GUIView view;

  public GUIController(ICalendarModel model) {
    this.model = model;
  }

  @Override
  public void setView(GUIView view) {
    this.view = view;
    view.addFeatures(this);
  }

  @Override
  public void createEvent(String eventName, String startTime, String endTime, String recurringDays,
                          String occurrenceCount, String recurrenceEndDate, String description,
                          String location, String visibility, boolean autoDecline) {
    try {
      model.createEvent(eventName, startTime, endTime, recurringDays, occurrenceCount,
          recurrenceEndDate, description, location, visibility, autoDecline);
      view.showConfirmation("Event created successfully.");

      // Refresh the view with updated events - assuming a method to get current view date range
      // will be added to the model later
      refreshEvents("day"); // Default to day view refresh
    } catch (EventConflictException e) {
      view.showError("Event conflicts with existing event: " + e.getMessage());
    } catch (Exception e) {
      view.showError("Error creating event: " + e.getMessage());
    }
  }

  @Override
  public void editEvent(String eventName, String startTime, String endTime, String property, String value) {
    try {
      model.editEvent(eventName, startTime, endTime, property, value);
      view.showConfirmation("Event updated successfully.");
      refreshEvents("day"); // Refresh current view
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
      refreshEvents("day"); // Refresh with current view type
    } catch (Exception e) {
      view.showError("Error setting active calendar: " + e.getMessage());
    }
  }

  @Override
  public void copyEvent(CopyEventRequestDTO copyRequest) {
    try {
      model.copyEvent(copyRequest);
      view.showConfirmation("Event copied successfully.");
      refreshEvents("day"); // Refresh current view
    } catch (Exception e) {
      view.showError("Error copying event: " + e.getMessage());
    }
  }

  @Override
  public void loadEvents(String startDate, String endDate, String viewType) {
    try {
      // The "on" parameter determines if we're looking at a specific day or a range
      String on = "range";
      if (startDate.equals(endDate)) {
        on = "day";
      }

      List<PrintEventsResponseDTO> events = model.getEventsForPrinting(startDate, endDate, on);
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

  /**
   * Helper method to refresh calendar list.
   * This would use a model method that will be added later.
   */
  private void refreshCalendarList() {
    // Placeholder - would call a model method to get calendar names
    // List<String> calendarNames = model.getCalendarNames();
    // view.updateCalendarList(calendarNames);
  }

  /**
   * Helper method to refresh events based on current view.
   *
   * @param viewType The current view type (day, week, month)
   */
  private void refreshEvents(String viewType) {
    // This is a placeholder implementation that would be replaced
    // once the model provides methods to get current date range

    // For now, we'll just assume we're looking at today's events
    String today = getCurrentDateString();

    if ("day".equals(viewType)) {
      loadEvents(today, today, viewType);
    } else if ("week".equals(viewType)) {
      // Get a week's worth of events - this would be replaced with proper date calculations
      loadEvents(today, today, viewType); // Placeholder
    } else if ("month".equals(viewType)) {
      // Get a month's worth of events - this would be replaced with proper date calculations
      loadEvents(today, today, viewType); // Placeholder
    }
  }

  /**
   * Helper method to get current date as string.
   * This is a placeholder that would be replaced with actual date formatting.
   *
   * @return Current date in MM-dd-yyyy format
   */
  private String getCurrentDateString() {
    // Placeholder - would use proper date formatting
    return "04-04-2025"; // Example hardcoded date
  }
}
