package calendarapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import calendarapp.controller.CalendarController;
import calendarapp.controller.ICalendarController;
import calendarapp.model.CalendarApplication;
import calendarapp.model.ICalendarApplication;
import calendarapp.view.CLIView;
import calendarapp.view.ICalendarView;

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

    ICalendarApplication model = new CalendarApplication();
    ICalendarView view = new CLIView(System.out);

    try {
      ICalendarController controller;

      switch (mode.toLowerCase()) {
        case "interactive":
          controller = new CalendarController(new InputStreamReader(System.in), model, view);
          break;
        case "headless":
          controller = new CalendarController(new BufferedReader(new FileReader(filename)),
              model, view);
          break;
        default:
          throw new IllegalArgumentException("Unknown mode: " + mode);
      }

      controller.go();
    } catch (FileNotFoundException e) {
      view.display("File not found in path : " + filename + "\n");
    }
  }
}