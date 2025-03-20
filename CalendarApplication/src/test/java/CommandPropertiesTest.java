import org.junit.Test;

import calendarapp.controller.commands.CommandProperties;

import static org.junit.Assert.assertEquals;

/**
 * Test class for Enum {@link CommandProperties}.
 */
public class CommandPropertiesTest {

  @Test
  public void testCreateCommand() {
    CommandProperties command = CommandProperties.getCommand("create event");
    assertEquals(CommandProperties.CREATE_EVENT, command);
  }

  @Test
  public void testCreateCommand1() {
    CommandProperties command = CommandProperties.getCommand("create calendar");
    assertEquals(CommandProperties.CREATE_CALENDAR, command);
  }

  @Test
  public void testEditCommand() {
    CommandProperties command = CommandProperties.getCommand("edit event");
    assertEquals(CommandProperties.EDIT_EVENT, command);
  }

  @Test
  public void testEditCommand1() {
    CommandProperties command = CommandProperties.getCommand("edit events");
    assertEquals(CommandProperties.EDIT_EVENTS, command);
  }

  @Test
  public void testPrintCommand() {
    CommandProperties command = CommandProperties.getCommand("print");
    assertEquals(CommandProperties.PRINT, command);
  }

  @Test
  public void testShowCommand() {
    CommandProperties command = CommandProperties.getCommand("show");
    assertEquals(CommandProperties.SHOW, command);
  }

  @Test
  public void testExportCommand() {
    CommandProperties command = CommandProperties.getCommand("export");
    assertEquals(CommandProperties.EXPORT, command);
  }

  @Test
  public void testUseCommand() {
    CommandProperties command = CommandProperties.getCommand("use");
    assertEquals(CommandProperties.USE, command);
  }

  @Test
  public void testCopyCommand() {
    CommandProperties command = CommandProperties.getCommand("copy");
    assertEquals(CommandProperties.COPY, command);
  }


  @Test
  public void testUnknownCommand() {
    CommandProperties command = CommandProperties.getCommand("unknownCommand");
    assertEquals(CommandProperties.UNKNOWN, command);
  }

  @Test
  public void testCaseInsensitiveCommand() {
    CommandProperties command = CommandProperties.getCommand("CREATE EVENT");
    assertEquals(CommandProperties.CREATE_EVENT, command);

    command = CommandProperties.getCommand("EdIt evEnt");
    assertEquals(CommandProperties.EDIT_EVENT, command);

    command = CommandProperties.getCommand("PRINT");
    assertEquals(CommandProperties.PRINT, command);
  }

  @Test
  public void testEmptyCommand() {
    CommandProperties command = CommandProperties.getCommand("");
    assertEquals(CommandProperties.UNKNOWN, command);
  }

  @Test
  public void testNullCommand() {
    CommandProperties command = CommandProperties.getCommand(null);
    assertEquals(CommandProperties.UNKNOWN, command);
  }
}
