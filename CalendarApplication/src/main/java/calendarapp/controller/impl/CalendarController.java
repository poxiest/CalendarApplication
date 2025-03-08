package calendarapp.controller.impl;

import java.util.Scanner;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.CommandFactory;
import calendarapp.controller.commands.impl.CommandFactoryImpl;
import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class CalendarController implements ICalendarController {

  protected CommandFactory commandFactory;
  protected ICalendarApplication model;
  protected Readable in;
  protected ICalendarView view;

  public CalendarController(Readable in, ICalendarApplication model, ICalendarView view) {
    this.in = in;
    this.view = view;
    this.model = model;
    commandFactory = new CommandFactoryImpl(model, view);
  }

  @Override
  public void go() {
    String command;
    Scanner scanner = new Scanner(in);

    while (true) {
      view.display("ENTER THE NEXT COMMAND/EXIT:\n");
      command = scanner.nextLine();
      if (command.trim().equalsIgnoreCase("exit")) {
        view.display("Executing: " + command + "\n");
        view.display("Exiting application.\n");
        System.exit(0);
      }
      Command cmnd = commandFactory.createCommand(command);
      if (cmnd != null) {
        view.display("Executing: " + command + "\n");
        cmnd.execute(command);
      }
      view.display("\n");
    }
  }
}
