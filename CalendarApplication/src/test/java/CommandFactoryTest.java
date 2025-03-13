import org.junit.Test;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

import calendarapp.controller.InvalidCommandException;
import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.impl.CommandFactory;
import calendarapp.controller.commands.impl.CreateCommand;
import calendarapp.controller.commands.impl.EditCommand;
import calendarapp.controller.commands.impl.ExportCommand;
import calendarapp.controller.commands.impl.PrintCommand;
import calendarapp.controller.commands.impl.ShowCommand;
import calendarapp.model.ICalendarModel;
import calendarapp.model.IEvent;
import calendarapp.view.ICalendarView;

import static org.junit.Assert.assertEquals;

public class CommandFactoryTest {

  Command command;

  @Test
  public void CreateCommandTest() {
    List<String> createCommands = new ArrayList<>();
    createCommands.add("create event --autoDecline TestEvent from 2025-11-11t11:00 to 2025-11-11t12:00 repeats <weekdays> until \"2025-11-20t12:00\"");
    createCommands.add("create event --autoDecline TestEvent on \"2025-11-20t12:00\"");
    createCommands.add("create event TestEvent on \"2025-11-20T12:00\" repeats \"MRW\" for 5 times");
    createCommands.add("create event TestEvent on \"2025-11-20T12:00\" repeats MRW until 2025-11-30");
    createCommands.add("CREATE EVENT --AUTODECLINE TESTEVENT FROM 2025-11-11T11:00 TO 2025-11-11T12:00 REPEATS <WEEKDAYS> UNTIL \"2025-11-20T12:00\"");
    createCommands.add("CREATE EVENT --AUTODECLINE TESTEVENT ON \"2025-11-20T12:00\"");
    createCommands.add("CREATE EVENT TESTEVENT ON \"2025-11-20T12:00\" REPEATS \"MRW\" FOR 5 TIMES");
    createCommands.add("CREATE EVENT TESTEVENT ON \"2025-11-20T12:00\" REPEATS MRW UNTIL 2025-11-30");
    createCommands.add("Create Event Auto Decline Test Event From 2025-11-11T11:00 To 2025-11-11T12:00 Repeats Weekdays Until 2025-11-20T12:00");
    createCommands.add("Create Event Auto Decline Test Event On 2025-11-20T12:00");
    createCommands.add("Create Event Test Event On 2025-11-20T12:00 Repeats MRW For 5 Times");
    createCommands.add("Create Event Test Event On 2025-11-20T12:00 Repeats MRW Until 2025-11-30");
    for (String cmnd : createCommands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), CreateCommand.class);
    }
  }

  @Test
  public void EditCommandTest() {
    List<String> createCommands = new ArrayList<>();
    createCommands.add("edit event location teammeeting from 2025-04-01t09:00 to 2025-04-01t10:00 with newlocation");
    createCommands.add("edit events location teammeeting from 2025-04-02t11:00 with newlocation");
    createCommands.add("edit events location teammeeting newlocation");
    createCommands.add("EDIT EVENT LOCATION TEAMMEETING FROM 2025-04-01T09:00 TO 2025-04-01T10:00 WITH NEWLOCATION");
    createCommands.add("EDIT EVENTS LOCATION TEAMMEETING FROM 2025-04-02T11:00 WITH NEWLOCATION");
    createCommands.add("EDIT EVENTS LOCATION TEAMMEETING NEWLOCATION");
    createCommands.add("Edit Event Location TeamMeeting From 2025-04-01T09:00 To 2025-04-01T10:00 With NewLocation");
    createCommands.add("Edit Events Location TeamMeeting From 2025-04-02T11:00 With NewLocation");
    createCommands.add("Edit Events Location TeamMeeting NewLocation");

    for (String cmnd : createCommands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), EditCommand.class);
    }
  }

  @Test
  public void PrintCommandTest() {
    List<String> createCommands = new ArrayList<>();
    createCommands.add("print events on 2025-04-01");
    createCommands.add("print events from 2025-04-01t09:00 to 2025-04-01t10:00");
    createCommands.add("PRINT EVENTS ON 2025-04-01");
    createCommands.add("PRINT EVENTS FROM 2025-04-01T09:00 TO 2025-04-01T10:00");
    createCommands.add("Print Events On 2025-04-01");
    createCommands.add("Print Events From 2025-04-01T09:00 To 2025-04-01T10:00");

    for (String cmnd : createCommands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), PrintCommand.class);
    }
  }

  @Test
  public void ExportCommandTest() {
    List<String> createCommands = new ArrayList<>();
    createCommands.add("export cal filename.csv");
    createCommands.add("EXPORT CAL FILENAME.CSV");
    createCommands.add("Export Cal Filename.Csv");

    for (String cmnd : createCommands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), ExportCommand.class);
    }
  }

  @Test
  public void ShowCommandTest() {
    List<String> createCommands = new ArrayList<>();
    createCommands.add("show status on 2025-04-01t09:00");
    createCommands.add("SHOW STATUS ON 2025-04-01T09:00");
    createCommands.add("Show Status On 2025-04-01T09:00");

    for (String cmnd : createCommands) {
      command = CommandFactory.getCommand(cmnd,
          new MockModel(), new MockView());
      assertEquals(command.getClass(), ShowCommand.class);
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

    @Override
    public void displayEvents(List<IEvent> events) {
      resultBuilder.append(events.toString());
    }

    public String getResult() {
      return resultBuilder.toString();
    }
  }

  private static class MockModel implements ICalendarModel {
    @Override
    public void createEvent(String eventName, Temporal startTime, Temporal endTime,
                            String recurringDays, String occurrenceCount,
                            Temporal recurrenceEndDate, String description, String location,
                            String visibility, boolean autoDecline) {
    }

    @Override
    public void editEvent(String eventName, Temporal startTime, Temporal endTime,
                          String property, String value) {
    }

    @Override
    public List<IEvent> getEventsBetween(Temporal startTime, Temporal endTime) {
      return List.of();
    }

    @Override
    public String export(String filename) {
      return "";
    }

    @Override
    public String showStatus(Temporal dateTime) {
      return "";
    }
  }
}