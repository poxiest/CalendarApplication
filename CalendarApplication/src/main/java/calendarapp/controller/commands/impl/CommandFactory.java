package calendarapp.controller.commands.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendarapp.controller.InvalidCommandException;
import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.CommandProperties;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

/**
 * Factory class for creating specific command objects based on input command.
 */
public class CommandFactory {

  private static final Map<CommandProperties,
      BiFunction<ICalendarModel, ICalendarView, Command>> commandsMap;

  static {
    commandsMap = new HashMap<>();
    commandsMap.put(CommandProperties.CREATE_EVENT, CreateEventCommand::new);
    commandsMap.put(CommandProperties.CREATE_CALENDAR, CreateCalendarCommand::new);
    commandsMap.put(CommandProperties.EDIT_EVENT, EditEventCommand::new);
    commandsMap.put(CommandProperties.EDIT_EVENTS, EditEventCommand::new);
    commandsMap.put(CommandProperties.EDIT_CALENDAR, EditCalendarCommand::new);
    commandsMap.put(CommandProperties.PRINT, PrintCommand::new);
    commandsMap.put(CommandProperties.EXPORT, ExportCommand::new);
    commandsMap.put(CommandProperties.SHOW, ShowCommand::new);
    commandsMap.put(CommandProperties.COPY, CopyCommand::new);
    commandsMap.put(CommandProperties.USE, UseCommand::new);
  }

  /**
   * Creates and returns the appropriate command object based on the command string.
   *
   * @param command The full command string to parse.
   * @param model   The calendar model to pass to the command.
   * @param view    The view to pass to the command.
   * @return A specific Command implementation based on the command type.
   * @throws InvalidCommandException If the command is not recognized or invalid.
   */
  public static Command getCommand(String command, ICalendarModel model, ICalendarView view)
      throws InvalidCommandException {
    String commandPattern = "^((?i)create|edit)\\s+(\\S+)|^(\\S+)";
    Pattern pattern = Pattern.compile(commandPattern);
    Matcher matcher = pattern.matcher(command);

    String cmd = null;

    if (matcher.find()) {
      if (matcher.group(1) != null && matcher.group(3) == null) {
        cmd = matcher.group(1) + " " + matcher.group(2);
      } else if (matcher.group(3) != null) {
        cmd = matcher.group(3);
      }
    }

    BiFunction<ICalendarModel, ICalendarView, Command> commandFunction = commandsMap
        .getOrDefault(Objects.requireNonNull(CommandProperties
            .getCommand(cmd.toLowerCase())), null);
    if (commandFunction == null) {
      throw new InvalidCommandException("Unknown command: " + command + "\n");
    }
    return commandFunction.apply(model, view);
  }
}