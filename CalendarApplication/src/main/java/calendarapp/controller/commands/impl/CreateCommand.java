package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.utils.TimeUtil;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.CREATE_AUTO_DECLINE_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.CREATE_EVENT_NAME_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.CREATE_FROM_TO_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.CREATE_ON_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.CREATE_OPTIONAL_PARAMETERS;
import static calendarapp.controller.commands.impl.RegexPatternConstants.CREATE_REPEATS_F0R_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.CREATE_REPEATS_UNTIL_PATTERN;
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
    autoDecline = command.toLowerCase().contains(CREATE_AUTO_DECLINE_PATTERN.toLowerCase());
    if (autoDecline) {
      command = command.replaceAll("(?i)--autoDecline", "");
    }

    Matcher matcher = regexMatching(CREATE_EVENT_NAME_PATTERN, command);
    if (matcher.find()) {
      eventName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    matcher = regexMatching(CREATE_FROM_TO_PATTERN, command);
    if (matcher.find()) {
      startDateTime = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      endDateTime = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    }

    matcher = regexMatching(CREATE_ON_PATTERN, command);
    if (matcher.find()) {
      on = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    matcher = regexMatching(CREATE_REPEATS_F0R_PATTERN, command);
    if (matcher.find()) {
      recurringDays = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      occurrenceCount = matcher.group(3);
    }

    matcher = regexMatching(CREATE_REPEATS_UNTIL_PATTERN, command);
    if (matcher.find()) {
      recurringDays = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      recurrenceEndDate = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    }

    matcher = regexMatching(CREATE_OPTIONAL_PARAMETERS, command);
    while (matcher.find()) {
      if (matcher.group(1).equalsIgnoreCase("description")) {
        description = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      } else if (matcher.group(1).equalsIgnoreCase("location")) {
        location = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      } else if (matcher.group(1).equalsIgnoreCase("visibility")) {
        visibility = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      }
    }

    if ((startDateTime == null && endDateTime == null) && on != null) {
      startDateTime = on;
    }

    if (eventName == null || startDateTime == null) {
      view.displayMessage("Required fields are missing. Cannot process the command.\n");
      return;
    }

    if ((recurringDays != null && occurrenceCount == null && recurrenceEndDate == null) ||
        (recurringDays == null && (occurrenceCount != null || recurrenceEndDate != null)) ||
        (occurrenceCount != null && recurrenceEndDate != null)) {
      view.displayMessage("Recurrence specification is incorrect.\n\n");
      return;
    }

    try {
      model.createEvent(eventName, getLocalDateTimeFromString(startDateTime),
          getLocalDateTimeFromString(endDateTime), recurringDays, occurrenceCount,
          TimeUtil.getLocalDateTimeFromString(recurrenceEndDate),
          description, location, visibility, autoDecline);
    } catch (IllegalArgumentException e) {
      view.displayMessage("Error creating event: " + e.getMessage() + ".\n\n");
    }
  }
}
