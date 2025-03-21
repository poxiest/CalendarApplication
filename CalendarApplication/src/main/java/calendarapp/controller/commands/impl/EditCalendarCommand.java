package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.EDIT_CALENDAR_PATTERN;

public class EditCalendarCommand extends AbstractCommand {

  private String calendarName;
  private String propertyName;
  private String propertyValue;

  EditCalendarCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Executes the create command by parsing the command string,
   * extracting event details, and creating the event in the model.
   *
   * @param command The command string containing event creation instructions.
   * @throws InvalidCommandException If the syntax is invalid or required fields are missing.
   * @throws EventConflictException  If the new event conflicts with existing events.
   */
  @Override
  public void execute(String command) throws InvalidCommandException, EventConflictException {
    parseCommand(command);
    try {
      model.editCalendar(calendarName, propertyName, propertyValue);
    } catch (IllegalArgumentException e) {
      throw new InvalidCommandException(command + "\nReason : " + e.getMessage());
    }
  }

  private void parseCommand(String command) {
    Matcher matcher = regexMatching(EDIT_CALENDAR_PATTERN, command);
    if (matcher.find()) {
      calendarName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      propertyName = matcher.group(3);
      propertyValue = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
    }

    if (calendarName == null || propertyName == null || propertyValue == null) {
      throw new InvalidCommandException(command + "\nReason : Required fields are missing.\n");
    }
  }
}
