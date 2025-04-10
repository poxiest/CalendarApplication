import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calendarapp.controller.Features;
import calendarapp.controller.InvalidCommandException;
import calendarapp.controller.impl.GUIController;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.CalendarExporterDTO;
import calendarapp.model.dto.CalendarResponseDTO;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.model.dto.EditEventRequestDTO;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.utils.TimeUtil;

import static org.junit.Assert.assertEquals;

/**
 * Test for GUI Controller to GUI View and to Model.
 */
public class GUIIntegrationTest {
  private MockModel mockModel;
  private MockGUIView mockView;
  private Features controller;
  private StringBuilder modelLogger;
  private StringBuilder viewLogger;

  @Before
  public void setUp() {
    modelLogger = new StringBuilder();
    viewLogger = new StringBuilder();
    mockModel = new MockModel(modelLogger);
    mockView = new MockGUIView(viewLogger);
    mockView.setCurrentDate(LocalDate.of(2025, 11, 11));
    controller = new GUIController(mockModel);
    controller.setView(mockView);
    controller.setActiveCalendar("PersonalCal");
  }

  @Test
  public void testCreateEvent() throws EventConflictException {
    Map<String, String> eventInput = new HashMap<>();
    eventInput.put("eventname", "TeamMeeting");
    eventInput.put("from", "2025-11-11T10:00");
    eventInput.put("to", "2025-11-11T11:00");
    eventInput.put("occurrence_count", null);
    eventInput.put("recurring_days", null);
    eventInput.put("recurrence_end_date", null);
    eventInput.put("description", "Daily standup meeting");
    eventInput.put("location", "Conference Room");
    eventInput.put("visibility", "public");
    mockView.setCreateEventResponse(eventInput);

    controller.createEvent();

    String expectedModelLog = "Getting calendars\n"
        + "Getting calendars\n"
        + "Using calendar: testCalendar\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Using calendar: PersonalCal\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Creating event\n"
        + "TeamMeeting 2025-11-11T10:00 2025-11-11T11:00 null null null Daily standup meeting"
        + " Conference Room public true\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n";
    String modelLog = modelLogger.toString();
    assertEquals(expectedModelLog, modelLog);

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null "
        + "null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null "
        + "null null null null\n"
        + "showCreateEventForm called\n"
        + "showConfirmation called with message: Event created successfully.\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null "
        + "null null null null\n", viewLogger.toString());
  }

  @Test
  public void testEditEvent() throws EventConflictException {
    EventsResponseDTO dummyEvent =
        EventsResponseDTO.builder().eventName("TeamMeeting").startTime(LocalDateTime.of(2025, 11,
                11, 10, 0)).endTime(LocalDateTime.of(2025, 11, 11, 11, 0))
            .location("Room1").build();

    Map<String, String> editInput = new HashMap<>();
    editInput.put("eventname", "TeamSync");
    editInput.put("isMultiple", "false");
    mockView.setEditEventFormResponse(editInput);
    controller.editEvent(dummyEvent);

    String expectedLogStart = "Getting calendars\n"
        + "Getting calendars\n"
        + "Using calendar: testCalendar\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Using calendar: PersonalCal\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Editing event\n"
        + "TeamMeeting 2025-11-11T10:00 2025-11-11T11:00 eventname TeamSync\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n";
    String modelLog = modelLogger.toString();
    assertEquals(expectedLogStart, modelLog);

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null "
        + "null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null "
        + "null null null null\n"
        + "showEditEventForm called for event: TeamMeeting 2025-11-11T10:00 2025-11-11T11:00 "
        + "Room1 null null null null null\n"
        + "showConfirmation called with message: Event updated successfully.\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null "
        + "null null null null\n", viewLogger.toString());
  }

  @Test
  public void testCreateCalendar() {
    Map<String, String> calInput = new HashMap<>();
    calInput.put("name", "WorkCal");
    calInput.put("timezone", "America/New_York");
    mockView.setCreateCalendarFormResponse(calInput);

    controller.createCalendar();
    String expectedModelLog = "Getting calendars\n"
        + "Getting calendars\n"
        + "Using calendar: testCalendar\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Using calendar: PersonalCal\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Creating calendar\n"
        + "WorkCal America/New_York\n"
        + "Getting calendars\n";
    String modelLog = modelLogger.toString();
    assertEquals(expectedModelLog, modelLog);

    assertEquals("addFeatures called\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
            + "setActiveCalendar called with: testCalendar\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
            + " null null null null null\n"
            + "setActiveCalendar called with: PersonalCal\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation "
            + "null null null null null\n"
            + "showCreateCalendarForm called\n"
            + "showConfirmation called with message: Calendar created successfully.\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n",
        viewLogger.toString());
  }

  @Test
  public void testSetActiveCalendar() {
    controller.setActiveCalendar("PersonalCal");
    String expectedLog = "Getting calendars\n"
        + "Getting calendars\n"
        + "Using calendar: testCalendar\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Using calendar: PersonalCal\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Using calendar: PersonalCal\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n";
    String modelLog = modelLogger.toString();
    assertEquals(expectedLog, modelLog);

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00"
        + " testLocation null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00"
        + " testLocation null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00"
        + " testLocation null null null null null\n", viewLogger.toString());
  }

  @Test
  public void testLoadEvents() {
    controller.loadEvents("2025-11-11T00:00", "2025-11-12T00:00", "2025-11-11");

    String expectedLog = "Getting calendars\n"
        + "Getting calendars\n"
        + "Using calendar: testCalendar\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Using calendar: PersonalCal\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Getting events\n"
        + "null 2025-11-11T00:00 2025-11-12T00:00 2025-11-11\n";
    String modelLog = modelLogger.toString();
    assertEquals(expectedLog, modelLog);

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00"
        + " testLocation null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00"
        + " testLocation null null null null null\n"
        + "updateEvents called with: null 2025-11-11T00:00 2025-11-12T00:00"
        + " testLocation null null null null null\n", viewLogger.toString());
  }

  @Test
  public void testNavigateToPrevious() {
    controller.navigateToPrevious();
    String expectedLog = "Getting calendars\n"
        + "Getting calendars\n"
        + "Using calendar: testCalendar\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Using calendar: PersonalCal\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n";
    assertEquals(expectedLog, modelLogger.toString());

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "navigateToPrevious called with date: 2025-10-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n", viewLogger.toString());
  }


  @Test
  public void testNavigateToNext() {
    controller.navigateToNext();
    String expectedLog = "Getting calendars\n"
        + "Getting calendars\n"
        + "Using calendar: testCalendar\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Using calendar: PersonalCal\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n";
    assertEquals(expectedLog, modelLogger.toString());

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00"
        + " testLocation null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00"
        + " testLocation null null null null null\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "navigateToNext called with date: 2025-12-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00"
        + " testLocation null null null null null\n", viewLogger.toString());
  }

  @Test
  public void testFindEvents() {
    Map<String, String> findInput = new HashMap<>();
    findInput.put("eventName", "Meeting");
    findInput.put("startTime", "2025-11-11T00:00");
    findInput.put("endTime", "2025-11-12T00:00");
    findInput.put("on", "2025-11-11");
    mockView.setFindEventsResponse(findInput);
    controller.findEvents();
    String expectedLog = "Getting calendars\n"
        + "Getting calendars\n"
        + "Using calendar: testCalendar\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Using calendar: PersonalCal\n"
        + "Getting events\n"
        + "null 2025-11-01T00:00 2025-12-01T00:00 null\n"
        + "Getting events\n"
        + "Meeting 2025-11-11T00:00 2025-11-12T00:00 2025-11-11\n";
    assertEquals(expectedLog, modelLogger.toString());

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "findEvents called\n"
        + "updateEvents called with: Meeting 2025-11-11T00:00 2025-11-12T00:00 testLocation"
        + " null null null null null\n", viewLogger.toString());
  }

  @Test
  public void testExportCalendar() {
    Map<String, String> exportInput = new HashMap<>();
    exportInput.put("exportFileName", "Tester");
    exportInput.put("exportFileExtension", "csv");
    mockView.setExportCalendarFormResponse(exportInput);

    controller.exportCalendar();
    String expectedConfirmation =
        "addFeatures called\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
            + "setActiveCalendar called with: testCalendar\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
            + " null null null null null\n"
            + "setActiveCalendar called with: PersonalCal\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
            + " null null null null null\n"
            + "showExportCalendarForm called\n"
            + "showConfirmation called with message: Calendar exported successfully at:"
            + " " + System.getProperty("user.dir") + File.separator + "Tester.csv\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null"
            + " null null null null\n";
    assertEquals(expectedConfirmation, viewLogger.toString());
  }

  @Test
  public void testExportCalendarInvalid() {
    Map<String, String> exportInput = new HashMap<>();
    exportInput.put("exportFileName", "Tester");
    exportInput.put("exportFileExtension", "xsd");
    mockView.setExportCalendarFormResponse(exportInput);

    controller.exportCalendar();
    String expectedConfirmation =
        "addFeatures called\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
            + "setActiveCalendar called with: testCalendar\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
            + " null null null null null\n"
            + "setActiveCalendar called with: PersonalCal\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
            + " null null null null null\n"
            + "showExportCalendarForm called\n"
            + "showError called with message: Error exporting calendar: null\n";
    assertEquals(expectedConfirmation, viewLogger.toString());
  }

  @Test
  public void testImportCalendar() {
    Map<String, String> importInput = new HashMap<>();
    importInput.put("importFilePath", System.getProperty("user.dir")
        + File.separator + "src" + File.separator + "test" + File.separator + "java"
        + File.separator + "test.csv");
    mockView.setImportCalendarDialogResponse(importInput);
    controller.importCalendar();

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "showImportCalendarDialog called\n"
        + "showConfirmation called with message: Calendar imported successfully.\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n", viewLogger.toString());
  }

  @Test
  public void testImportCalendarInvalid() {
    Map<String, String> importInput = new HashMap<>();
    importInput.put("importFilePath", "test.xsd");
    mockView.setImportCalendarDialogResponse(importInput);
    controller.importCalendar();

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "showImportCalendarDialog called\n"
        + "showError called with message: Error importing calendar:"
        + " Unsupported export format: xsd."
        + " Supported formats are: [csv]\n", viewLogger.toString());
  }

  @Test
  public void createEventThrowingException() {
    Map<String, String> eventInput = new HashMap<>();
    eventInput.put("eventname", "TeamMeeting");
    eventInput.put("from", "2025-11-11T10:00");
    eventInput.put("to", "2025-11-11T11:00");
    eventInput.put("occurrence_count", null);
    eventInput.put("recurring_days", null);
    eventInput.put("recurrence_end_date", null);
    eventInput.put("description", "Daily standup meeting");
    eventInput.put("location", "Conference Room");
    eventInput.put("visibility", "public");
    mockView.setCreateEventResponse(eventInput);

    mockModel.setEventConflictException(true);
    controller.createEvent();

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "showCreateEventForm called\n"
        + "showError called with message: Event conflicts with existing event:"
        + " Event conflict exception\n", viewLogger.toString());
  }

  @Test
  public void createEventThrowingException1() {
    Map<String, String> eventInput = new HashMap<>();
    eventInput.put("eventname", "TeamMeeting");
    eventInput.put("from", "2025-11-11T10:00");
    eventInput.put("to", "2025-11-11T11:00");
    eventInput.put("occurrence_count", null);
    eventInput.put("recurring_days", null);
    eventInput.put("recurrence_end_date", null);
    eventInput.put("description", "Daily standup meeting");
    eventInput.put("location", "Conference Room");
    eventInput.put("visibility", "public");
    mockView.setCreateEventResponse(eventInput);

    mockModel.setInvalidCommandException(true);
    controller.createEvent();

    assertEquals("addFeatures called\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
            + "setActiveCalendar called with: testCalendar\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation "
            + "null null null null null\n"
            + "setActiveCalendar called with: PersonalCal\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
            + " null null null null null\n"
            + "showCreateEventForm called\n"
            + "showError called with message: Error creating event: Invalid command exception\n",
        viewLogger.toString());
  }

  @Test
  public void testEditEventInvalid() throws EventConflictException {
    EventsResponseDTO dummyEvent =
        EventsResponseDTO.builder().eventName("TeamMeeting").startTime(LocalDateTime.of(2025, 11,
                11, 10, 0)).endTime(LocalDateTime.of(2025, 11, 11, 11, 0))
            .location("Room1").build();

    Map<String, String> editInput = new HashMap<>();
    editInput.put("eventname", "TeamSync");
    editInput.put("isMultiple", "false");
    mockView.setEditEventFormResponse(editInput);
    mockModel.setEventConflictException(true);
    controller.editEvent(dummyEvent);

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "showEditEventForm called for event: TeamMeeting 2025-11-11T10:00 2025-11-11T11:00"
        + " Room1 null null null null null\n"
        + "showError called with message: Event update conflicts with existing event: Event"
        + " conflict exception\n", viewLogger.toString());
  }

  @Test
  public void testEditEventInvalid2() throws EventConflictException {
    EventsResponseDTO dummyEvent =
        EventsResponseDTO.builder().eventName("TeamMeeting").startTime(LocalDateTime.of(2025, 11,
                11, 10, 0)).endTime(LocalDateTime.of(2025, 11, 11, 11, 0))
            .location("Room1").build();

    Map<String, String> editInput = new HashMap<>();
    editInput.put("eventname", "TeamSync");
    editInput.put("isMultiple", "false");
    mockView.setEditEventFormResponse(editInput);
    mockModel.setInvalidCommandException(true);
    controller.editEvent(dummyEvent);

    assertEquals("addFeatures called\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
            + "setActiveCalendar called with: testCalendar\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
            + " null null null null null\n"
            + "setActiveCalendar called with: PersonalCal\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
            + " null null null null null\n"
            + "showEditEventForm called for event: TeamMeeting 2025-11-11T10:00 2025-11-11T11:00"
            + " Room1 null null null null null\n"
            + "showError called with message: Error updating event: Invalid command exception\n",
        viewLogger.toString());
  }

  @Test
  public void testCreateCalendarInvalid() {
    Map<String, String> calInput = new HashMap<>();
    calInput.put("name", "WorkCal");
    calInput.put("timezone", "America/New_York");
    mockView.setCreateCalendarFormResponse(calInput);
    mockModel.setInvalidCommandException(true);
    controller.createCalendar();
    assertEquals("addFeatures called\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
            + "setActiveCalendar called with: testCalendar\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00"
            + " testLocation null null null null null\n"
            + "setActiveCalendar called with: PersonalCal\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00"
            + " testLocation null null null null null\n"
            + "showCreateCalendarForm called\n"
            + "showError called with message: Error creating calendar: Invalid command exception\n",
        viewLogger.toString());
  }

  @Test
  public void testSetActiveCalendarInvalid() {
    mockModel.setInvalidCommandException(true);
    controller.setActiveCalendar("PersonalCal");

    assertEquals("addFeatures called\n"
        + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
        + "setActiveCalendar called with: testCalendar\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "setActiveCalendar called with: PersonalCal\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "getCurrentDate called, returning: 2025-11-11\n"
        + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
        + " null null null null null\n"
        + "showError called with message: Error setting active calendar: Invalid command"
        + " exception\n", viewLogger.toString());
  }

  @Test
  public void testLoadEventsInvalid() {
    mockModel.setInvalidCommandException(true);
    controller.loadEvents("2025-11-11T00:00", "2025-11-12T00:00", "2025-11-11");

    assertEquals("addFeatures called\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
            + "setActiveCalendar called with: testCalendar\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null "
            + "null null null null\n"
            + "setActiveCalendar called with: PersonalCal\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null "
            + "null null null null\n"
            + "showError called with message: Error loading events: Invalid command exception\n",
        viewLogger.toString());
  }

  @Test
  public void testNavigateToPreviousInvalid() {
    mockModel.setInvalidCommandException(true);
    controller.navigateToPrevious();
    assertEquals("addFeatures called\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
            + "setActiveCalendar called with: testCalendar\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
            + " null null null null null\n"
            + "setActiveCalendar called with: PersonalCal\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation"
            + " null null null null null\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "navigateToPrevious called with date: 2025-10-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "showError called with message: Error loading events: Invalid command exception\n",
        viewLogger.toString());
  }

  @Test
  public void testNavigateToNextInvalid() {
    mockModel.setInvalidCommandException(true);
    controller.navigateToNext();

    assertEquals("addFeatures called\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
            + "setActiveCalendar called with: testCalendar\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null"
            + " null null null null\n"
            + "setActiveCalendar called with: PersonalCal\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null"
            + " null null null null\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "navigateToNext called with date: 2025-12-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "showError called with message: Error loading events: Invalid command exception\n",
        viewLogger.toString());
  }

  @Test
  public void testFindEventsInvalid() {
    Map<String, String> findInput = new HashMap<>();
    findInput.put("eventName", "Meeting");
    findInput.put("startTime", "2025-11-11T00:00");
    findInput.put("endTime", "2025-11-12T00:00");
    findInput.put("on", "2025-11-11");
    mockModel.setInvalidCommandException(true);
    mockView.setFindEventsResponse(findInput);
    controller.findEvents();

    assertEquals("addFeatures called\n"
            + "updateCalendarList called with: testCalendar America/Los_Angeles\n"
            + "setActiveCalendar called with: testCalendar\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null "
            + "null null null null\n"
            + "setActiveCalendar called with: PersonalCal\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "getCurrentDate called, returning: 2025-11-11\n"
            + "updateEvents called with: null 2025-11-01T00:00 2025-12-01T00:00 testLocation null "
            + "null null null null\n"
            + "findEvents called\n"
            + "showError called with message: Error finding events: Invalid command exception\n",
        viewLogger.toString());
  }


  private static class MockModel implements ICalendarModel {
    private final StringBuilder log;
    private boolean eventConflictException;
    private boolean invalidCommandException;

    public MockModel(StringBuilder builder) {
      log = builder;
      eventConflictException = false;
      invalidCommandException = false;
    }

    @Override
    public void createEvent(String eventName, String startTime, String endTime,
                            String recurringDays, String occurrenceCount,
                            String recurrenceEndDate, String description, String location,
                            String visibility, boolean autoDecline) throws EventConflictException {
      if (eventConflictException) {
        throw new EventConflictException("Event conflict exception");
      }
      if (invalidCommandException) {
        throw new InvalidCommandException("Invalid command exception");
      }
      log.append("Creating event\n").append(eventName)
          .append(" ").append(startTime).append(" ").append(endTime).append(" ")
          .append(recurringDays).append(" ").append(occurrenceCount).append(" ")
          .append(recurrenceEndDate).append(" ").append(description).append(" ")
          .append(location).append(" ").append(visibility).append(" ").append(autoDecline)
          .append("\n");
    }


    @Override
    public void editEvent(EditEventRequestDTO editEventRequestDTO) throws EventConflictException {
      if (eventConflictException) {
        throw new EventConflictException("Event conflict exception");
      }
      if (invalidCommandException) {
        throw new InvalidCommandException("Invalid command exception");
      }
      log.append("Editing event\n").append(editEventRequestDTO.getEventName()).append(" ")
          .append(editEventRequestDTO.getStartTime()).append(" ")
          .append(editEventRequestDTO.getEndTime()).append(" ")
          .append(editEventRequestDTO.getPropertyName()).append(" ").
          append(editEventRequestDTO.getPropertyValue()).append("\n");
    }

    @Override
    public List<EventsResponseDTO> getEvents(String eventName, String startTime, String endTime,
                                             String on) {
      if (eventConflictException) {
        throw new EventConflictException("Event conflict exception");
      }
      if (invalidCommandException) {
        throw new InvalidCommandException("Invalid command exception");
      }
      log.append("Getting events\n").append(eventName).append(" ").append(startTime)
          .append(" ").append(endTime).append(" ").append(on).append("\n");
      return List.of(EventsResponseDTO.builder().eventName(eventName)
          .startTime(TimeUtil.getTemporalFromString(startTime))
          .endTime(TimeUtil.getTemporalFromString(endTime))
          .location("testLocation").build());
    }

    @Override
    public List<CalendarExporterDTO> getEventsForExport() {

      if (eventConflictException) {
        throw new EventConflictException("Event conflict exception");
      }
      if (invalidCommandException) {
        throw new InvalidCommandException("Invalid command exception");
      }
      return List.of();
    }

    @Override
    public String showStatus(String dateTime) {
      if (eventConflictException) {
        throw new EventConflictException("Event conflict exception");
      }
      if (invalidCommandException) {
        throw new InvalidCommandException("Invalid command exception");
      }
      log.append("Showing status\n").append(dateTime).append("\n");
      return "Return from Status";
    }

    @Override
    public void createCalendar(String calendarName, String timezone) {
      if (eventConflictException) {
        throw new EventConflictException("Event conflict exception");
      }
      if (invalidCommandException) {
        throw new InvalidCommandException("Invalid command exception");
      }
      log.append("Creating calendar\n").append(calendarName).append(" ").append(timezone).append(
          "\n");
    }

    @Override
    public void editCalendar(String calendarName, String propertyName, String propertyValue) {
      if (eventConflictException) {
        throw new EventConflictException("Event conflict exception");
      }
      if (invalidCommandException) {
        throw new InvalidCommandException("Invalid command exception");
      }
      log.append("Editing Calendar\n").append(calendarName).append(" ")
          .append(propertyName).append(" ")
          .append(propertyValue).append("\n");
    }

    @Override
    public void setCalendar(String calendarName) {
      if (eventConflictException) {
        throw new EventConflictException("Event conflict exception");
      }
      if (invalidCommandException) {
        throw new InvalidCommandException("Invalid command exception");
      }
      log.append("Using calendar: ").append(calendarName).append("\n");
    }

    @Override
    public void copyEvent(CopyEventRequestDTO copyEventRequestDTO) {
      if (eventConflictException) {
        throw new EventConflictException("Event conflict exception");
      }
      if (invalidCommandException) {
        throw new InvalidCommandException("Invalid command exception");
      }
      log.append("Copying event\n").append(copyEventRequestDTO.toString()).append("\n");
    }

    @Override
    public List<CalendarResponseDTO> getCalendars() {
      if (eventConflictException) {
        throw new EventConflictException("Event conflict exception");
      }
      if (invalidCommandException) {
        throw new InvalidCommandException("Invalid command exception");
      }
      log.append("Getting calendars\n");
      return List.of(CalendarResponseDTO.builder().name("testCalendar")
          .zoneId(ZoneId.of("America/Los_Angeles")).build());
    }

    public void setEventConflictException(boolean value) {
      this.eventConflictException = value;
    }

    public void setInvalidCommandException(boolean value) {
      this.invalidCommandException = value;
    }
  }
}
