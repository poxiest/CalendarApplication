import org.junit.Test;

import java.io.IOException;
import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.impl.CommandFactory;
import calendarapp.controller.commands.impl.CreateCommand;
import calendarapp.controller.commands.impl.EditCommand;
import calendarapp.model.ICalendarModel;
import calendarapp.model.IEvent;
import calendarapp.view.ICalendarView;

import static org.junit.Assert.assertEquals;

public class CommandFactoryTest {

  Command command;

  @Test
  public void CreateCommandTest() {
    command = CommandFactory.getCommand("create events on \"2025-11-11\"",
        new MockModel(), new MockView());
    assertEquals(command.getClass(), CreateCommand.class);
  }

  @Test
  public void CreateCommandTest1() {
    command = CommandFactory.getCommand("CREATE EVENTS on \"2025-11-11\"",
        new MockModel(), new MockView());
    assertEquals(command.getClass(), CreateCommand.class);
  }

  @Test
  public void EditCommandTest() {
    command = CommandFactory.getCommand("edit events <property> <eventName> " +
            "<NewPropertyValue>",
        new MockModel(), new MockView());
    assertEquals(command.getClass(), EditCommand.class);
  }

  @Test
  public void EditCommandTest1() {
    command = CommandFactory.getCommand("edit events <property> <eventName> " +
            "from <dateStringTtimeString> with <NewPropertyValue>",
        new MockModel(), new MockView());
    assertEquals(command.getClass(), EditCommand.class);
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
    public List<IEvent> printEvents(Temporal startTime, Temporal endTime) {
      return List.of();
    }

    @Override
    public String export(String filename) throws IOException {
      return "";
    }

    @Override
    public String showStatus(Temporal dateTime) {
      return "";
    }
  }
}