package commands;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import model.AnalyticsVisitor;
import model.IMultipleCalendarModel;

/**
 * Class to parse show calendar dashboard command and
 * display the analytics of the given dates.
 */
public class ShowCalendar implements IControllerCommand {

  String[] commandComponents;
  IMultipleCalendarModel calendarModel;
  Appendable out;

  /**
   * A constructor for an event command handler which
   * takes in an array of command components, a calendar model,
   * and the current programs output buffer.
   *
   * @param commandComponents the command components as strings.
   * @param calendarModel     an instance of the calendar model.
   * @param out               the programs output buffer.
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
      if (commandComponents[1].compareTo("calendar") != 0
          || commandComponents[2].compareTo("dashboard") != 0
          || commandComponents[3].compareTo("from") != 0
          || commandComponents[5].compareTo("to") != 0) {
        throw new IOException("Invalid use of show calendar\n" + menu);
      } else {
        LocalDate startTime = LocalDate.parse(commandComponents[4]);
        LocalDate endTime = LocalDate.parse(commandComponents[6]);
        AnalyticsVisitor analyticsVisitor = new AnalyticsVisitor(startTime, endTime);
        this.calendarModel.getActiveCalendar().accept(analyticsVisitor);
        this.out.append("\nCalendar Analytics Summary\n");

        this.out.append("Total Events: ")
            .append(String.valueOf(analyticsVisitor.getTotalCount()))
            .append("\n");

        this.out.append("Events by Day of Week:\n");
        for (DayOfWeek day : DayOfWeek.values()) {
          this.out.append("\t")
              .append(day.toString())
              .append(": ")
              .append(String.valueOf(analyticsVisitor.getDaysCount().get(day)))
              .append("\n");
        }

        this.out.append("Events by Subject:\n");
        for (Map.Entry<String, Long> entry : analyticsVisitor.getSubjectCountMap().entrySet()) {
          this.out.append("\t")
              .append(entry.getKey())
              .append(": ")
              .append(String.valueOf(entry.getValue()))
              .append("\n");
        }

        this.out.append("Average Events per Day: ")
            .append(String.format("%.2f", analyticsVisitor.getAverageEventsPerDay()))
            .append("\n");

        this.out.append("Online Events: ")
            .append(String.format("%.2f", analyticsVisitor.getOnlinePercentage()))
            .append("%\n");

        this.out.append("Offline Events: ")
            .append(String.format("%.2f", analyticsVisitor.getOfflinePercentage()))
            .append("%\n");

        this.out.append("Most Busy Date(s) by Event Count: ")
            .append(analyticsVisitor.getMostBusyByEvents().stream().map(LocalDate::toString)
                .collect(Collectors.joining(", ")))
            .append("\n");

        this.out.append("Least Busy Date(s) by Event Count: ")
            .append(analyticsVisitor.getLeastBusyByEvents().stream().map(LocalDate::toString)
                .collect(Collectors.joining(", ")))
            .append("\n");

        this.out.append("Most Busy Date(s) by Total Hours: ")
            .append(analyticsVisitor.getMostBusyByDuration().stream().map(LocalDate::toString)
                .collect(Collectors.joining(", ")))
            .append("\n");

        this.out.append("Least Busy Date(s) by Total Hours: ")
            .append(analyticsVisitor.getLeastBusyByDuration().stream().map(LocalDate::toString)
                .collect(Collectors.joining(", ")))
            .append("\n");
      }
    } else {
      throw new IOException("Invalid number of arguments for show\n" + menu);
    }
  }
}