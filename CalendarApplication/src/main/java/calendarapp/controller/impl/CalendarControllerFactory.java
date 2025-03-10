package calendarapp.controller.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import calendarapp.controller.ICalendarController;
import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class CalendarControllerFactory {
  public static ICalendarController getController(String mode,
                                                  String filename,
                                                  ICalendarApplication model,
                                                  ICalendarView view) {
    switch (mode.toLowerCase()) {
      case "interactive":
        return new InteractiveCalendarController(new InputStreamReader(System.in), model, view);
      case "headless":
        try {
          return new HeadlessCalendarController(new BufferedReader(new FileReader(filename)), model,
              view);
        } catch (FileNotFoundException e) {
          throw new IllegalArgumentException("File is not found at " + filename);
        } catch (NullPointerException e) {
          throw new IllegalArgumentException("Filename cannot be null.");
        }
      default:
        throw new IllegalArgumentException("Unknown mode: " + mode);
    }
  }
}