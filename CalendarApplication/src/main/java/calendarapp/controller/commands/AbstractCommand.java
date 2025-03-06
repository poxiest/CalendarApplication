package calendarapp.controller.commands;

import calendarapp.model.ICalendarApplication;
import calendarapp.view.ICalendarView;

public abstract class AbstractCommand implements Command {

  protected ICalendarApplication model;
  protected ICalendarView view;

  AbstractCommand(ICalendarApplication model, ICalendarView view) {
    this.model = model;
    this.view = view;
  }
}
