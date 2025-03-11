package calendarapp.controller.impl;

import java.util.Scanner;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

public class CalendarController extends AbstractCalendarController {

  public CalendarController(Readable in, ICalendarModel model, ICalendarView view) {
    super(in, model, view);
  }

  @Override
  public void go() {
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
        throw new InvalidCommandException("Invalid command: " + command + "\n");
      }
      view.displayMessage("\n");
    }
  }
}
