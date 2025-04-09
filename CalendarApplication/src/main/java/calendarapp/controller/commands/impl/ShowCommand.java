package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.Constants.STATUS_ON_PATTERN;

/**
 * Show Command implementation for showing calendar status at a specific date and time.
 * Parses the show command and displays the status through the view.
 */
public class ShowCommand extends AbstractCommand {

  /**
   * The date and time to show status for.
   */
  private String on;

  /**
   * Creates a new ShowCommand with the specified model and view.
   *
   * @param model The calendar model to retrieve status from.
   * @param view  The view to use for displaying status.
   */
  ShowCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Executes the show command by parsing the command string,
   * extracting the date and time, retrieving the status from the model,
   * and displaying it through the view.
   *
   * @param command The command string containing show instructions.
   * @throws InvalidCommandException If the syntax is invalid or required fields are missing.
   */
  @Override
  public void execute(String command) throws InvalidCommandException {
    Matcher matcher = regexMatching(STATUS_ON_PATTERN, command);
    if (matcher.find()) {
      on = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    if (on == null) {
      throw new InvalidCommandException(command + "\nReason : Required fields are missing.\n");
    }

    view.displayMessage("Status: " + model.showStatus(on) + "\n");
  }
}