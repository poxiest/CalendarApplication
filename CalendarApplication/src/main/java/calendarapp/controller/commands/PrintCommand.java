package calendarapp.controller.commands;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class PrintCommand extends AbstractCommand {

  PrintCommand(ICalendarApplication model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    view.display("Inside PrintCommand\n");
  }
}