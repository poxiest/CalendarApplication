package commands;

import java.io.IOException;
import java.time.LocalDateTime;

import model.IMultipleCalendarModel;

/**
 * Class that handles all show events heard by the controller
 * and applies the commandComponents to the given multiple calendar
 * model.
 */
public class Show implements IControllerCommand {

  String[] commandComponents;
  IMultipleCalendarModel calendarModel;
  Appendable out;

  /**
   * A constructor for an event command handler which
   * takes in an array of command components, a calendar model,
   * and the current programs output buffer.
   * @param commandComponents the command components as strings.
   * @param calendarModel an instance of the calendar model.
   * @param out the programs output buffer.
   */
  public Show(
      String[] commandComponents, 
      IMultipleCalendarModel calendarModel,
      Appendable out
  ) {
    this.commandComponents = commandComponents;
    this.calendarModel = calendarModel;
    this.out = out;
  }

  public void execute() throws IOException {
    this.handleShow(commandComponents);
  }

  private void handleShow(String[] commandComponents) 
      throws IOException { 
    String menu = "menu: show status on <dateStringTtimeString>\n";
    if (commandComponents.length == 4) {
      if (commandComponents[1].compareTo("status") != 0
          || commandComponents[2].compareTo("on") != 0) {
        throw new IOException("Invalid use of show\n" + menu);
      }
      else {
        LocalDateTime dateTime = LocalDateTime.parse(commandComponents[3]);
        String result = calendarModel.getActiveCalendar().show(dateTime);
        this.out.append(result);
      }
    }
    else {
      throw new IOException("Invalid number of arguments for show\n" + menu);
    }
  }
}