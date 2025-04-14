package commands;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import calendar.ConflictException;
import events.SingleEventProperties;
import model.CalendarProperty;
import model.IMultipleCalendarModel;

/**
 * Class that handles all edit events heard by the controller
 * and applies the commandComponents to the given multiple calendar
 * model.
 */
public class Edit implements IControllerCommand {

  String[] commandComponents;
  IMultipleCalendarModel calendarModel;

  /**
   * A constructor for an edit event handler which takes in
   * an array of command components and an instance of the
   * calendar model.
   * @param commandComponents the command components as strings.
   * @param calendarModel an instance of the calendar model.
   */
  public Edit(String[] commandComponents, IMultipleCalendarModel calendarModel) {
    this.commandComponents = commandComponents;
    this.calendarModel = calendarModel;
  }

  public void execute() throws IOException {
    this.handleEdit(commandComponents);
  }

  private void handleEdit(String[] commandComponents) 
      throws IOException { 
    switch (commandComponents.length) {
      case 10:
        parseEditSingle(commandComponents);
        break;
      case 8:
        parseEditFromDate(commandComponents);
        break;
      case 5:
        parseEditAll(commandComponents);
        break;
      case 7:
        parseEditCalendar(commandComponents);
        break;
      default:
        throw new IOException("Invalid number of arguments for edit");
    }
  }

  private void parseEditCalendar(String[] commandComponents) 
      throws ConflictException, IOException {
    if (commandComponents[2].compareTo("--name") == 0
        && commandComponents[4].compareTo("--property") == 0
    ) {
      String calendarName = commandComponents[3];
      CalendarProperty property = CalendarProperty.valueOf(commandComponents[5].toUpperCase());
      String value = commandComponents[6];
      if (! calendarModel.getCalendarKeys().contains(calendarName)) {
        throw new IOException("A calendar with this name does not exist");
      }
      if (
          property.equals(CalendarProperty.NAME) 
          && calendarModel.getCalendarKeys().contains(calendarName)) {
        throw new IOException("A calendar with this name already exits");
      }
      validateCalendarProperty(property, value);
      calendarModel.editCalendar(calendarName, property, value);
    }
    else {
      String menu = "menu:\n";
      menu += "edit calendar --name <name-of-calendar>";
      menu += " --property <property-name> <new-property-value>\n";
      throw new IOException(menu);
    }

  }

  private void parseEditAll(String[] commandComponents)
      throws ConflictException, IOException {
    if (commandComponents[1].compareTo("events") == 0) {
      SingleEventProperties property = SingleEventProperties
          .valueOf(commandComponents[2].toUpperCase());
      String eventName = commandComponents[3];
      String newPropertyValue = commandComponents[4];
      validateEventProperty(property, newPropertyValue);
      calendarModel.getActiveCalendar().editAll(property, eventName, newPropertyValue);
    }
    else {
      String menu = "menu:\n";
      menu += "edit events <property> <eventName> <newPropertyValue>\n";
      throw new IOException(menu);
    }
  }

  private void parseEditFromDate(String[] commandComponents) 
      throws ConflictException, IOException {
    if (
        commandComponents[1].compareTo("events") == 0
        && commandComponents[4].compareTo("from") == 0
        && commandComponents[6].compareTo("with") == 0
    ) {
      SingleEventProperties property = SingleEventProperties
          .valueOf(commandComponents[2].toUpperCase());
      String eventName = commandComponents[3];
      LocalDateTime startDateTime = LocalDateTime.parse(commandComponents[5]);
      String newPropertyValue = commandComponents[7];
      validateEventProperty(property, newPropertyValue);
      calendarModel.getActiveCalendar()
        .editFromDate(property, eventName, startDateTime, newPropertyValue);
    }
    else {
      String menu = "menu:\n";
      menu += "edit events <property> <eventName> from ";
      menu += "<dateStringTtimeString> with <newPropertyValue>\n";
      throw new IOException("Invalid use of edit from" + menu);
    }
  }

  private void parseEditSingle(String[] commandComponents) 
      throws IOException, ConflictException {
    if (
        commandComponents[1].compareTo("event") == 0 
        && commandComponents[4].compareTo("from") == 0
        && commandComponents[6].compareTo("to") == 0
        && commandComponents[8].compareTo("with") == 0) {
      SingleEventProperties property = SingleEventProperties
          .valueOf(commandComponents[2].toUpperCase());
      String eventName = commandComponents[3];
      LocalDateTime startDateTime = LocalDateTime.parse(commandComponents[5]);
      LocalDateTime endDateTime = LocalDateTime.parse(commandComponents[7]);
      String newPropertyValue = commandComponents[9];
      validateEventProperty(property, newPropertyValue);
      calendarModel.getActiveCalendar()
        .editSingle(property, eventName, startDateTime, endDateTime, newPropertyValue);
    }
    else {
      String menu = "menu:\n";
      menu += "edit event <property> <eventName> from ";
      menu += "<dateStringTtimeString> to <dateStringTtimeString> ";
      menu += "with <newPropertyValue>\n";
      throw new IOException(menu);
    }
  }

  private void validateCalendarProperty(CalendarProperty property, String value) 
      throws IOException {
    if (property.equals(CalendarProperty.TIMEZONE)) {
      ZoneId.of(value);
    }
  }

  private void validateEventProperty(SingleEventProperties property, String value) 
      throws IOException {
    switch (property) {
      case STARTDATE:
      case ENDDATE:
        LocalDate.parse(value);
        break;
      case STARTTIME:
      case ENDTIME:
        LocalTime.parse(value);
        break;
      case ALLDAYEVENT:
      case PRIVATE:
        String lower = value.toLowerCase();
        if (lower.compareTo("true") != 0
            && lower.compareTo("false") != 0) {
          throw new IOException("Boolean property values must be true | false");
        }
        break;
      default:
        break;
    }
  }
}
