package calendarapp;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.impl.CalendarControllerFactory;
import calendarapp.model.ICalendarModel;
import calendarapp.model.impl.CalendarModel;
import calendarapp.view.ICalendarView;
import calendarapp.view.impl.CLIView;

/**
 * This is the Main class of this entire application, it serves as the starting point and contains
 * the main method in it, which initializes the model, controller and view and starts the whole
 * process.
 */
public class Main {

  /**
   * The entry point of the calendar application. Initializes the model, view, and
   * controller components, and starts the application.
   *
   * @param args command-line arguments taking mode inputs.
   */
  public static void main(String[] args) {
    if (args.length < 2 || !args[0].equalsIgnoreCase("--mode")) {
      throw new IllegalArgumentException("--mode argument required for the application to run.");
    }

    String mode = args[1];
    String filename = null;
    if (args.length == 3) {
      filename = args[2];
    }

    ICalendarModel model = new CalendarModel();
    ICalendarView view = new CLIView(System.out);
    ICalendarController controller = CalendarControllerFactory.getController(mode,
        filename, model, view);

    controller.start();
  }
}