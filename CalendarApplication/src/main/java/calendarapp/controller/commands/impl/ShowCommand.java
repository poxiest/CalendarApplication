package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.STATUS_ON_PATTERN;
import static calendarapp.utils.TimeUtil.getLocalDateTimeFromString;

public class ShowCommand extends AbstractCommand {

  private String on;

  ShowCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    Matcher matcher = regexMatching(STATUS_ON_PATTERN, command);
    if (matcher.find()) {
      on = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    if (on == null) {
      throw new InvalidCommandException(command + "\nReason : Required fields are missing.\n");
    }

    try {
      view.displayMessage(model.showStatus(getLocalDateTimeFromString(on)) + "\n");
    } catch (IllegalArgumentException e) {
      throw new InvalidCommandException(command + "\nReason : " + e.getMessage());
    }
  }
}
