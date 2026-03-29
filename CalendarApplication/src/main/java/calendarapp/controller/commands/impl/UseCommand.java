package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.Constants.USE_COMMAND;

/**
 * Command to switch the active calendar to the specified one.
 * Parses the input and updates the model to use the selected calendar.
 */

public class UseCommand extends AbstractCommand {

  private String calendarName;

  UseCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Executes the use calendar command by parsing the command string.
   *
   * @param command The command string containing export instructions.
   * @throws InvalidCommandException If the command syntax is invalid, the calendar name is missing.
   */
  @Override
  public void execute(String command) throws InvalidCommandException {
    Matcher matcher = regexMatching(USE_COMMAND, command);
    if (matcher.find()) {
      calendarName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    if (calendarName == null) {
      throw new InvalidCommandException(command + "\nReason : Required fields are missing.\n");
    }

    try {
      model.setCalendar(calendarName);
    } catch (Exception e) {
      throw new InvalidCommandException(command + "\nReason : " + e.getMessage());
    }
  }
}
