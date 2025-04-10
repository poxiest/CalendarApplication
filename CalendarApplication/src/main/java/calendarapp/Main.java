package calendarapp;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.impl.CalendarControllerFactory;
import calendarapp.controller.impl.GUIController;
import calendarapp.model.ICalendarModel;
import calendarapp.model.impl.CalendarModel;
import calendarapp.view.GUIView;
import calendarapp.view.ICalendarView;
import calendarapp.view.impl.CLIView;
import calendarapp.view.impl.GUIJFrameView;

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
    ICalendarModel model = new CalendarModel();
    ICalendarView view = new CLIView(System.out);

    if (args.length >= 2) {
      String mode = args[1];
      String filename = null;
      if (args.length == 3) {
        filename = args[2];
      }
      ICalendarController controller = null;
      try {
        controller = CalendarControllerFactory.getController(mode,
            filename, model, view);
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
      if (controller != null) {
        controller.start();
      }
    } else if (args.length == 0) {
      GUIController guiController = new GUIController(model);
      GUIView guiView = new GUIJFrameView();
      guiController.setView(guiView);
    } else {
      view.displayMessage("Invalid arguments.");
    }
  }
}