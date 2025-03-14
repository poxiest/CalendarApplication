import org.junit.Test;

import calendarapp.controller.commands.CommandProperties;

import static org.junit.Assert.assertEquals;

/**
 * Test class for Enum {@link CommandProperties}.
 */
public class CommandPropertiesTest {

  @Test
  public void testCreateCommand() {
    CommandProperties command = CommandProperties.getCommand("create");
    assertEquals(CommandProperties.CREATE, command);
  }

  @Test
  public void testEditCommand() {
    CommandProperties command = CommandProperties.getCommand("edit");
    assertEquals(CommandProperties.EDIT, command);
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
  public void testUnknownCommand() {
    CommandProperties command = CommandProperties.getCommand("unknownCommand");
    assertEquals(CommandProperties.UNKNOWN, command);
  }

  @Test
  public void testCaseInsensitiveCommand() {
    CommandProperties command = CommandProperties.getCommand("CREATE");
    assertEquals(CommandProperties.CREATE, command);

    command = CommandProperties.getCommand("EdIt");
    assertEquals(CommandProperties.EDIT, command);

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
