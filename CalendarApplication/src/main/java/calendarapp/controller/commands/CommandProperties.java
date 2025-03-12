package calendarapp.controller.commands;

/**
 * Enum of command types supported by the calendar application.
 */
public enum CommandProperties {
  /**
   * Command to create new events.
   */
  CREATE("create"),

  /**
   * Command to edit existing events.
   */
  EDIT("edit"),

  /**
   * Command to print calendar information.
   */
  PRINT("print"),

  /**
   * Command to show specific calendar views.
   */
  SHOW("show"),

  /**
   * Command to export calendar data.
   */
  EXPORT("export"),

  /**
   * Represents an unrecognized command.
   */
  UNKNOWN("unknown");

  /**
   * The string representation of the command.
   */
  private final String command;

  /**
   * Constructs a command property with the specified string representation.
   *
   * @param command the string representation of this command.
   */
  CommandProperties(String command) {
    this.command = command;
  }

  /**
   * Finds the command property corresponding to the given string command.
   *
   * @param command the string command to match.
   * @return the matching CommandProperties value, or UNKNOWN if no match is found.
   */
  public static CommandProperties getCommand(String command) {
    for (CommandProperties prop : values()) {
      if (prop.command.equalsIgnoreCase(command)) {
        return prop;
      }
    }
    return UNKNOWN;
  }
}
