package calendarapp.controller.commands;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class CommandFactoryImpl implements CommandFactory {

  ICalendarApplication model;
  ICalendarView view;

  public CommandFactoryImpl(ICalendarApplication model, ICalendarView view) {
    this.model = model;
    this.view = view;
  }

  @Override
  public Command createCommand(String command) {

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
        view.display("Unknown command: " + command + "\n");
      }
    }
    return null;
  }
}
