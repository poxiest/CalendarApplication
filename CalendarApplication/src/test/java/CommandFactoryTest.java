import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import calendarapp.controller.InvalidCommandException;
import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.impl.CommandFactory;
import calendarapp.controller.commands.impl.CopyCommand;
import calendarapp.controller.commands.impl.CreateCalendarCommand;
import calendarapp.controller.commands.impl.CreateEventCommand;
import calendarapp.controller.commands.impl.EditCalendarCommand;
import calendarapp.controller.commands.impl.EditEventCommand;
import calendarapp.controller.commands.impl.ExportCommand;
import calendarapp.controller.commands.impl.PrintCommand;
import calendarapp.controller.commands.impl.ShowCommand;
import calendarapp.controller.commands.impl.UseCommand;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.CalendarExporterDTO;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.view.ICalendarView;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link CommandFactory}.
 */
public class CommandFactoryTest {

  Command command;

  @Test
  public void CreateCommandTest() {
    List<String> commands = new ArrayList<>();
    commands.add("create event --autoDecline TestEvent from 2025-11-11t11:00 to " +
        "2025-11-11t12:00 repeats <weekdays> until \"2025-11-20t12:00\"");
    commands.add("create event --autoDecline TestEvent on \"2025-11-20t12:00\"");
    commands.add("create event TestEvent on \"2025-11-20T12:00\" repeats \"MRW\" for " +
        "5 times");
    commands.add("create event TestEvent on \"2025-11-20T12:00\" repeats MRW until " +
        "2025-11-30");
    commands.add("CREATE EVENT --AUTODECLINE TESTEVENT FROM 2025-11-11T11:00 TO " +
        "2025-11-11T12:00 REPEATS <WEEKDAYS> UNTIL \"2025-11-20T12:00\"");
    commands.add("CREATE EVENT --AUTODECLINE TESTEVENT ON \"2025-11-20T12:00\"");
    commands.add("CREATE EVENT TESTEVENT ON \"2025-11-20T12:00\" REPEATS \"MRW\" " +
        "FOR 5 TIMES");
    commands.add("CREATE EVENT TESTEVENT ON \"2025-11-20T12:00\" REPEATS MRW UNTIL " +
        "2025-11-30");
    commands.add("Create Event Auto Decline Test Event From 2025-11-11T11:00 To " +
        "2025-11-11T12:00 Repeats Weekdays Until 2025-11-20T12:00");
    commands.add("Create Event Auto Decline Test Event On 2025-11-20T12:00");
    commands.add("Create Event Test Event On 2025-11-20T12:00 Repeats MRW For 5 Times");
    commands.add("Create Event Test Event On 2025-11-20T12:00 Repeats MRW Until 2025-11-30");
    for (String cmnd : commands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), CreateEventCommand.class);
    }
  }

  @Test
  public void CreateCalendarCommandTest() {
    List<String> commands = new ArrayList<>();
    commands.add("create calendar --name WorkCalendar --timezone America/New_York");
    commands.add("create calendar --name \"Personal Calendar\" --timezone Europe/London");
    commands.add("CREATE CALENDAR --NAME FamilyEvents --TIMEZONE Asia/Kolkata");
    commands.add("Create Calendar --Name TravelPlanner --Timezone Australia/Sydney");
    for (String cmnd : commands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), CreateCalendarCommand.class);
    }
  }

  @Test
  public void EditCommandTest() {
    List<String> commands = new ArrayList<>();
    commands.add("edit event location teammeeting from 2025-04-01t09:00 to " +
        "2025-04-01t10:00 with newlocation");
    commands.add("edit events location teammeeting from 2025-04-02t11:00 with newlocation");
    commands.add("edit events location teammeeting newlocation");
    commands.add("EDIT EVENT LOCATION TEAMMEETING FROM 2025-04-01T09:00 TO " +
        "2025-04-01T10:00 WITH NEWLOCATION");
    commands.add("EDIT EVENTS LOCATION TEAMMEETING FROM 2025-04-02T11:00 WITH NEWLOCATION");
    commands.add("EDIT EVENTS LOCATION TEAMMEETING NEWLOCATION");
    commands.add("Edit Event Location TeamMeeting From 2025-04-01T09:00 To " +
        "2025-04-01T10:00 With NewLocation");
    commands.add("Edit Events Location TeamMeeting From 2025-04-02T11:00 With NewLocation");
    commands.add("Edit Events Location TeamMeeting NewLocation");

    for (String cmnd : commands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), EditEventCommand.class);
    }
  }

  @Test
  public void EditCalendarCommandTest() {
    List<String> commands = new ArrayList<>();
    commands.add("edit calendar --name WorkCalendar --property timezone Asia/Tokyo");
    commands.add("edit calendar --name \"Personal Calendar\" --property timezone America/Chicago");
    commands.add("EDIT CALENDAR --NAME FamilyEvents --PROPERTY name \"Family Planner\"");
    commands.add("Edit Calendar --Name TravelPlanner --Property color blue");

    for (String cmnd : commands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), EditCalendarCommand.class);
    }
  }

  @Test
  public void PrintCommandTest() {
    List<String> commands = new ArrayList<>();
    commands.add("print events on 2025-04-01");
    commands.add("print events from 2025-04-01t09:00 to 2025-04-01t10:00");
    commands.add("PRINT EVENTS ON 2025-04-01");
    commands.add("PRINT EVENTS FROM 2025-04-01T09:00 TO 2025-04-01T10:00");
    commands.add("Print Events On 2025-04-01");
    commands.add("Print Events From 2025-04-01T09:00 To 2025-04-01T10:00");

    for (String cmnd : commands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), PrintCommand.class);
    }
  }

  @Test
  public void ExportCommandTest() {
    List<String> commands = new ArrayList<>();
    commands.add("export cal filename.csv");
    commands.add("EXPORT CAL FILENAME.CSV");
    commands.add("Export Cal Filename.Csv");

    for (String cmnd : commands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), ExportCommand.class);
    }
  }

  @Test
  public void ShowCommandTest() {
    List<String> commands = new ArrayList<>();
    commands.add("show status on 2025-04-01t09:00");
    commands.add("SHOW STATUS ON 2025-04-01T09:00");
    commands.add("Show Status On 2025-04-01T09:00");

    for (String cmnd : commands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), ShowCommand.class);
    }
  }

  @Test
  public void UseCommandTest() {
    List<String> commands = new ArrayList<>();
    commands.add("use calendar --name WorkCalendar");
    commands.add("use calendar --name \"Personal Calendar\"");
    commands.add("USE CALENDAR --NAME FamilyEvents");
    commands.add("Use Calendar --Name TravelPlanner");

    for (String cmnd : commands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), UseCommand.class);
    }
  }

  @Test
  public void CopyCommandTest() {
    List<String> commands = new ArrayList<>();
    commands.add("copy event Meeting1 on 2025-04-10T09:00 --target WorkCalendar to "
        + "2025-04-15T10:00");
    commands.add("copy event \"Project Deadline\" on 2025-06-30T23:59 --target PersonalCalendar "
        + "to 2025-07-01T00:00");
    commands.add("COPY EVENT CONFERENCE ON 2025-05-12T14:30 --TARGET FamilyEvents TO "
        + "2025-05-20T16:00");
    commands.add("Copy Event LunchMeeting on 2025-09-01T12:00 --Target TravelPlanner to "
        + "2025-09-02T13:00");
    commands.add("copy events on 2025-04-10 --target WorkCalendar to 2025-04-15");
    commands.add("copy events on 2025-06-30 --target PersonalCalendar to 2025-07-01");
    commands.add("COPY EVENTS ON 2025-05-12 --TARGET FamilyEvents TO 2025-05-20");
    commands.add("Copy Events On 2025-09-01 --Target TravelPlanner to 2025-09-02");
    commands.add("copy events between 2025-04-01 and 2025-04-10 --target WorkCalendar 2025-04-15");
    commands.add("copy events between 2025-06-01 and 2025-06-30 --target PersonalCalendar "
        + "2025-07-01");
    commands.add("COPY EVENTS BETWEEN 2025-05-01 AND 2025-05-12 --TARGET FamilyEvents 2025-05-20");
    commands.add("Copy Events Between 2025-09-01 And 2025-09-10 --Target TravelPlanner 2025-09-15");

    for (String cmnd : commands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), CopyCommand.class);
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void WrongInputCommandTest() {
    try {
      command = CommandFactory.getCommand("creta", new MockModel(), new MockView());
    } catch (InvalidCommandException e) {
      assertEquals("Unknown command: creta\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void WrongInputCommandTest1() {
    try {
      command = CommandFactory.getCommand("edti", new MockModel(), new MockView());
    } catch (InvalidCommandException e) {
      assertEquals("Unknown command: edti\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void WrongInputCommandTest2() {
    try {
      command = CommandFactory.getCommand("prin", new MockModel(), new MockView());
    } catch (InvalidCommandException e) {
      assertEquals("Unknown command: prin\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void WrongInputCommandTest3() {
    try {
      command = CommandFactory.getCommand("shw", new MockModel(), new MockView());
    } catch (InvalidCommandException e) {
      assertEquals("Unknown command: shw\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void WrongInputCommandTest4() {
    try {
      command = CommandFactory.getCommand("exprt", new MockModel(), new MockView());
    } catch (InvalidCommandException e) {
      assertEquals("Unknown command: exprt\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  private static class MockView implements ICalendarView {
    private final StringBuilder resultBuilder = new StringBuilder();

    @Override
    public void displayMessage(String message) {
      resultBuilder.append(message);
    }

    public String getResult() {
      return resultBuilder.toString();
    }
  }

  private static class MockModel implements ICalendarModel {
    @Override
    public void createEvent(String eventName, String startTime, String endTime,
                            String recurringDays, String occurrenceCount,
                            String recurrenceEndDate, String description, String location,
                            String visibility, boolean autoDecline) {
      return;
    }

    @Override
    public void editEvent(String eventName, String startTime, String endTime,
                          String property, String value) {
      return;
    }

    @Override
    public List<EventsResponseDTO> getEvents(String startTime, String endTime,
                                             String on, String eventName) {
      return List.of();
    }

    @Override
    public List<CalendarExporterDTO> getEventsForExport() {
      return List.of();
    }

    @Override
    public String showStatus(String dateTime) {
      return "";
    }

    @Override
    public void createCalendar(String calendarName, String timezone) {
      // empty for test purposes.
    }

    @Override
    public void editCalendar(String calendarName, String propertyName, String propertyValue) {
      // empty for test purposes.
    }

    @Override
    public void setCalendar(String calendarName) {
      // empty for test purposes.
    }

    @Override
    public void copyEvent(CopyEventRequestDTO copyEventRequestDTO) {
      // empty for test purposes.
    }

    @Override
    public List<String> getCalendars() {
      return List.of();
    }
  }
}