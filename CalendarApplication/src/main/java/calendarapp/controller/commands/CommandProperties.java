package calendarapp.controller.commands;

/**
 * Enum of command types supported by the calendar application.
 */
public enum CommandProperties {
  /**
   * Command to create new events.
   */
  CREATE_EVENT("create event"),

  /**
   * Command to create new calendar.
   */
  CREATE_CALENDAR("create calendar"),

  /**
   * Command to edit existing event.
   */
  EDIT_EVENT("edit event"),

  /**
   * Command to edit existing event.
   */
  EDIT_EVENTS("edit events"),

  /**
   * Command to edit existing events.
   */
  EDIT_CALENDAR("edit calendar"),

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
   * Command to copy calendar data.
   */
  COPY("copy"),

  /**
   * Command to use calendar.
   */
  USE("use"),

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
