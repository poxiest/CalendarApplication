package controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import events.IEvent;
import events.SingleEventProperties;
import model.CalendarProperty;
import model.IMultipleCalendarModel;

/**
 * This is the concrete class that implements the 
 * IController interface.
 */
public class MockController implements IController {

  private Readable in;
  private Appendable out;
  private IMultipleCalendarModel calendarModel;
  private String menu;

  /**
   * A default constructor which initializes in and out
   * to be System.in and System.out.
   * @param calendarModel the calendarModel used to perform operations.
   * @param args the arguments passed in by the user when the program is run.
   * @throws IOException if an IOException occurs based on user input.
   */
  public MockController(
      IMultipleCalendarModel calendarModel, 
      Readable in,
      Appendable out,
      String[] args
  ) throws IOException {
    this.calendarModel = calendarModel;
    this.in = in;
    this.out = out;
    menu = "menu:\nTo run in interactive mode:\n";
    menu += "java CalendarApp.java --mode interactive\n\n";
    menu += "To run in headless mode:\n";
    menu += "java CalendarApp.java --mode headless <pathToCommandTextFile>";
    handleSetup(args);
  }

  @Override
  public void execute() 
      throws IOException { 
    Objects.requireNonNull(calendarModel);
    Scanner scan = new Scanner(this.in);
    boolean acceptingInput = true;
    while (acceptingInput) {
      String command = scan.nextLine();
      acceptingInput = handleCommand(command);
    }
    scan.close();
  }

  private void handleSetup(String[] args) throws IOException {
    try {
      String flag = args[0].toLowerCase();
      String mode = args[1].toLowerCase();
      if (flag.compareTo("--mode") == 0
          && mode.compareTo("interactive") == 0) {
        out.append("Now running calendar in interactive mode.\n");
      }
      else if (flag.compareTo("--mode") == 0
          && mode.compareTo("headless") == 0) {
        String pathToCommandTextFile = args[2];
        File commandFile = new File(pathToCommandTextFile);
        this.in = new FileReader(commandFile);
        this.out.append("Now running calendar in headless mode.\n");
      }
      else {
        throw new IOException();
      }
    }
    catch (Exception ex) {
      throw new IOException(menu);
    }
  }
  
  private boolean handleCommand(String command) 
      throws IOException {
    String[] commandComponents = command.split(" ");
    switch (commandComponents[0]) {
      case "create":
        handleCreate(commandComponents);
        break;
      case "edit":
        handleEdit(commandComponents);
        break;
      case "print":
        handlePrint(commandComponents);
        break;
      case "export":
        handleExport(commandComponents);
        break;
      case "show":
        handleShow(commandComponents);
        break;
      case "copy":
        handleCopy(commandComponents);
        break;
      case "use":
        handleUse(commandComponents);
        break;
      case "exit":
        return false;
      default:
        throw new IOException("Invalid command action: " + commandComponents[0]);
    }
    return true;
  }

