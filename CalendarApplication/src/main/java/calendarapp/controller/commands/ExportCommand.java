package calendarapp.controller.commands;

import calendarapp.model.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class ExportCommand extends AbstractCommand{

  ExportCommand(ICalendarApplication model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    view.display("Inside ExportCommand\n");
  }
}
