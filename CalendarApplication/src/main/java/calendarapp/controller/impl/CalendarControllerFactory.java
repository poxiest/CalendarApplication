package calendarapp.controller.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import calendarapp.controller.ICalendarController;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

/**
 * Factory class for creating calendar controller instances based on the specified mode.
 */
public class CalendarControllerFactory {
  /**
   * Creates and returns a calendar controller based on the specified mode and parameters.
   *
   * @param mode     The mode to operate in ("interactive" for console input, "headless"
   *                 for file input)
   * @param filename The name of the file containing commands (only used in headless mode)
   * @param model    The calendar model to use
   * @param view     The view to use for output
   * @return A controller instance appropriate for the specified mode
   * @throws IllegalArgumentException If the mode is unknown, the filename is invalid or not found,
   *                                  or if a text file with .txt extension is not provided in
   *                                  headless mode
   */
  public static ICalendarController getController(String mode,
                                                  String filename,
                                                  ICalendarModel model,
                                                  ICalendarView view) {
    switch (mode.toLowerCase()) {
      case "interactive":
        return new CalendarController(new InputStreamReader(System.in), model, view);
      case "headless":
        try {
          String[] filenameSplit = filename.split("\\.");
          if (filenameSplit.length <= 1
              || !filenameSplit[filenameSplit.length-1].equalsIgnoreCase("txt")) {
            throw new IllegalArgumentException("Only txt files are supported.");
          }
          return new CalendarController(new BufferedReader(new FileReader(filename)),
              model, view);
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