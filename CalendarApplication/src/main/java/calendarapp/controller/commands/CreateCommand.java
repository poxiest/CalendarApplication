package calendarapp.controller.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public class CreateCommand extends AbstractCommand {

  private String eventName;
  private String from;
  private String to;
  private String on;
  private String repeats;
  private String times;
  private String until;
  private String description;
  private String location;
  private String scope;
  private boolean autoDecline;

  CreateCommand(ICalendarApplication model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    view.display("Inside CreateCommand\n");

    String autoDeclinePattern = "--autoDecline";
    String eventNamePattern = "(?i)\\s+event\\s+(?:\"([^\"]+)\"|(\\S+))\\s+(on|from)";
    String dateFromToPattern = "(?i)\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+"
        + "to\\s+(?:\"([^\"]+)\"|(\\S+))";
    String dateOnPattern = "(?i)\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))";
    String repeatsForPattern = "(?i)\\s+repeats\\s+(?:\"([^\"]+)\"|(\\S+))\\s+for\\s+(\\d+)\\s+times";
    String repeatsUntilPattern = "(?i)\\s+repeats\\s+(?:\"([^\"]+)\"|(\\S+))\\s+"
        + "until\\s+(?:\"([^\"]+)\"|(\\S+))/gm";
    String optionalParameters = "(?i)(description|location|scope)\\s+(?:\"([^\"]+)\"|(\\S+))";

    autoDecline = command.toLowerCase().contains(autoDeclinePattern.toLowerCase());

    if (autoDecline) {
      command = command.replaceAll("(?i)--autoDecline", "");
    }

    Pattern pattern = Pattern.compile(eventNamePattern);
    Matcher matcher = pattern.matcher(command);
    if (matcher.find()) {
      eventName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    pattern = Pattern.compile(dateFromToPattern);
    matcher = pattern.matcher(command);
    if (matcher.find()) {
      from  = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      to = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    }

    pattern = Pattern.compile(dateOnPattern);
    matcher = pattern.matcher(command);
    if (matcher.find()) {
      on = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    pattern = Pattern.compile(repeatsForPattern);
    matcher = pattern.matcher(command);
    if (matcher.find()) {
      repeats = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      times = matcher.group(3);
    }

    pattern = Pattern.compile(repeatsUntilPattern);
    matcher = pattern.matcher(command);
    if (matcher.find()) {
      repeats = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      until = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    }

    pattern = Pattern.compile(optionalParameters);
    matcher = pattern.matcher(command);
    while (matcher.find()) {
      if (matcher.group(1).equalsIgnoreCase("description")) {
        description = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      } else if (matcher.group(1).equalsIgnoreCase("location")) {
        location = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      } else if (matcher.group(1).equalsIgnoreCase("scope")) {
        scope = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      }
    }

    view.display(eventName + "\n" + from + "\n" + to + "\n" + on + "\n" + repeats
        + "\n" + times + "\n" + until + "\n" + description + "\n" + location + "\n" + scope + "\n" + autoDecline);
  }
}
