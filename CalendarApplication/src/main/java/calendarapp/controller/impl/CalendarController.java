package calendarapp.controller.impl;

import java.util.Scanner;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

/**
 * Concrete implementation of a calendar controller that processes user commands
 * from an input source and displays results through a view.
 * Supports interactive command processing with continuous input until exit.
 */
public class CalendarController extends AbstractCalendarController {

  /**
   * Creates a new calendar controller with the specified input, model, and view.
   *
   * @param in    The input source for reading commands.
   * @param model The calendar model to operate on.
   * @param view  The view to display information.
   */
  public CalendarController(Readable in, ICalendarModel model, ICalendarView view) {
    super(in, model, view);
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
      try {
        if (command.trim().equalsIgnoreCase("exit")) {
          view.displayMessage("Exiting application.\n");
          break;
        }
        view.displayMessage("Processing command: " + command + "\n");
        processCommand(command);
      } catch (InvalidCommandException e) {
        throw e;
      }
      view.displayMessage("\n");
    }
  }
}