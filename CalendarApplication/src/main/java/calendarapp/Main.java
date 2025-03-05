package calendarapp;

import java.io.IOException;

import calendarapp.controller.CalendarControllerFactory;
import calendarapp.controller.ICalendarController;
import calendarapp.model.CalendarApplication;

public class Main {
  public static void main(String[] args) {
    if (args.length < 2 || !args[0].equalsIgnoreCase("--mode")) {
      throw new IllegalArgumentException("--mode argument required for the application to run.");
    }

    String mode = args[1];
    String filename = null;
    if (args.length == 3) {
      filename = args[2];
    }

    try {
      ICalendarController controller = CalendarControllerFactory.createController(mode, filename);
      controller.go(new CalendarApplication());
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}