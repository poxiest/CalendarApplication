package calendarapp.controller;

import calendarapp.model.ICalendarApplication;

public class InteractiveCalendarController implements ICalendarController {

  private Readable in;
  private Appendable out;

  public InteractiveCalendarController(Readable in, Appendable out) {
    this.in = in;
    this.out = out;
  }

  @Override
  public void go(ICalendarApplication calendarApplication) {

  }
}
