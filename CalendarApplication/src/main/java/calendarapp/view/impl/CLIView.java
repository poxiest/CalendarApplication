package calendarapp.view.impl;

import java.io.IOException;

import calendarapp.view.ICalendarView;

public class CLIView implements ICalendarView {

  private final Appendable out;

  public CLIView(Appendable out) {
    this.out = out;
  }

  @Override
  public void displayMessage(String message) {
    try {
      out.append(message);
    } catch (IOException e) {
      throw new RuntimeException("Append failed : ", e);
    }
  }
}
