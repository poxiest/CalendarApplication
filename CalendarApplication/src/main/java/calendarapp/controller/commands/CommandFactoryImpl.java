package calendarapp.controller.commands;

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

    String cmnd = matcher.find() ? matcher.group(1) : "";

    switch (cmnd.toLowerCase()) {
      case "create":
        return new CreateCommand(model, view);
      case "edit":
        return new EditCommand(model, view);
      case "print":
        return new PrintCommand(model, view);
      case "export":
        return new ExportCommand(model, view);
      case "show":
        return new ShowCommand(model, view);
      default: {
        view.display("Unknown command: " + command + "\n");
      }
    }
    return null;
  }
}
