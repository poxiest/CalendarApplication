package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.utils.TimeUtil;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.autoDeclinePattern;
import static calendarapp.controller.commands.impl.RegexPatternConstants.dateFromToPattern;
import static calendarapp.controller.commands.impl.RegexPatternConstants.dateOnPattern;
import static calendarapp.controller.commands.impl.RegexPatternConstants.eventNamePattern;
import static calendarapp.controller.commands.impl.RegexPatternConstants.optionalParameters;
import static calendarapp.controller.commands.impl.RegexPatternConstants.repeatsForPattern;
import static calendarapp.controller.commands.impl.RegexPatternConstants.repeatsUntilPattern;
import static calendarapp.utils.TimeUtil.getLocalDateTimeFromString;

public class CreateCommand extends AbstractCommand {

  private String eventName;
  private String startDateTime;
  private String endDateTime;
  private String on;
  private String recurringDays;
  private String occurrenceCount;
  private String recurrenceEndDate;
  private String description;
  private String location;
  private String visibility;
  private boolean autoDecline;

  CreateCommand(ICalendarApplication model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    autoDecline = command.toLowerCase().contains(autoDeclinePattern.toLowerCase());
    if (autoDecline) {
      command = command.replaceAll("(?i)--autoDecline", "");
    }

    Matcher matcher = regexMatching(eventNamePattern, command);
    if (matcher.find()) {
      eventName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    matcher = regexMatching(dateFromToPattern, command);
    if (matcher.find()) {
      startDateTime = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      endDateTime = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    }

    matcher = regexMatching(dateOnPattern, command);
    if (matcher.find()) {
      on = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    matcher = regexMatching(repeatsForPattern, command);
    if (matcher.find()) {
      recurringDays = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      occurrenceCount = matcher.group(3);
    }

    matcher = regexMatching(repeatsUntilPattern, command);
    if (matcher.find()) {
      recurringDays = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      recurrenceEndDate = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    }

    matcher = regexMatching(optionalParameters, command);
    while (matcher.find()) {
      if (matcher.group(1).equalsIgnoreCase("description")) {
        description = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      } else if (matcher.group(1).equalsIgnoreCase("location")) {
        location = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      } else if (matcher.group(1).equalsIgnoreCase("scope")) {
        visibility = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      }
    }

    if ((startDateTime == null && endDateTime == null) && on != null) {
      startDateTime = on;
    }

    if (eventName == null || startDateTime == null) {
      view.display("Required fields are missing. Cannot process the command.\n");
      return;
    }

    if ((recurringDays != null && occurrenceCount == null && recurrenceEndDate == null) ||
        (recurringDays == null && (occurrenceCount != null || recurrenceEndDate != null)) ||
        (occurrenceCount != null && recurrenceEndDate != null)) {
      view.display("Recurrence specification is incorrect.\n");
      return;
    }

    try {
      model.createEvent(eventName, getLocalDateTimeFromString(startDateTime),
          getLocalDateTimeFromString(endDateTime), recurringDays, occurrenceCount,
          TimeUtil.getLocalDateTimeFromString(recurrenceEndDate),
          description, location, visibility, autoDecline);
    } catch (IllegalArgumentException e) {
      view.display("Error creating event: " + e.getMessage() + ".\n");
    }
  }
}
