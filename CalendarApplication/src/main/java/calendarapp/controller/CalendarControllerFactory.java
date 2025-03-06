package calendarapp.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class CalendarControllerFactory {
  public static ICalendarController createController(String mode,
                                                     String filename,
                                                     ICalendarApplication model,
                                                     ICalendarView view)
      throws FileNotFoundException {
    switch (mode.toLowerCase()) {
      case "interactive":
        return new InteractiveCalendarController(new InputStreamReader(System.in), model, view);
      case "headless":
        return new HeadlessCalendarController(new BufferedReader(new FileReader(filename)), model,
            view);
      default:
        throw new IllegalArgumentException("Unknown mode: " + mode);
    }
  }
}
