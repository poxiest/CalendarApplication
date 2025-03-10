package calendarapp.controller.impl;

import java.util.Scanner;

import calendarapp.controller.InvalidCommandFileException;
import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class HeadlessCalendarController extends AbstractCalendarController {

  HeadlessCalendarController(Readable in, ICalendarApplication model, ICalendarView view) {
    super(in, model, view);
  }

  @Override
  public void go() {
    String command;
    Scanner scanner = new Scanner(in);

    while (true) {
      if (!scanner.hasNextLine()) {
        throw new InvalidCommandFileException("Exit command is expected in the end!");
      }
      command = scanner.nextLine();
      if (command.trim().equalsIgnoreCase("exit")) {
        view.displayMessage("Exiting Application");
        return;
      }
      processCommand(command);
    }
  }
}
