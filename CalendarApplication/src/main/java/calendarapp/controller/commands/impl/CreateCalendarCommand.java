package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.CREATE_NEW_CALENDAR;

public class CreateCalendarCommand extends AbstractCommand {

  private String calendarName;
  private String timeZone;

  CreateCalendarCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Executes the create calendar command by parsing the command string,
   * extracting calendar details, and creating the calendar in the model.
   *
   * @param command The command string containing event creation instructions.
   * @throws InvalidCommandException If the syntax is invalid or required fields are missing.
   * @throws EventConflictException  If the new event conflicts with existing events.
   */
  @Override
  public void execute(String command) throws InvalidCommandException, EventConflictException {
    parseCommand(command);
    try {
      model.createCalendar(calendarName, timeZone);
    } catch (IllegalArgumentException | InvalidCommandException e) {
      throw new InvalidCommandException(command + "\nReason : " + e.getMessage());
    }
  }

  /**
   * Parses the create calendar command and extracts the calendar name and time zone.
   *
   * @param command the full user input command
   * @throws InvalidCommandException if calendar name or time zone is missing
   */
  private void parseCommand(String command) {
    Matcher matcher = regexMatching(CREATE_NEW_CALENDAR, command);
    if (matcher.find()) {
      calendarName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      timeZone = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    }

    if (calendarName == null || timeZone == null) {
      throw new InvalidCommandException(command + "\nReason : Required fields are missing.\n");
    }
  }
}
