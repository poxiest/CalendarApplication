package calendarapp.controller.commands;

public enum CommandProperties {
  CREATE("create"),
  EDIT("edit"),
  PRINT("print"),
  SHOW("show"),
  EXPORT("export"),
  UNKNOWN("unknown");

  private final String command;

  CommandProperties(String command) {
    this.command = command;
  }

  public String getCommand() {
    return command;
  }

  public static CommandProperties getCommand(String command) {
    for (CommandProperties prop : values()) {
      if (prop.command.equalsIgnoreCase(command)) {
        return prop;
      }
    }
    return UNKNOWN;
  }
}
