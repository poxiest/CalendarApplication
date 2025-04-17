package commands;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import model.AnalyticsVisitor;
import model.IMultipleCalendarModel;

/**
 * Class that handles all show events heard by the controller
 * and applies the commandComponents to the given multiple calendar
 * model.
 */
public class ShowCalendar implements IControllerCommand {

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
  public ShowCalendar(
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
    String menu = "menu: show calendar dashboard from <dateString> to <dateString>\n";
    if (commandComponents.length == 7) {
      if(commandComponents[1].compareTo("calendar") != 0
      || commandComponents[2].compareTo("dashboard") != 0
      || commandComponents[3].compareTo("from") != 0
      || commandComponents[5].compareTo("to") != 0) {
        throw new IOException("Invalid use of show calendar\n" + menu);
      } else {
        LocalDate startTime = LocalDate.parse(commandComponents[4]);
        LocalDate endTime = LocalDate.parse(commandComponents[6]);
        AnalyticsVisitor analyticsVisitor = new AnalyticsVisitor(startTime, endTime);
        this.calendarModel.getActiveCalendar().accept(analyticsVisitor);
        this.out.append("Total Count: ").append(Integer.toString(analyticsVisitor.getTotalCount())).append("\n");

        for (Map.Entry<DayOfWeek, Integer> entry : analyticsVisitor.getDaysCount().entrySet()) {
          this.out.append("Day: ")
              .append(entry.getKey().toString())
              .append(", Count: ")
              .append(Integer.toString(entry.getValue()))
              .append("\n");
        }

        for (Map.Entry<String, Long> entry : analyticsVisitor.getSubjectCountMap().entrySet()) {
          this.out.append("Subject: ")
              .append(entry.getKey())
              .append(", Count: ")
              .append(Long.toString(entry.getValue()))
              .append("\n");
        }

        this.out.append("Average Events per Day: ")
            .append(Double.toString(analyticsVisitor.getAverageEventsPerDay()))
            .append("\n");

        this.out.append("Online %: ")
            .append(String.format("%.2f", analyticsVisitor.getOnlinePercentage()))
            .append("\n");

        this.out.append("Offline %: ")
            .append(String.format("%.2f", analyticsVisitor.getOfflinePercentage()))
            .append("\n");

        this.out.append("Most Busy Date(s) by Event Count: ")
            .append(analyticsVisitor.getMostBusyByEvents().toString())
            .append("\n");

        this.out.append("Least Busy Date(s) by Event Count: ")
            .append(analyticsVisitor.getLeastBusyByEvents().toString())
            .append("\n");

        this.out.append("Most Busy Date(s) by Total Hours: ")
            .append(analyticsVisitor.getMostBusyByHours().toString())
            .append("\n");

        this.out.append("Least Busy Date(s) by Total Hours: ")
            .append(analyticsVisitor.getLeastBusyByHours().toString())
            .append("\n");
      }
    } else {
      throw new IOException("Invalid number of arguments for show\n" + menu);
    }
  }
}