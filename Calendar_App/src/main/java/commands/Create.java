package commands;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import calendar.ConflictException;
import model.IMultipleCalendarModel;

/**
 * Class that handles all create events heard by the controller
 * and applies the commandComponents to the given multiple calendar
 * model.
 */
public class Create implements IControllerCommand {

  String[] commandComponents;
  IMultipleCalendarModel calendarModel;

  /**
   * A constructor for a copy event handler which takes in
   * an array of command components and an instance of the
   * calendar model.
   * @param commandComponents the command components as strings.
   * @param calendarModel an instance of the calendar model.
   */
  public Create(String[] commandComponents, IMultipleCalendarModel calendarModel) {
    this.commandComponents = commandComponents;
    this.calendarModel = calendarModel;
  }

  public void execute() throws IOException {
    this.handleCreate(commandComponents);
  }

  private void handleCreate(String[] commandComponents) 
      throws IOException {
    List<String> commandList = new ArrayList<String>(0);
    boolean autoDecline = true;
    commandList.addAll(Arrays.asList(commandComponents));
    if (
        commandList.size() > 1
        && commandList.get(2).compareTo("--autoDecline") == 0
    ) {
      commandList.remove(2);
    }
    switch (commandList.size()) {
      case 7:
        parseCreateSingleEvent(commandList, autoDecline);
        break;
      case 12:
        parseCreateNRecurringEvents(commandList, autoDecline);
        break;
      case 11:
        parseCreateRecurringEventsUntil(commandList, autoDecline);
        break;
      case 5:
        parseCreateSingleAllDayEvent(commandList, autoDecline);
        break;
      case 10:
        parseCreateNRecurringAllDayEvents(commandList, autoDecline);
        break;
      case 9:
        parseCreateRecurringAllDayEventsUntil(commandList, autoDecline);
        break;
      case 6:
        parseCreateCalendar(commandList);
        break;
      default:
        throw new IOException("Invalid number of arguments for create event");
    }
  }

  private void parseCreateCalendar(
      List<String> commandList
  ) throws IOException {
    if (
        commandList.get(1).compareTo("calendar") == 0
        && commandList.get(2).compareTo("--name") == 0
        && commandList.get(4).compareTo("--timezone") == 0
    ) {
      String calendarName = commandList.get(3);
      if (calendarModel.getCalendarKeys().contains(calendarName)) {
        throw new IOException("A calendar already exists with this name");
      }
      ZoneId timeZone = ZoneId.of(commandList.get(5));
      calendarModel.createCalendar(calendarName, timeZone);
    }
    else {
      String menu = "menu:\n";
      menu += "create calendar --name <calName> --timezone area/location";
      throw new IOException(menu);
    }
  }

  private void parseCreateRecurringAllDayEventsUntil(
      List<String> commandList,
      Boolean autoDecline
  ) throws ConflictException, IOException {
    if (
        commandList.get(1).compareTo("event") == 0
        && commandList.get(3).compareTo("on") == 0
        && commandList.get(5).compareTo("repeats") == 0
        && commandList.get(7).compareTo("until") == 0
    ) {
      String eventName = commandList.get(2);
      LocalDate startDate = LocalDate.parse(commandList.get(4));
      List<DayOfWeek> weekdays = getWeekDays(commandList.get(6));
      LocalDate endDate = LocalDate.parse(commandList.get(8));
      if (endDate.isBefore(startDate)) {
        throw new IOException("End date cannot be before start date");
      }
      calendarModel.getActiveCalendar()
        .createRecurringAllDayEventsUntil(eventName, startDate, weekdays, endDate);
    }
    else {
      String menu = "menu:\n";
      menu += "create event <eventName> on <dateString> repeats <weekdays> until <dateString>";
      throw new IOException(menu);
    }
  }

  private void parseCreateNRecurringAllDayEvents(List<String> commandList, Boolean autoDecline)
      throws ConflictException, IOException {
    if (
        commandList.get(1).compareTo("event") == 0
        && commandList.get(3).compareTo("on") == 0
        && commandList.get(5).compareTo("repeats") == 0
        && commandList.get(7).compareTo("for") == 0
        && commandList.get(9).compareTo("times") == 0) {
      String eventName = commandList.get(2);
      LocalDate startDate = LocalDate.parse(commandList.get(4));
      List<DayOfWeek> weekdays = getWeekDays(commandList.get(6));
      int n = Integer.parseInt(commandList.get(8));
      calendarModel.getActiveCalendar()
        .createNRecurringAllDayEvents(eventName, startDate, weekdays, n);
    }
    else {
      String menu = "menu:\n";
      menu += "create event <eventName> on <dateString> repeats <weekdays> for <N> times";
      throw new IOException(menu);
    }
  }

