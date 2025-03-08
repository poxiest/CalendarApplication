package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.statusPattern;
import static calendarapp.utils.TimeUtil.getLocalDateTimeFromString;

public class ShowCommand extends AbstractCommand {

  private String on;

  ShowCommand(ICalendarApplication model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    Matcher matcher = regexMatching(statusPattern, command);
    if (matcher.find()) {
      on = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    if (on == null) {
      view.display("Required fields are missing. Cannot process the command.\n");
      return;
    }

    try {
      view.display(model.showStatus(getLocalDateTimeFromString(on)) + "\n");
    } catch (IllegalArgumentException e) {
      view.display("Error running show command: " + e.getMessage() + ".\n");
    }
  }
}
