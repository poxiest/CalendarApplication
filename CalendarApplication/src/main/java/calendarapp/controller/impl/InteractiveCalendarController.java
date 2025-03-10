package calendarapp.controller.impl;

import java.util.Scanner;

import calendarapp.controller.InvalidCommandFileException;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

public class InteractiveCalendarController extends AbstractCalendarController {

  InteractiveCalendarController(Readable in, ICalendarModel model, ICalendarView out) {
    super(in, model, out);
  }

  @Override
  public void go() {
    String command;
    Scanner scanner = new Scanner(in);

    while (true) {
      view.displayMessage("Enter command or enter 'exit' to exit the calendar application.\n");
      command = scanner.nextLine();
      try {
        if (command.trim().equalsIgnoreCase("exit")) {
          view.displayMessage("Exiting application.\n");
          break;
        }
        processCommand(command);
      } catch (InvalidCommandFileException e) {
        view.displayMessage(e.getMessage() + '\n');
      }
    }
  }
}