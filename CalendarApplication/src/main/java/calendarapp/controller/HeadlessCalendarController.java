package calendarapp.controller;

import java.util.Scanner;

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
    while (scanner.hasNextLine()) {
      command = scanner.nextLine();
      processCommand(command);
    }
  }
}
