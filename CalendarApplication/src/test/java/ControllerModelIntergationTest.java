import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.InvalidCommandException;
import calendarapp.controller.impl.CalendarController;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.CalendarExporterDTO;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.model.dto.PrintEventsResponseDTO;
import calendarapp.utils.TimeUtil;
import calendarapp.view.ICalendarView;
import calendarapp.view.impl.CLIView;

import static org.junit.Assert.assertEquals;

/**
 * Integration test for controller to model by mocking model.
 */
public class ControllerModelIntergationTest {
  private StringBuilder log;
  private StringBuilder output;
  private ICalendarModel model;
  private ICalendarView view;
  private ICalendarController controller;

  @Before
  public void setUp() {
    log = new StringBuilder();
    output = new StringBuilder();
    model = new MockModel(log);
    view = new CLIView(output);
  }

  @Test(expected = InvalidCommandException.class)
  public void testingMissingExitStatement() {
    try {
      controller = new CalendarController(new StringReader("create event Standup from "
          + "\"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n"), model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("Command file must end with 'exit' command./n", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testingCreateCommandIntegration() {
    controller = new CalendarController(new StringReader("create event Standup from "
        + "\"2025-11-11T11:00\" to \"2025-11-11T12:00\"\nexit"), model, view);
    controller.start();
    assertEquals("Event Name: Standup\n"
        + "Start Time: 2025-11-11T11:00\n"
        + "End Time: 2025-11-11T12:00\n"
        + "Recurring Days: null\n"
        + "Occurrence Count: null\n"
        + "Recurrence End Date: null\n"
        + "Description: null\n"
        + "Location: null\n"
        + "Visibility: null\n"
        + "Auto Decline: false\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: create event Standup from \"2025-11-11T11:00\" to "
        + "\"2025-11-11T12:00\"\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testingCreateCommandIntegration1() {
    controller = new CalendarController(new StringReader("create event --autodecline Standup "
        + "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\" description \"Daily Standup\" "
        + "location \"Shillman\" visibility \"private\"\nexit"), model, view);
    controller.start();
    assertEquals("Event Name: Standup\n"
        + "Start Time: 2025-11-11T11:00\n"
        + "End Time: 2025-11-11T12:00\n"
        + "Recurring Days: null\n"
        + "Occurrence Count: null\n"
        + "Recurrence End Date: null\n"
        + "Description: Daily Standup\n"
        + "Location: Shillman\n"
        + "Visibility: private\n"
        + "Auto Decline: true\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: create event --autodecline Standup from \"2025-11-11T11:00\" to "
        + "\"2025-11-11T12:00\" description \"Daily Standup\" location \"Shillman\" visibility "
        + "\"private\"\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testingCreateCommandIntegration2() {
    controller = new CalendarController(new StringReader("create event --autodecline Standup "
        + "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\" repeats MW for 9 times \nexit"),
        model, view);
    controller.start();
    assertEquals("Event Name: Standup\n"
        + "Start Time: 2025-11-11T11:00\n"
        + "End Time: 2025-11-11T12:00\n"
        + "Recurring Days: MW\n"
        + "Occurrence Count: 9\n"
        + "Recurrence End Date: null\n"
        + "Description: null\n"
        + "Location: null\n"
        + "Visibility: null\n"
        + "Auto Decline: true\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: create event --autodecline Standup from \"2025-11-11T11:00\" to "
        + "\"2025-11-11T12:00\" repeats MW for 9 times \n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testingCreateCommandIntegration3() {
    controller = new CalendarController(new StringReader("create event --autodecline Standup "
        + "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\" repeats MW until \"2025-11-20\""
        + "\nexit"),
        model, view);
    controller.start();
    assertEquals("Event Name: Standup\n"
        + "Start Time: 2025-11-11T11:00\n"
        + "End Time: 2025-11-11T12:00\n"
        + "Recurring Days: MW\n"
        + "Occurrence Count: null\n"
        + "Recurrence End Date: 2025-11-20\n"
        + "Description: null\n"
        + "Location: null\n"
        + "Visibility: null\n"
        + "Auto Decline: true\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: create event --autodecline Standup from \"2025-11-11T11:00\" to "
        + "\"2025-11-11T12:00\" repeats MW until \"2025-11-20\"\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testEditCommandsIntegration() {
    controller = new CalendarController(new StringReader("edit event eventname Standup from "
        + "\"2025-11-11T11:00\" to \"2025-11-11T12:00\" with Meetup\nexit"), model, view);
    controller.start();
    assertEquals("Event Name: Standup\n"
        + "Start Time: 2025-11-11T11:00\n"
        + "End Time: 2025-11-11T12:00\n"
        + "Property: eventname\n"
        + "Property Value: Meetup\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: edit event eventname Standup from \"2025-11-11T11:00\" to "
        + "\"2025-11-11T12:00\" with Meetup\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testEditCommandsIntegration1() {
    controller = new CalendarController(new StringReader("edit events location Standup1 from "
        + "\"2025-11-11T11:00\" with Shillman\nexit"), model, view);
    controller.start();
    assertEquals("Event Name: Standup1\n"
        + "Start Time: 2025-11-11T11:00\n"
        + "End Time: null\n"
        + "Property: location\n"
        + "Property Value: Shillman\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: edit events location Standup1 from \"2025-11-11T11:00\" with "
        + "Shillman\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testEditCommandsIntegration2() {
    controller = new CalendarController(new StringReader("edit events location standup1 "
        + "Richard\nexit"), model, view);
    controller.start();
    assertEquals("Event Name: standup1\n"
        + "Start Time: null\n"
        + "End Time: null\n"
        + "Property: location\n"
        + "Property Value: Richard\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: edit events location standup1 Richard\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testPrintCommandsIntegration() {
    controller = new CalendarController(new StringReader("print events on \"2025-11-11\"\n"
        + "exit"), model, view);
    controller.start();
    assertEquals("Start Time: null\n"
        + "End Time: null\n"
        + "On Time: 2025-11-11\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: print events on \"2025-11-11\"\n"
        + "Events:\n"
        + "• Test - 2025-11-11T00:00 to 2025-11-12T00:00 - Location: testLocation\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testPrintCommandsIntegration1() {
    controller = new CalendarController(new StringReader("print events from "
        + "\"2025-11-11T11:00\" to \"2025-11-12T11:00\"\nexit"),
        model, view);
    controller.start();
    assertEquals("Start Time: 2025-11-11T11:00\n"
        + "End Time: 2025-11-12T11:00\n"
        + "On Time: null\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: print events from \"2025-11-11T11:00\" to \"2025-11-12T11:00\"\n"
        + "Events:\n"
        + "• Test - 2025-11-11T00:00 to 2025-11-12T00:00 - Location: testLocation\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testShowCommandsIntegration() {
    controller = new CalendarController(new StringReader("show status on "
        + "\"2025-11-11T11:00\"\nexit"),
        model, view);
    controller.start();
    assertEquals("Status on Time: 2025-11-11T11:00\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: show status on \"2025-11-11T11:00\"\n"
        + "Status: Status Available/Busy!\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testCreateCalendarCommandsIntegration() {
    controller = new CalendarController(new StringReader("create calendar --name NewCal "
        + "--timezone America/Los_Angeles\nexit"),
        model, view);
    controller.start();
    assertEquals("Calendar Name: NewCal\n"
        + "Timezone: America/Los_Angeles\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: create calendar --name NewCal --timezone America/Los_Angeles\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testEditCalendarCommandsIntegration() {
    controller = new CalendarController(new StringReader("edit calendar --name NewCal "
        + "--property name OldCal\nexit"),
        model, view);
    controller.start();
    assertEquals("Calendar Name: NewCal\n"
        + "Property Name: name\n"
        + "Property Value: OldCal\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: edit calendar --name NewCal --property name OldCal\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testUseCalendarCommandsIntegration() {
    controller = new CalendarController(new StringReader("use calendar --name newCal\nexit"),
        model, view);
    controller.start();
    assertEquals("Calendar Name: newCal\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: use calendar --name newCal\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testCopyCommandsIntegration() {
    controller = new CalendarController(new StringReader("copy event Standup on "
        + "\"2025-11-11T11:00\" --target NewCal to \"2025-12-12T09:00\"\nexit"),
        model, view);
    controller.start();
    assertEquals("Event Name: Standup\n"
        + "Start Time: 2025-11-11T11:00\n"
        + "End Time: null\n"
        + "Calendar Name: NewCal\n"
        + "Copy to: 2025-12-12T09:00\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: copy event Standup on \"2025-11-11T11:00\" --target NewCal to "
        + "\"2025-12-12T09:00\"\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testCopyCommandsIntegration1() {
    controller = new CalendarController(new StringReader("copy events on "
        + "\"2025-11-11\" --target NewCal to \"2025-12-12\"\nexit"),
        model, view);
    controller.start();
    assertEquals("Event Name: null\n"
        + "Start Time: 2025-11-11T00:00\n"
        + "End Time: null\n"
        + "Calendar Name: NewCal\n"
        + "Copy to: 2025-12-12\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: copy events on \"2025-11-11\" --target NewCal to "
        + "\"2025-12-12\"\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  @Test
  public void testCopyCommandsIntegration2() {
    controller = new CalendarController(new StringReader("copy events between \"2025-11-11\" "
        + "and \"2025-12-11\" --target newCal to \"2026-01-11\"\nexit"),
        model, view);
    controller.start();
    assertEquals("Event Name: null\n"
        + "Start Time: 2025-11-11T00:00\n"
        + "End Time: 2025-12-12T00:00\n"
        + "Calendar Name: newCal\n"
        + "Copy to: 2026-01-11\n", log.toString());
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: copy events between \"2025-11-11\" and \"2025-12-11\" --target "
        + "newCal to \"2026-01-11\"\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Exiting application.\n", output.toString());
  }

  private class MockModel implements ICalendarModel {
    private final StringBuilder log;

    MockModel(StringBuilder builder) {
      log = builder;
    }

    @Override
    public void createEvent(String eventName, String startTime, String endTime,
                            String recurringDays, String occurrenceCount,
                            String recurrenceEndDate, String description, String location,
                            String visibility, boolean autoDecline) throws EventConflictException {
      log.append("Event Name: ").append(eventName).append("\n")
          .append("Start Time: ").append(startTime).append("\n")
          .append("End Time: ").append(endTime).append("\n")
          .append("Recurring Days: ").append(recurringDays).append("\n")
          .append("Occurrence Count: ").append(occurrenceCount).append("\n")
          .append("Recurrence End Date: ").append(recurrenceEndDate).append("\n")
          .append("Description: ").append(description).append("\n")
          .append("Location: ").append(location).append("\n")
          .append("Visibility: ").append(visibility).append("\n")
          .append("Auto Decline: ").append(autoDecline).append("\n");
    }


    @Override
    public void editEvent(String eventName, String startTime, String endTime, String property,
                          String value) throws EventConflictException {
      log.append("Event Name: ").append(eventName).append("\n")
          .append("Start Time: ").append(startTime).append("\n")
          .append("End Time: ").append(endTime).append("\n")
          .append("Property: ").append(property).append("\n")
          .append("Property Value: ").append(value).append("\n");
    }

    @Override
    public List<PrintEventsResponseDTO> getEventsForPrinting(String startTime, String endTime,
                                                             String on) {
      log.append("Start Time: ").append(startTime).append("\n")
          .append("End Time: ").append(endTime).append("\n")
          .append("On Time: ").append(on).append("\n");
      return List.of(PrintEventsResponseDTO.builder().eventName("Test")
          .startTime(TimeUtil.getTemporalFromString("2025-11-11"))
          .endTime(TimeUtil.getTemporalFromString("2025-11-12"))
          .location("testLocation").build());
    }

    @Override
    public List<CalendarExporterDTO> getEventsForExport() {
      return List.of();
    }

    @Override
    public String showStatus(String dateTime) {
      log.append("Status on Time: ").append(dateTime).append("\n");
      return "Status Available/Busy!";
    }

    @Override
    public void createCalendar(String calendarName, String timezone) {
      log.append("Calendar Name: ").append(calendarName).append("\n")
          .append("Timezone: ").append(timezone).append("\n");
    }

    @Override
    public void editCalendar(String calendarName, String propertyName, String propertyValue) {
      log.append("Calendar Name: ").append(calendarName).append("\n")
          .append("Property Name: ").append(propertyName).append("\n")
          .append("Property Value: ").append(propertyValue).append("\n");
    }

    @Override
    public void setCalendar(String calendarName) {
      log.append("Calendar Name: ").append(calendarName).append("\n");
    }

    @Override
    public void copyEvent(CopyEventRequestDTO copyEventRequestDTO) {
      log.append("Event Name: ").append(copyEventRequestDTO.getEventName()).append("\n")
          .append("Start Time: ").append(copyEventRequestDTO.getStartTime()).append("\n")
          .append("End Time: ").append(copyEventRequestDTO.getEndTime()).append("\n")
          .append("Calendar Name: ").append(copyEventRequestDTO.getCopyCalendarName()).append("\n")
          .append("Copy to: ").append(copyEventRequestDTO.getCopyToDate()).append("\n");
    }
  }
}
