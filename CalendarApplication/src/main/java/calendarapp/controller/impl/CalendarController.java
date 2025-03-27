package calendarapp.controller.impl;

import java.util.Scanner;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.InvalidCommandException;
import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.impl.CommandFactory;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

/**
 * Concrete implementation of a calendar controller that processes user commands
 * from an input source and displays results through a view.
 * Supports interactive command processing with continuous input until exit.
 */
public class CalendarController implements ICalendarController {

  /**
   * The calendar model that stores and manages calendar data.
   */
  private ICalendarModel model;

  /**
   * The input source for reading commands.
   */
  private Readable in;

  /**
   * The view used for displaying information to the user.
   */
  private ICalendarView view;

  /**
   * Creates a new calendar controller with the specified input, model, and view.
   *
   * @param in    The input source for reading commands.
   * @param model The calendar model to operate on.
   * @param view  The view to display information.
   */
  public CalendarController(Readable in, ICalendarModel model, ICalendarView view) {
    this.in = in;
    this.view = view;
    this.model = model;
  }

  /**
   * Starts the controller and begins processing user commands.
   * Continuously reads commands from the input source until an "exit" command is received.
   * Displays prompts and results through the view.
   *
   * @throws InvalidCommandException If a command is invalid or if the input source ends
   *                                 without an "exit" command.
   */
  @Override
  public void start() throws InvalidCommandException {
    String command;
    Scanner scanner = new Scanner(in);

    while (true) {
      view.displayMessage("Enter command or enter 'exit' to exit the calendar application.\n");
      if (!scanner.hasNextLine()) {
        throw new InvalidCommandException("Command file must end with 'exit' command./n");
      }
      command = scanner.nextLine();
      if (command.trim().equalsIgnoreCase("exit")) {
        view.displayMessage("Exiting application.\n");
        break;
      }
      view.displayMessage("Processing command: " + command + "\n");
      try {
        processCommand(command);
      } catch (Exception e) {
        view.displayMessage("\nENCOUNTERED ERROR : " + e.getMessage() + "\n");
        view.displayMessage("Exiting application gracefully.\n");
        break;
      }
      view.displayMessage("\n");
    }
  }

  /**
   * Processes a command string by creating the appropriate {@link Command} object
   * using the {@link CommandFactory} and executing it.
   *
   * @param commandString The command string to process.
   * @throws InvalidCommandException If the command is invalid or cannot be executed.
   */
  private void processCommand(String commandString) throws InvalidCommandException {
    Command command;
    command = CommandFactory.getCommand(commandString, model, view);
    command.execute(commandString);
  }
}