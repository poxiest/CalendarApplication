package calendarapp.controller.commands;

import java.util.regex.Matcher;

import calendarapp.model.ICalendarApplication;
import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class EditCommand extends AbstractCommand {

  private String propertyName;
  private String newPropertyValue;
  private String eventName;
  private String fromDateTimeString;
  private String toDateTimeString;

  EditCommand(ICalendarApplication model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    String fromToPattern = "(?i)\\s+event\\s+(\\S+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))\\s+with\\s+(?:\"([^\"]+)\"|(\\S+))$";
    String fromPattern = "(?i)\\s+events\\s+(\\S+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+with\\s+(?:\"([^\"]+)\"|(\\S+))$";
    String eventNamePattern = "(?i)\\s+events\\s+(\\S+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+(?:\"([^\"]+)\"|(\\S+))$";

    Matcher matcher = regexMatching(fromToPattern, command);
    if (matcher.find()) {
      propertyName = matcher.group(1);
      eventName = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      fromDateTimeString = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
      toDateTimeString = matcher.group(6) != null ? matcher.group(6) : matcher.group(7);
      newPropertyValue = matcher.group(8) != null ? matcher.group(8) : matcher.group(9);
    }

    matcher = regexMatching(fromPattern, command);
    if (matcher.find()) {
      propertyName = matcher.group(1);
      eventName = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      fromDateTimeString = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
      newPropertyValue = matcher.group(6) != null ? matcher.group(6) : matcher.group(7);
    }

    matcher = regexMatching(eventNamePattern, command);
    if (matcher.find()) {
      propertyName = matcher.group(1);
      eventName = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      newPropertyValue = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
    }

    if (eventName == null || newPropertyValue == null || propertyName == null) {
      view.display("Required fields are missing. Cannot process the command.\n\n");
    }

//    view.display( propertyName + "\n" + eventName + "\n" + newPropertyValue + "\n" + fromDateTimeString + "\n" + toDateTimeString + "\n" );

    model.editEvent();
  }
}
