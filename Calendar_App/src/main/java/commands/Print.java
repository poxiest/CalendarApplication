package commands;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import model.IMultipleCalendarModel;

/**
 * Class that handles all print events heard by the controller
 * and applies the commandComponents to the given multiple calendar
 * model.
 */
public class Print implements IControllerCommand {

  String[] commandComponents;
  IMultipleCalendarModel calendarModel;
  Appendable out;

  /**
   * A constructor for a print command handler which
   * takes in an array of command components, a calendar model,
   * and the current programs output buffer.
   * @param commandComponents the command components as strings.
   * @param calendarModel an instance of the calendar model.
   * @param out the programs output buffer.
   */
  public Print(
      String[] commandComponents, 
      IMultipleCalendarModel calendarModel,
      Appendable out
  ) {
    this.commandComponents = commandComponents;
    this.calendarModel = calendarModel;
    this.out = out;
  }

  public void execute() throws IOException {
    this.handlePrint(commandComponents);
  }

  private void handlePrint(String[] commandComponents) 
      throws IOException { 
    switch (commandComponents.length) {
      case 4:
        parsePrintEventsOnDate(commandComponents);
        break;
      case 6:
        parsePrintEventsInRange(commandComponents);
        break;
      default:
        throw new IOException("Invalid number of arguments for print\n");
    }
  }

  private void parsePrintEventsInRange(String[] commandComponents) 
      throws IOException {
    if (
        commandComponents[1].compareTo("events") == 0
        && commandComponents[2].compareTo("from") == 0
        && commandComponents[4].compareTo("to") == 0
    ) {
      LocalDateTime startDateTime = LocalDateTime.parse(commandComponents[3]);
      LocalDateTime endDateTime = LocalDateTime.parse(commandComponents[5]);
      String result = calendarModel.getActiveCalendar()
          .printEventsFromTo(startDateTime, endDateTime);
      this.out.append(result);
    }
    else {
      String menu = "menu:\n";
      menu += "print events from <dateStringTtimeString> to <dateStringTtimeString>\n";
      throw new IOException(menu);
    }
  }

  private void parsePrintEventsOnDate(String[] commandComponents) 
      throws IOException {
    if (
        commandComponents[1].compareTo("events") == 0
        && commandComponents[2].compareTo("on") == 0 
    ) {
      LocalDate date = LocalDate.parse(commandComponents[3]);
      String result = calendarModel.getActiveCalendar().printEventsOnDate(date);
      this.out.append(result);
    }
    else {
      String menu = "menu:\n";
      menu += "print events on <dateString>\n";
      throw new IOException(menu);
    }
  }
}