package commands;

import java.io.IOException;

import model.IMultipleCalendarModel;

/**
 * Class that handles all use events heard by the controller
 * and applies the commandComponents to the given multiple calendar
 * model.
 */
public class Use implements IControllerCommand {

  String[] commandComponents;
  IMultipleCalendarModel calendarModel;

  /**
   * A constructor for a copy event handler which takes in
   * an array of command components and an instance of the
   * calendar model.
   * @param commandComponents the command components as strings.
   * @param calendarModel an instance of the calendar model.
   */
  public Use(String[] commandComponents, IMultipleCalendarModel calendarModel) {
    this.commandComponents = commandComponents;
    this.calendarModel = calendarModel;
  }

  public void execute() throws IOException {
    this.handleUse(commandComponents);
  }

  private void handleUse(String[] commandComponents) 
      throws IOException {
    String menu = "use calendar --name <name-of-calendar>";
    if (
        commandComponents.length == 4
        && commandComponents[0].compareTo("use") == 0
        && commandComponents[1].compareTo("calendar") == 0
        && commandComponents[2].compareTo("--name") == 0
    ) {
      String calendarName = commandComponents[3];
      if (! calendarModel.getCalendarKeys().contains(calendarName)) {
        throw new IOException("A calendar with this name does not exist");
      }
      this.calendarModel.useCalendar(calendarName);
    }
    else {
      throw new IOException(menu);
    }
  }
}
