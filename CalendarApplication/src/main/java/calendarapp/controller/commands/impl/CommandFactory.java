package calendarapp.controller.commands.impl;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendarapp.controller.InvalidCommandException;
import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.CommandProperties;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

public class CommandFactory {
  public static Command getCommand(String command, ICalendarModel model, ICalendarView view)
      throws InvalidCommandException {
    String commandPattern = "^\\s*(\\S+)";
    Pattern pattern = Pattern.compile(commandPattern);
    Matcher matcher = pattern.matcher(command);

    String cmd = matcher.find() ? matcher.group(1) : "";

    switch (Objects.requireNonNull(CommandProperties.getCommand(cmd.toLowerCase()))) {
      case CREATE:
        return new CreateCommand(model, view);
      case EDIT:
        return new EditCommand(model, view);
      case PRINT:
        return new PrintCommand(model, view);
      case EXPORT:
        return new ExportCommand(model, view);
      case SHOW:
        return new ShowCommand(model, view);
      default: {
        throw new InvalidCommandException("Unknown command: " + command + "\n");
      }
    }
  }
}
