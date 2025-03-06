package calendarapp.view;

import java.io.IOException;

public class CLIView implements ICalendarView {

  private final Appendable out;

  /**
   * This constructor initializes the view of this image processing application with the specified
   * output destination.
   *
   * @param out this parameter determines the type of output the view would present to the user.
   */
  public CLIView(Appendable out) {
    this.out = out;
  }

  @Override
  public void display(String message) {
    try {
      out.append(message);
    } catch (IOException e) {
      throw new IllegalStateException("Append failed : ", e);
    }
  }
}
