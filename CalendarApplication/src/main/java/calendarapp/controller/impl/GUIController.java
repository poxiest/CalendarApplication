package calendarapp.controller.impl;

import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import calendarapp.controller.Constants;
import calendarapp.controller.Features;
import calendarapp.controller.ICalendarExporter;
import calendarapp.controller.ICalendarImporter;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.CalendarImporterDTO;
import calendarapp.model.dto.EditEventRequestDTO;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.model.impl.EditEventHelper;
import calendarapp.view.GUIView;

import static calendarapp.controller.Constants.EXPORTER_MAP;
import static calendarapp.controller.Constants.IMPORTER_MAP;
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

/**
 * Controller implementation for handling user interactions in the GUI-based calendar application.
 * Connects the view and the model, managing actions such as creating events, navigating dates,
 * exporting and importing calendars.
 */
public class GUIController implements Features {

  private final ICalendarModel model;
  private GUIView view;

  /**
   * Constructs the controller with the given calendar model.
   *
   * @param model the calendar model to interact with
   */
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
      if (results != null) {
        model.createEvent(
            results.get(calendarapp.model.Constants.PropertyKeys.NAME),
            results.get(calendarapp.model.Constants.PropertyKeys.START_TIME),
            results.get(calendarapp.model.Constants.PropertyKeys.END_TIME),
            results.get(calendarapp.model.Constants.PropertyKeys.RECURRING_DAYS),
            results.get(calendarapp.model.Constants.PropertyKeys.OCCURRENCE_COUNT),
            results.get(calendarapp.model.Constants.PropertyKeys.RECURRENCE_END_DATE),
            results.get(calendarapp.model.Constants.PropertyKeys.DESCRIPTION),
            results.get(calendarapp.model.Constants.PropertyKeys.LOCATION),
            results.get(calendarapp.model.Constants.PropertyKeys.VISIBILITY), true);
        view.showConfirmation("Event created successfully.");
        loadCurrentMonthEvents();
      }
    } catch (EventConflictException e) {
      handleError("Event conflicts with existing event: " + e.getMessage(), e);
    } catch (Exception e) {
      handleError("Error creating event: " + e.getMessage(), e);
    }
  }

  @Override
  public void editEvent(EventsResponseDTO event) {
    try {
      Map<String, String> result = view.showEditEventForm(event);
      if (result != null) {
        Map<String, String> updatedValues = EditEventHelper.getEditEventChanges(event, result);
        boolean isRecurring = Boolean.parseBoolean(result.getOrDefault(IS_MULTIPLE,
            String.valueOf(false)));
        for (Map.Entry<String, String> entry : updatedValues.entrySet()) {
          model.editEvent(
              EditEventRequestDTO.builder()
                  .eventName(event.getEventName())
                  .startTime(event.getStartTime().toString())
                  .endTime(event.getEndTime().toString())
                  .propertyName(entry.getKey())
                  .propertyValue(entry.getValue())
                  .isRecurring(isRecurring)
                  .build());
        }
        view.showConfirmation("Event updated successfully.");
        loadCurrentMonthEvents();
      }
    } catch (EventConflictException e) {
      handleError("Event update conflicts with existing event: " + e.getMessage(), e);
    } catch (Exception e) {
      handleError("Error updating event: " + e.getMessage(), e);
    }
  }

  @Override
  public void createCalendar() {
    try {
      Map<String, String> results = view.showCreateCalendarForm();
      if (results != null) {
        model.createCalendar(results.get(CALENDAR_NAME), results.get(CALENDAR_TIME_ZONE));
        view.showConfirmation("Calendar created successfully.");
        refreshCalendarList();
        ;
      }
    } catch (Exception e) {
      handleError("Error creating calendar: " + e.getMessage(), e);
    }
  }

  @Override
  public void setActiveCalendar(String calendarName) {
    try {
      model.setCalendar(calendarName);
      view.setActiveCalendar(calendarName);
      loadCurrentMonthEvents();
    } catch (Exception e) {
      handleError("Error setting active calendar: " + e.getMessage(), e);
    }
  }

  @Override
  public void loadEvents(String startDate, String endDate, String on) {
    try {
      List<EventsResponseDTO> events = model.getEvents(null, startDate, endDate, on);
      view.updateEvents(events);
    } catch (Exception e) {
      handleError("Error loading events: " + e.getMessage(), e);
    }
  }

  @Override
  public void navigateToPrevious() {
    view.navigateToPrevious(view.getCurrentDate().minusMonths(1));
    loadCurrentMonthEvents();
  }

  @Override
  public void navigateToNext() {
    view.navigateToNext(view.getCurrentDate().plusMonths(1));
    loadCurrentMonthEvents();
  }

  @Override
  public void findEvents() {
    try {
      Map<String, String> results = view.findEvents();
      if (results != null) {
        List<EventsResponseDTO> events = model.getEvents(
            results.get(FIND_EVENT_NAME),
            results.get(FIND_START_TIME),
            results.get(FIND_END_TIME),
            results.get(FIND_ON));
        view.updateEvents(events);
        ;
      }
    } catch (Exception e) {
      handleError("Error finding events: " + e.getMessage(), e);
    }
  }

  @Override
  public void exportCalendar() {
    try {
      Map<String, String> results = view.showExportCalendarForm();
      if (results != null) {
        ICalendarExporter exporter = EXPORTER_MAP.get(results.get(EXPORT_FILE_EXTENSION));
        String filePath = exporter.export(model.getEventsForExport(),
            results.get(EXPORT_FILE_NAME) + "."
                + results.get(EXPORT_FILE_EXTENSION));
        view.showConfirmation("Calendar exported successfully at: " + filePath);
        loadCurrentMonthEvents();
        ;
      }
    } catch (Exception e) {
      handleError("Error exporting calendar: " + e.getMessage(), e);
    }
  }

  @Override
  public void importCalendar() {
    try {
      Map<String, String> results = view.showImportCalendarDialog();
      if (results != null) {
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
        loadCurrentMonthEvents();
      }
    } catch (Exception e) {
      handleError("Error importing calendar: " + e.getMessage(), e);
    }
  }

  /**
   * Updates the calendar list in the view with the latest calendars from the model.
   */
  private void refreshCalendarList() {
    view.updateCalendarList(model.getCalendars());
  }

  /**
   * Loads and displays all events for the current month in the view.
   */
  private void loadCurrentMonthEvents() {
    String startOfMonth = view.getCurrentDate()
        .with(TemporalAdjusters.firstDayOfMonth())
        .atStartOfDay().toString();
    String startOfNextMonth = view.getCurrentDate()
        .with(TemporalAdjusters.firstDayOfNextMonth())
        .atStartOfDay().toString();
    loadEvents(startOfMonth, startOfNextMonth, null);
  }

  /**
   * Handles exceptions by logging the error and showing an error message in the view.
   *
   * @param message the error message to display
   * @param e       the exception to log
   */
  private void handleError(String message, Exception e) {
    view.showError(message);
  }
}
