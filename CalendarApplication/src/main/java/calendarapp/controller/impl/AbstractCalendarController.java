package calendarapp.controller.impl;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.InvalidCommandException;
import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.impl.CommandFactory;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

/**
 * Abstract base class for calendar controllers implementing common functionality.
 * Provides a template for handling command processing and interaction with the model and view.
 */
public abstract class AbstractCalendarController implements ICalendarController {

  /**
   * The calendar model that stores and manages calendar data.
   */
  protected ICalendarModel model;

  /**
   * The input source for reading commands.
   */
  protected Readable in;

  /**
   * The view used for displaying information to the user.
   */
  protected ICalendarView view;

  /**
   * Creates a new abstract controller with the specified input, model, and view.
   *
   * @param in    The input source for reading commands.
   * @param model The calendar model to operate on.
   * @param view  The view to display information.
   */
  protected AbstractCalendarController(Readable in, ICalendarModel model, ICalendarView view) {
    this.in = in;
    this.view = view;
    this.model = model;
  }

  /**
   * Processes a command string by creating the appropriate {@link Command} object
   * using the {@link CommandFactory} and executing it.
   *
   * @param commandString The command string to process.
   * @throws InvalidCommandException If the command is invalid or cannot be executed.
   */
  protected void processCommand(String commandString) throws InvalidCommandException {
    Command command;
    try {
      command = CommandFactory.getCommand(commandString, model, view);
    } catch (InvalidCommandException e) {
      throw e;
    }
    command.execute(commandString);
  }
}