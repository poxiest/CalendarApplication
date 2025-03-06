package calendarapp.controller.commands;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class ShowCommand extends AbstractCommand {

  ShowCommand(ICalendarApplication model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    view.display("Inside ShowCommand\n");
  }
}
