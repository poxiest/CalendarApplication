package calendarapp.controller;

import java.util.Scanner;

import calendarapp.model.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class InteractiveCalendarController extends AbstractCalendarController {

  InteractiveCalendarController(Readable in, ICalendarApplication model, ICalendarView out) {
    super(in, model, out);
  }

  @Override
  public void go() {
    String command;
    Scanner scanner = new Scanner(in);
    while (true) {
      view.display("Enter command or enter 'exit' to exit the calendar application.\n");
      command = scanner.nextLine();
      if (command.equals("exit")) {
        break;
      }
      processCommand(command);
    }
  }
}
