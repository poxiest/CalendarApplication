package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendarapp.controller.commands.Command;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

/**
 * Abstract base class for all command implementations.
 * Provides common functionality and fields needed by command classes.
 */
public abstract class AbstractCommand implements Command {

  /**
   * The calendar model that command operations will be performed on.
   * */
  protected ICalendarModel model;

  /**
   * The view used to display information to the user.
   * */
  protected ICalendarView view;

  /**
   * Creates a new command with the specified model and view.
   *
   * @param model The calendar model to use for operations
   * @param view The view to use for displaying information
   */
  AbstractCommand(ICalendarModel model, ICalendarView view) {
    this.model = model;
    this.view = view;
  }

  /**
   * Performs regex pattern matching on the command string.
   *
   * @param regexPattern The regular expression pattern to match
   * @param command The command string to apply the pattern to
   * @return A Matcher object that can be used to extract matched groups
   */
  protected Matcher regexMatching(String regexPattern, String command) {
    Pattern pattern = Pattern.compile(regexPattern);
    return pattern.matcher(command.trim());
  }
}