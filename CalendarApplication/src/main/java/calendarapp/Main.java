package calendarapp;

import java.util.TimeZone;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.impl.CalendarControllerFactory;
import calendarapp.model.calendar.CalendarApplication;
import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;
import calendarapp.view.impl.CLIView;

public class Main {
  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

    if (args.length < 2 || !args[0].equalsIgnoreCase("--mode")) {
      throw new IllegalArgumentException("--mode argument required for the application to run.");
    }

    String mode = args[1];
    String filename = null;
    if (args.length == 3) {
      filename = args[2];
    }

    ICalendarApplication model = new CalendarApplication();
    ICalendarView view = new CLIView(System.out);


    ICalendarController controller = CalendarControllerFactory.getController(mode, filename,
        model, view);
    controller.go();
  }
}