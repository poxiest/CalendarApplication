package calendarapp.controller.impl;

import java.util.List;
import java.util.Map;

import calendarapp.controller.Features;
import calendarapp.controller.ICalendarExporter;
import calendarapp.controller.ICalendarImporter;
import calendarapp.controller.exporter.Constants;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.CalendarImporterDTO;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.view.GUIView;

import static calendarapp.controller.exporter.Constants.EXPORTER_MAP;
import static calendarapp.controller.importer.Constants.IMPORTER_MAP;
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
import static calendarapp.utils.Constants.EXPORT_FILE_EXTENSION;
import static calendarapp.utils.Constants.EXPORT_FILE_NAME;
import static calendarapp.utils.Constants.IMPORT_FILE_PATH;
import static calendarapp.utils.FileUtil.getFileExtension;

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

  @Override
  public void exportCalendar() {
    try {
      Map<String, String> results = view.showExportCalendarForm();
      if (results == null) {
        return;
      }
      ICalendarExporter exporter = EXPORTER_MAP.get(results.get(EXPORT_FILE_EXTENSION));
      exporter.export(model.getEventsForExport(), results.get(EXPORT_FILE_NAME) + "."
          + results.get(EXPORT_FILE_EXTENSION));
      view.showConfirmation("Calendar exported successfully.");
      refreshEvents();
    } catch (Exception e) {
      view.showError("Error exporting calendar: " + e.getMessage());
    }
  }

  @Override
  public void importCalendar() {
    try {
      Map<String, String> results = view.showImportCalendarDialog();
      if (results == null) {
        return;
      }

      String fileExtension = getFileExtension(results.get(IMPORT_FILE_PATH));
      if (!Constants.SupportExportFormats.SUPPORTED_EXPORT_FORMATS.contains(fileExtension)) {
        throw new IllegalArgumentException("Unsupported export format: " + fileExtension
            + ". Supported formats are: "
            + Constants.SupportExportFormats.SUPPORTED_EXPORT_FORMATS);
      }
      ICalendarImporter importer = IMPORTER_MAP.get(fileExtension);
      List<CalendarImporterDTO> importedEvents = importer.importEvents(results.get(IMPORT_FILE_PATH));
      for (CalendarImporterDTO importedEvent : importedEvents) {
        model.createEvent(importedEvent.getEventName(), importedEvent.getStartTime(), importedEvent.getEndTime(),
            null, null, null, importedEvent.getDescription(),
            importedEvent.getLocation(), importedEvent.getVisibility(), true);
      }
      view.showConfirmation("Calendar imported successfully.");
      refreshEvents();
    } catch (Exception e) {
      view.showError("Error importing calendar: " + e.getMessage());
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
}
