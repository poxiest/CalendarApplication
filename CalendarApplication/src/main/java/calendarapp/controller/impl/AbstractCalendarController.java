package calendarapp.controller.impl;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.impl.CommandFactory;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

public abstract class AbstractCalendarController implements ICalendarController {

  protected ICalendarModel model;
  protected Readable in;
  protected ICalendarView view;

  protected AbstractCalendarController(Readable in, ICalendarModel model, ICalendarView view) {
    this.in = in;
    this.view = view;
    this.model = model;
  }

  protected void processCommand(String commandString) {
    Command command = CommandFactory.getCommand(commandString, model, view);
    if (command != null) {
      command.execute(commandString);
    }
  }
}