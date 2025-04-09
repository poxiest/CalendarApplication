package calendarapp.controller.impl;

import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import calendarapp.controller.Features;
import calendarapp.controller.ICalendarExporter;
import calendarapp.controller.ICalendarImporter;
import calendarapp.controller.exporter.Constants;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.CalendarImporterDTO;
import calendarapp.model.dto.EditEventRequestDTO;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.view.GUIView;

import static calendarapp.controller.exporter.Constants.EXPORTER_MAP;
import static calendarapp.controller.importer.Constants.IMPORTER_MAP;
import static calendarapp.utils.Constants.CALENDAR_NAME;
import static calendarapp.utils.Constants.CALENDAR_TIME_ZONE;
import static calendarapp.utils.Constants.EXPORT_FILE_EXTENSION;
import static calendarapp.utils.Constants.EXPORT_FILE_NAME;
import static calendarapp.utils.Constants.FIND_END_TIME;
import static calendarapp.utils.Constants.FIND_EVENT_NAME;
import static calendarapp.utils.Constants.FIND_ON;
import static calendarapp.utils.Constants.FIND_START_TIME;
import static calendarapp.utils.Constants.IMPORT_FILE_PATH;
import static calendarapp.utils.Constants.IS_MULTIPLE;
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
    setActiveCalendar(model.getCalendars().get(0).getName());
  }

  @Override
  public void createEvent() {
    try {
      Map<String, String> results = view.showCreateEventForm();
      if (results == null) {
        return;
      }
      model.createEvent(results.get(calendarapp.model.impl.Constants.PropertyKeys.NAME),
          results.get(calendarapp.model.impl.Constants.PropertyKeys.START_TIME),
          results.get(calendarapp.model.impl.Constants.PropertyKeys.END_TIME),
          results.get(calendarapp.model.impl.Constants.PropertyKeys.RECURRING_DAYS),
          results.get(calendarapp.model.impl.Constants.PropertyKeys.OCCURRENCE_COUNT),
          results.get(calendarapp.model.impl.Constants.PropertyKeys.RECURRENCE_END_DATE),
          results.get(calendarapp.model.impl.Constants.PropertyKeys.DESCRIPTION),
          results.get(calendarapp.model.impl.Constants.PropertyKeys.LOCATION),
          results.get(calendarapp.model.impl.Constants.PropertyKeys.VISIBILITY), true);
      view.showConfirmation("Event created successfully.");
      refreshEvents();
    } catch (EventConflictException e) {
      view.showError("Event conflicts with existing event: " + e.getMessage());
    } catch (Exception e) {
      view.showError("Error creating event: " + e.getMessage());
    }
  }

  @Override
  public void editEvent(EventsResponseDTO eventName) {
    try {
      Map<String, String> result = view.showEditEventForm(eventName);
      if (result == null) {
        return;
      }
      Map<String, String> updatedValues = EditEventHelper.getEditEventChanges(eventName, result);
      for (Map.Entry<String, String> entry : updatedValues.entrySet()) {
        model.editEvent(
            EditEventRequestDTO.builder().eventName(eventName.getEventName())
                .startTime(eventName.getStartTime().toString())
                .endTime(eventName.getEndTime().toString())
                .propertyName(entry.getKey())
                .propertyValue(entry.getValue())
                .isRecurring(Boolean.parseBoolean(result.getOrDefault(IS_MULTIPLE,
                    String.valueOf(false))))
                .build());
      }
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
    refreshEvents();
  }

  @Override
  public void navigateToNext() {
    view.navigateToNext(view.getCurrentDate().plusMonths(1));
    refreshEvents();
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
      view.showError("Error Finding events: " + e.getMessage());
    }
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
      List<CalendarImporterDTO> importedEvents =
          importer.importEvents(results.get(IMPORT_FILE_PATH));
      for (CalendarImporterDTO importedEvent : importedEvents) {
        model.createEvent(importedEvent.getEventName(), importedEvent.getStartTime(),
            importedEvent.getEndTime(), null, null, null,
            importedEvent.getDescription(), importedEvent.getLocation(),
            importedEvent.getVisibility(), true);
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
    loadEvents(view.getCurrentDate().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay().toString(),
        view.getCurrentDate().with(TemporalAdjusters.firstDayOfNextMonth()).atStartOfDay().toString(), null);
  }
}
