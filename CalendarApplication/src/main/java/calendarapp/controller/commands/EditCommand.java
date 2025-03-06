package calendarapp.controller.commands;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class EditCommand extends AbstractCommand {

  EditCommand(ICalendarApplication model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    view.display("Inside EditCommand\n");
  }
}
