package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.STATUS_ON_PATTERN;
import static calendarapp.utils.TimeUtil.getLocalDateTimeFromString;

public class ShowCommand extends AbstractCommand {

  private String on;

  ShowCommand(ICalendarApplication model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    Matcher matcher = regexMatching(STATUS_ON_PATTERN, command);
    if (matcher.find()) {
      on = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    if (on == null) {
      view.displayMessage("Required fields are missing. Cannot process the command.\\n\n");
      return;
    }

    try {
      view.displayMessage(model.showStatus(getLocalDateTimeFromString(on)) + "\n\n");
    } catch (IllegalArgumentException e) {
      view.displayMessage("Error running show command: " + e.getMessage() + ".\n\n");
    }
  }
}
