package calendarapp.view.impl;

import java.io.IOException;
import java.util.List;

import calendarapp.view.ICalendarView;

/**
 * Command Line implementation of the calendar view.
 */
public class CLIView implements ICalendarView {

  private final Appendable out;

  /**
   * Constructs a command-line interface view with the specified output destination.
   *
   * @param out the output destination to which all view operations will be appended.
   */
  public CLIView(Appendable out) {
    this.out = out;
  }

  /**
   * Displays a message.
   *
   * @param message the message to be displayed.
   * @throws RuntimeException if appending to the output destination fails.
   */
  @Override
  public void displayMessage(String message) {
    try {
      out.append(message);
    } catch (IOException e) {
      throw new RuntimeException("Append failed : ", e);
    }
  }

  /**
   * Displays a list of events.
   *
   * @param events the list of events to be displayed.
   * @throws RuntimeException if appending to the output destination fails.
   */
  @Override
  public void displayEvents(List<String> events) {
    for (String event : events) {
      try {
        out.append(event).append("\n");
      } catch (IOException e) {
        throw new RuntimeException("Append failed : ", e);
      }
    }
  }
}