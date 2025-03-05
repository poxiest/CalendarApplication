package controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

public class CalendarControllerFactory {
  public static ICalendarController createController(String mode, String filename)
      throws FileNotFoundException {
    switch (mode) {
      case "interactive":
        return new InteractiveCalendarController(new InputStreamReader(System.in), System.out);
      case "headless":
        return new HeadlessCalendarController(new BufferedReader(new FileReader(filename)),
            System.out);
      default:
        throw new IllegalArgumentException("Unknown mode: " + mode);
    }
  }
}