  private void handleCopy(String[] commandComponents) throws IOException {
    String menu = "menu:\n";
    menu += "copy event <eventName> on <dateStringTtimeString>";
    menu += " --target <calendarName> to <dateStringTtimeString>\n";
    menu += "copy events on <dateString> --target <calendarName> to <dateString>\n";
    menu += "copy events between <dateString> and <dateString> ";
    menu += "--target <calendarName> to <dateString>";
    if (
        commandComponents.length == 9
        && commandComponents[1].compareTo("event") == 0
        && commandComponents[3].compareTo("on") == 0
        && commandComponents[5].compareTo("--target") == 0
        && commandComponents[7].compareTo("to") == 0
    ) {
      String eventName = commandComponents[2];
      LocalDateTime startDateTime = LocalDateTime.parse(commandComponents[4]);
      String targetCalendarName = commandComponents[6];
      LocalDateTime targetDateTime = LocalDateTime.parse(commandComponents[8]);
      this.out.append(eventName + ", " + startDateTime + ", " 
          + targetCalendarName + ", " + targetDateTime);
    }
    else if (
        commandComponents.length == 8
        && commandComponents[1].compareTo("events") == 0
        && commandComponents[2].compareTo("on") == 0
        && commandComponents[4].compareTo("--target") == 0
        && commandComponents[6].compareTo("to") == 0
    ) {
      LocalDate startDate = LocalDate.parse(commandComponents[3]);
      String targetCalendarName = commandComponents[5];
      LocalDate targetDate = LocalDate.parse(commandComponents[7]);
      this.calendarModel.copyEventsOnDate(startDate, targetCalendarName, targetDate);
      this.out.append(startDate + ", " + targetCalendarName + ", " + targetDate);
    }
    else if (
        commandComponents.length == 10
        && commandComponents[1].compareTo("events") == 0
        && commandComponents[2].compareTo("between") == 0
        && commandComponents[4].compareTo("and") == 0
        && commandComponents[6].compareTo("--target") == 0
        && commandComponents[8].compareTo("to") == 0
    ) {
      LocalDate startDate = LocalDate.parse(commandComponents[3]);
      LocalDate endDate = LocalDate.parse(commandComponents[5]);
      String targetCalendarName = commandComponents[7];
      LocalDate targetDate = LocalDate.parse(commandComponents[9]);
      this.out.append(startDate + ", " + endDate + ", " + targetCalendarName + ", " + targetDate);
    }
    else {
      throw new IOException(menu);
    }

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
      this.out.append(calendarName);
    }
    else {
      throw new IOException(menu);
    }
  }

  private void handleCreate(String[] commandComponents) 
      throws IOException {
    List<String> commandList = new ArrayList<String>(0);
    commandList.addAll(Arrays.asList(commandComponents));
    if (commandList.get(1).compareTo("event") == 0) {
      if (commandList.get(2).compareTo("--autoDecline") == 0) {
        commandList.remove(2);
      }
      switch (commandList.size()) {
        case 7:
          if (commandList.get(3).compareTo("from") == 0
              && commandList.get(5).compareTo("to") == 0) {
            String eventName = commandList.get(2);
            LocalDateTime startDateTime = LocalDateTime.parse(commandList.get(4));
            LocalDateTime endDateTime = LocalDateTime.parse(commandList.get(6));
            this.out.append(eventName + ", " + startDateTime + ", " + endDateTime);
          }
          else {
            String menu = "menu:\n";
            menu += "create event --autoDecline <eventName> from ";
            menu += "<dateStringTtimeString> to <dateStringTtimeString>";
            throw new IOException(menu);
          }
          break;
        case 12:
          if (commandList.get(3).compareTo("from") == 0
              && commandList.get(5).compareTo("to") == 0
              && commandList.get(7).compareTo("repeats") == 0
              && commandList.get(9).compareTo("for") == 0
              && commandList.get(11).compareTo("times") == 0) {
            String eventName = commandList.get(2);
            LocalDateTime startDateTime = LocalDateTime.parse(commandList.get(4));
            LocalDateTime endDateTime = LocalDateTime.parse(commandList.get(6));
            List<DayOfWeek> weekdays = getWeekDays(commandList.get(8));
            int n = Integer.parseInt(commandList.get(10));
            this.out.append(eventName + ", " + startDateTime + ", " 
                + endDateTime + ", " + weekdays + ", " + n);
          }
          else {
            String menu = "menu:\n";
            menu += "create event --autoDecline <eventName> from <dateStringTtimeString>";
            menu += " to <dateStringTtimeString> repeats <weekdays> for <N> times";
            throw new IOException(menu);
          }
          break;
        case 11:
          if (commandList.get(3).compareTo("from") == 0
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
            this.out.append(eventName + ", " + startDateTime + ", " 
                + endDateTime + ", " + weekdays + ", " + untilDateTime);
          }
          else {
            String menu = "menu:\n";
            menu += "create event --autoDecline <eventName> from <dateStringTtimeString> to";
            menu += " <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>";
            throw new IOException(menu);
          }
          break;
        case 5:
          if (commandList.get(3).compareTo("on") == 0) {
            String eventName = commandList.get(2);
            LocalDate date = LocalDateTime.parse(commandList.get(4)).toLocalDate();
            this.out.append(eventName + ", " + date);
          }
          else {
            String menu = "menu:\n";
            menu += "create event --autoDecline <eventName> on <dateStringTtimeString>";
            throw new IOException(menu);
          }
          break;
        case 10:
          if (commandList.get(3).compareTo("on") == 0
              && commandList.get(5).compareTo("repeats") == 0
              && commandList.get(7).compareTo("for") == 0
              && commandList.get(9).compareTo("times") == 0) {
            String eventName = commandList.get(2);
            LocalDate startDate = LocalDate.parse(commandList.get(4));
            List<DayOfWeek> weekdays = getWeekDays(commandList.get(6));
            int n = Integer.parseInt(commandList.get(8));
            calendarModel.getActiveCalendar()
              .createNRecurringAllDayEvents(eventName, startDate, weekdays, n);
            this.out.append(eventName + ", " + startDate + ", " + weekdays + ", " + n);

          }
          else {
            String menu = "menu:\n";
            menu += "create event <eventName> on <dateString> repeats <weekdays> for <N> times";
            throw new IOException(menu);
          }
          break;
        case 9:
          if (commandList.get(3).compareTo("on") == 0
              && commandList.get(5).compareTo("repeats") == 0
              && commandList.get(7).compareTo("until") == 0) {
            String eventName = commandList.get(2);
            LocalDate startDate = LocalDate.parse(commandList.get(4));
            List<DayOfWeek> weekdays = getWeekDays(commandList.get(6));
            LocalDate endDate = LocalDate.parse(commandList.get(8));
            if (endDate.isBefore(startDate)) {
              throw new IOException("End date cannot be before start date");
            }
            this.out.append(eventName + ", " + startDate + ", " + weekdays + ", " + endDate);
          }
          else {
            String menu = "menu:\n";
            menu += "create event <eventName> on <dateString> repeats"
                + " <weekdays> until <dateString>";
            throw new IOException(menu);
          }
          break;
        default:
          throw new IOException("Invalid number of arguments for create event");
      }
    }
    else if (commandList.get(1).compareTo("calendar") == 0) {
      if (commandList.size() == 6) {
        if (commandList.get(2).compareTo("--name") == 0
            && commandList.get(4).compareTo("--timezone") == 0) {
          String calendarName = commandList.get(3);
          ZoneId timeZone = ZoneId.of(commandList.get(5));
          this.out.append(calendarName + ", " + timeZone);
        }
        else {
          String menu = "menu:\n";
          menu += "create calendar --name <calName> --timezone area/location";
          throw new IOException(menu);
        }
      }
      else {
        throw new IOException("Invalid number of arguments for create calendar");
      }
    }
    else {
      throw new IOException("Invalid usaage of create");
    }
  }

  private void handleEdit(String[] commandComponents) 
      throws IOException { 
    String menu = "menu:\n";
    String editSingleMenu = "edit event <property> <eventName> from ";
    editSingleMenu += "<dateStringTtimeString> to <dateStringTtimeString> ";
    editSingleMenu += "with <newPropertyValue>\n";
    String editFromDateMenu = "edit events <property> <eventName> from ";
    editFromDateMenu += "<dateStringTtimeString> with <newPropertyValue>\n";
    String editAllMenu = "edit events <property> <eventName> <newPropertyValue>\n";
    String calendarMenu = "menu:\n";
    calendarMenu += "edit calendar --name <name-of-calendar>";
    calendarMenu += " --property <property-name> <new-property-value>\n";
    menu += editSingleMenu + editFromDateMenu + editAllMenu + calendarMenu;
    if (commandComponents.length >= 2) {
      switch (commandComponents[1]) {
        case "event":
          if (commandComponents.length != 10) {
            String message = "Invalid number of arguments for edit single\n";
            message += editSingleMenu;
            throw new IOException(message);
          }
          else {
            if (commandComponents[4].compareTo("from") == 0
                && commandComponents[6].compareTo("to") == 0
                && commandComponents[8].compareTo("with") == 0) {
              SingleEventProperties property = SingleEventProperties
                  .valueOf(commandComponents[2].toUpperCase());
              String eventName = commandComponents[3];
              LocalDateTime startDateTime = LocalDateTime.parse(commandComponents[5]);
              LocalDateTime endDateTime = LocalDateTime.parse(commandComponents[7]);
              String newPropertyValue = commandComponents[9];
              validateEventProperty(property, newPropertyValue);
              this.out.append(property + ", " + eventName + ", " 
                  + startDateTime + ", " + endDateTime + ", " + newPropertyValue);
            }
            else {
              throw new IOException("Invalid use of edit single" + editSingleMenu);
            }
          }
          break;
        case "events":
          if (commandComponents.length == 8) {
            if (commandComponents[4].compareTo("from") == 0
                && commandComponents[6].compareTo("with") == 0) {
              SingleEventProperties property = SingleEventProperties
                  .valueOf(commandComponents[2].toUpperCase());
              String eventName = commandComponents[3];
              LocalDateTime startDateTime = LocalDateTime.parse(commandComponents[5]);
              String newPropertyValue = commandComponents[7];
              validateEventProperty(property, newPropertyValue);
              this.out.append(property + ", " + eventName + ", " 
                  + startDateTime + ", " + newPropertyValue);
            }
            else {
              throw new IOException("Invalid use of edit from" + editFromDateMenu);
            }
          }
          else if (commandComponents.length == 5) {
            SingleEventProperties property = SingleEventProperties
                .valueOf(commandComponents[2].toUpperCase());
            String eventName = commandComponents[3];
            String newPropertyValue = commandComponents[4];
            validateEventProperty(property, newPropertyValue);
            this.out.append(property + ", " + eventName 
                + ", " + newPropertyValue);
          }
          else {
            throw new IOException(
              "Invalid number of arguments passed to edit events\n"
              + editFromDateMenu + editAllMenu
            );
          }
          break;
        case "calendar":
          if (commandComponents.length == 7) {
            if (commandComponents[2].compareTo("--name") == 0
                && commandComponents[4].compareTo("--property") == 0) {
              String calendarName = commandComponents[3];
              CalendarProperty property = CalendarProperty
                  .valueOf(commandComponents[5].toUpperCase());
              String value = commandComponents[6];
              validateCalendarProperty(property, value);
              this.out.append(calendarName + ", " + property + ", " + value);
            }
            else {
              throw new IOException(calendarMenu);
            }
          }
          else {
            throw new IOException(
              "Invalid number of arguments passed to edit calendar\n"
            );
          }
          break;
        default:
          throw new IOException("Invalid use of edit" + menu);
      }
    }
    else {
      throw new IOException("Invalid number of arguments for edit" + menu);
    }
  }

  private void handlePrint(String[] commandComponents) 
      throws IOException { 
    String menu = "menu:\n";
    String onMenu = "print events on <dateString>\n";
    String fromMenu = "print events from <dateStringTtimeString> to <dateStringTtimeString>\n";
    menu += onMenu + fromMenu;
    List<String> validActions = Arrays.asList(
        "on",
        "from"
    );
    if (commandComponents.length >= 4) {
      String action = commandComponents[2];
      if (commandComponents[1].compareTo("events") == 0
          && validActions.contains(action)) {
        switch (action) {
          case "on":
            if (commandComponents.length != 4) {
              throw new IOException(
              "Invalid number of arguments for on action\n" + onMenu);
            }
            else {
              LocalDate date = LocalDate.parse(commandComponents[3]);
              this.out.append(date.toString());
            }
            break;
          case "from":
            if (commandComponents.length != 6) {
              throw new IOException(
              "Invalid number of arguments for from/to action\n" + fromMenu);
            }
            else {
              if (commandComponents[4].compareTo("to") != 0) {
                throw new IOException("Invalid use of from/to action\n" + fromMenu);
              }
              else {
                LocalDateTime startDateTime = LocalDateTime.parse(commandComponents[3]);
                LocalDateTime endDateTime = LocalDateTime.parse(commandComponents[5]);
                this.out.append(startDateTime + ", " + endDateTime);
              }
            }
            break;
          default:
            throw new IOException("Invalid use of print\n" + menu);
        }  
      }
      else {
        throw new IOException("Invalid number of arguments for print\n" + menu);
      }
    }
    else {
      throw new IOException("Invalid use of print\n" + menu);
    }
  }

  private void handleExport(String[] commandComponents) 
      throws IOException { 
    if (commandComponents.length == 3) {
      String fileName = commandComponents[2];
      String menu = "menu: export cal filename.csv\n";
      if (commandComponents[1].compareTo("cal") != 0) {
        throw new IOException("Invalid use of export\n" + menu);
      }
      else {
        if (fileName.endsWith(".csv")) {
          this.out.append(fileName + "\n");
          String absolutePath = exportToCSV(fileName);
          this.out.append(absolutePath);
        }
        else {
          throw new IOException("Invalid file extension\n" + menu);
        }
      }
    }
    else {
      throw new IOException("Invalid number of arguments for export\n" + menu);
    }
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
        this.out.append(dateTime.toString());
      }
    }
    else {
      throw new IOException("Invalid number of arguments for show\n" + menu);
    }
  }

  private List<DayOfWeek> getWeekDays(String weekdays) 
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

  private String exportToCSV(String fileName) throws IOException {
    List<IEvent> events = calendarModel
        .getActiveCalendar()
        .queryDateRange(LocalDateTime.MIN, LocalDateTime.MAX);
    File exportFile = new File(fileName);
    FileWriter fileWriter = new FileWriter(exportFile);
    String header = "Subject,Start Date,Start Time,End Date,"
        + "End Time,All Day Event,Description,Location,Private\n";
    fileWriter.append(header);
    for (IEvent event: events) {
      String row = event.getSubject() + ",";
      row += event.getStartDate() + ",";
      row += event.getStartTime() + ",";
      row += event.getEndDate() + ",";
      row += event.getEndTime() + ",";
      row += event.isAllDayEvent() + ",";
      row += event.getDescription() + ",";
      row += event.getLocation() + ",";
      row += event.isPrivate();
      row = row.replace("null", "");
      fileWriter.append(row += "\n");
    }
    fileWriter.flush();
    fileWriter.close();
    return exportFile.getAbsolutePath();
  }

}
