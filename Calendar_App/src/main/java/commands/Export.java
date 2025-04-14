package commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import events.IEvent;
import model.IMultipleCalendarModel;

/**
 * Class that handles all export events heard by the controller
 * and applies the commandComponents to the given multiple calendar
 * model.
 */
public class Export implements IControllerCommand {

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
  public Export(
      String[] commandComponents, 
      IMultipleCalendarModel calendarModel,
      Appendable out
  ) {
    this.commandComponents = commandComponents;
    this.calendarModel = calendarModel;
    this.out = out;
  }

  public void execute() throws IOException {
    this.handleExport(commandComponents);
  }

  private void handleExport(String[] commandComponents) 
      throws IOException { 
    String menu = "menu: export cal filename.csv\n";
    if (commandComponents.length == 3) {
      String fileName = commandComponents[2];
      if (commandComponents[1].compareTo("cal") != 0) {
        throw new IOException("Invalid use of export\n" + menu);
      }
      else {
        if (fileName.endsWith(".csv")) {
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

  private String exportToCSV(String fileName) throws IOException {
    List<IEvent> events = calendarModel
        .getActiveCalendar()
        .queryDateRange(LocalDateTime.MIN, LocalDateTime.MAX);
    File exportFile = new File(fileName);
    FileWriter fileWriter = new FileWriter(exportFile);
    String header = "Subject,Start date,Start time,End Date,"
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