  private void parseCreateSingleAllDayEvent(List<String> commandList, Boolean autoDecline) 
      throws ConflictException, IOException {
    if (
        commandList.get(1).compareTo("event") == 0
        && commandList.get(3).compareTo("on") == 0
    ) {
      String eventName = commandList.get(2);
      LocalDate date = LocalDateTime.parse(commandList.get(4)).toLocalDate();
      calendarModel.getActiveCalendar().createSingleAllDayEvent(
          eventName, 
          date, 
          autoDecline
      );
    }
    else {
      String menu = "menu:\n";
      menu += "create event --autoDecline <eventName> on <dateStringTtimeString>";
      throw new IOException(menu);
    }
  }

  private void parseCreateRecurringEventsUntil(List<String> commandList, Boolean autoDecline)
      throws ConflictException, IOException {
    if (
        commandList.get(1).compareTo("event") == 0
        && commandList.get(3).compareTo("from") == 0
        && commandList.get(5).compareTo("to") == 0
        && commandList.get(7).compareTo("repeats") == 0
        && commandList.get(9).compareTo("until") == 0) {
      String eventName = commandList.get(2);
      LocalDateTime startDateTime = LocalDateTime.parse(commandList.get(4));
      LocalDateTime endDateTime = LocalDateTime.parse(commandList.get(6));
      List<DayOfWeek> weekdays = getWeekDays(commandList.get(8));
      LocalDateTime untilDateTime = LocalDateTime.parse(commandList.get(10));
      if (untilDateTime.isBefore(startDateTime)) {
        throw new IOException("Until date cannot be before the start date");
      }
      calendarModel.getActiveCalendar().createRecurringEventsUntil(
          eventName, 
          startDateTime, 
          endDateTime, 
          weekdays, 
          untilDateTime, 
          autoDecline
      );
    }
    else {
      String menu = "menu:\n";
      menu += "create event --autoDecline <eventName> from <dateStringTtimeString> to";
      menu += " <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>";
      throw new IOException(menu);
    }
  }

  private void parseCreateNRecurringEvents(List<String> commandList, Boolean autoDecline) 
      throws ConflictException, IOException {
    if (
        commandList.get(1).compareTo("event") == 0
        && commandList.get(3).compareTo("from") == 0
        && commandList.get(5).compareTo("to") == 0
        && commandList.get(7).compareTo("repeats") == 0
        && commandList.get(9).compareTo("for") == 0
        && commandList.get(11).compareTo("times") == 0
    ) {
      String eventName = commandList.get(2);
      LocalDateTime startDateTime = LocalDateTime.parse(commandList.get(4));
      LocalDateTime endDateTime = LocalDateTime.parse(commandList.get(6));
      List<DayOfWeek> weekdays = getWeekDays(commandList.get(8));
      int n = Integer.parseInt(commandList.get(10));
      calendarModel.getActiveCalendar().createNRecurringEvents(
          eventName, 
          startDateTime, 
          endDateTime, 
          weekdays, 
          n, 
          autoDecline
      );
    }
    else {
      String menu = "menu:\n";
      menu += "create event --autoDecline <eventName> from <dateStringTtimeString>";
      menu += " to <dateStringTtimeString> repeats <weekdays> for <N> times";
      throw new IOException(menu);
    }
  }

  private void parseCreateSingleEvent(List<String> commandList, Boolean autoDecline) 
      throws ConflictException, IOException {
    if (
        commandList.get(1).compareTo("event") == 0
        && commandList.get(3).compareTo("from") == 0
        && commandList.get(5).compareTo("to") == 0) {
      String eventName = commandList.get(2);
      LocalDateTime startDateTime = LocalDateTime.parse(commandList.get(4));
      LocalDateTime endDateTime = LocalDateTime.parse(commandList.get(6));
      calendarModel.getActiveCalendar().createSingleEvent(
          eventName, 
          startDateTime, 
          endDateTime, 
          autoDecline
      );
    }
    else {
      String menu = "menu:\n";
      menu += "create event --autoDecline <eventName> from ";
      menu += "<dateStringTtimeString> to <dateStringTtimeString>";
      throw new IOException(menu);
    }
  }

  /**
   * Gets a list of weekdays from a weekdays string
   * of the format "MWTRFSU" where characters correspond
   * to the days of the week.
   * @param weekdays a weekdays string.
   * @return a list of weekdays.
   * @throws IOException if a character in the string is invalid.
   */
  public static List<DayOfWeek> getWeekDays(String weekdays) 
      throws IOException {
    List<DayOfWeek> result = new ArrayList<DayOfWeek>(0);
    Map<Character, DayOfWeek> weekDays = new HashMap<Character, DayOfWeek>();
    weekDays.put('M', DayOfWeek.MONDAY);
    weekDays.put('T', DayOfWeek.TUESDAY);
    weekDays.put('W', DayOfWeek.WEDNESDAY);
    weekDays.put('R', DayOfWeek.THURSDAY);
    weekDays.put('F', DayOfWeek.FRIDAY);
    weekDays.put('S', DayOfWeek.SATURDAY);
    weekDays.put('U', DayOfWeek.SUNDAY);
    for (char c: weekdays.toCharArray()) {
      if (! weekDays.containsKey(c)) {
        throw new IOException("Invalid weekday");
      }
      else {
        result.add(weekDays.get(c));
      }
    } 
    return result;
  }
}
