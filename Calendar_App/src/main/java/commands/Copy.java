package commands;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import calendar.ConflictException;
import model.IMultipleCalendarModel;

/**
 * Class that handles all copy events heard by the controller
 * and applies the commandComponents to the given multiple calendar
 * model.
 */
public class Copy implements IControllerCommand {

  String[] commandComponents;
  IMultipleCalendarModel calendarModel;

  /**
   * A constructor for a copy event handler which takes in
   * an array of command components and an instance of the
   * calendar model.
   * @param commandComponents the command components as strings.
   * @param calendarModel an instance of the calendar model.
   */
  public Copy(String[] commandComponents, IMultipleCalendarModel calendarModel) {
    this.commandComponents = commandComponents;
    this.calendarModel = calendarModel;
  }

  public void execute() throws IOException {
    this.handleCopy(commandComponents);
  }

  private void handleCopy(String[] commandComponents) throws IOException {
    String menu = "menu:\n";
    menu += "copy event <eventName> on <dateStringTtimeString>";
    menu += " --target <calendarName> to <dateStringTtimeString>\n";
    menu += "copy events on <dateString> --target <calendarName> to <dateString>\n";
    menu += "copy events between <dateString> and <dateString>";
    menu += " --target <calendarName> to <dateString>";
    switch (commandComponents.length) {
      case 8:
        parseCopyEventsOnDate(commandComponents);
        break;
      case 9:
        parseCopyEventWithName(commandComponents);
        break;
      case 10:
        parseCopyEventsBetween(commandComponents);
        break;
      default:
        throw new IOException(menu);
    }

  }

  private void parseCopyEventsBetween(String[] commandComponents) 
      throws ConflictException {
    if (
        commandComponents[1].compareTo("events") == 0
        && commandComponents[2].compareTo("between") == 0
        && commandComponents[4].compareTo("and") == 0
        && commandComponents[6].compareTo("--target") == 0
        && commandComponents[8].compareTo("to") == 0
    ) {
      LocalDate startDate = LocalDate.parse(commandComponents[3]);
      LocalDate endDate = LocalDate.parse(commandComponents[5]);
      String targetCalendarName = commandComponents[7];
      LocalDate targetDate = LocalDate.parse(commandComponents[9]);
      this.calendarModel.copyEventsBetween(
          startDate, 
          endDate, 
          targetCalendarName, 
          targetDate
      );
    }
  }

  private void parseCopyEventsOnDate(String[] commandComponents) 
      throws ConflictException {
    if (
        commandComponents[1].compareTo("events") == 0
        && commandComponents[2].compareTo("on") == 0
        && commandComponents[4].compareTo("--target") == 0
        && commandComponents[6].compareTo("to") == 0
    ) {
      LocalDate startDate = LocalDate.parse(commandComponents[3]);
      String targetCalendarName = commandComponents[5];
      LocalDate targetDate = LocalDate.parse(commandComponents[7]);
      this.calendarModel.copyEventsOnDate(startDate, targetCalendarName, targetDate);
    }
  }

  private void parseCopyEventWithName(String[] commandComponents) 
      throws ConflictException {
    if (
        commandComponents[1].compareTo("event") == 0
        && commandComponents[3].compareTo("on") == 0
        && commandComponents[5].compareTo("--target") == 0
        && commandComponents[7].compareTo("to") == 0
    ) {
      String eventName = commandComponents[2];
      LocalDateTime startDateTime = LocalDateTime.parse(commandComponents[4]);
      String targetCalendarName = commandComponents[6];
      LocalDateTime targetDateTime = LocalDateTime.parse(commandComponents[8]);
      this.calendarModel.copyEventWithName(
          eventName, 
          startDateTime, 
          targetCalendarName, 
          targetDateTime
      );
    }
  }
}
