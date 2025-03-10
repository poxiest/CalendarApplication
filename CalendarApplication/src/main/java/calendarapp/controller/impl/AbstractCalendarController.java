package calendarapp.controller.impl;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.CommandFactory;
import calendarapp.controller.commands.impl.CommandFactoryImpl;
import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public abstract class AbstractCalendarController implements ICalendarController {

  protected CommandFactory commandFactory;
  protected ICalendarApplication model;
  protected Readable in;
  protected ICalendarView view;

  protected AbstractCalendarController(Readable in, ICalendarApplication model, ICalendarView view) {
    this.in = in;
    this.view = view;
    this.model = model;
    commandFactory = new CommandFactoryImpl(model, view);
  }

  protected void processCommand(String commandString) {
    Command command = commandFactory.getCommand(commandString);
    if (command != null) {
      command.execute(commandString);
    }
  }
}