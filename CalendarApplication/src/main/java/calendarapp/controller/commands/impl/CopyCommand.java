package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.utils.TimeUtil;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.COPY_EVENTS_BETWEEN_COMMAND;
import static calendarapp.controller.commands.impl.RegexPatternConstants.COPY_EVENTS_ON_COMMAND;
import static calendarapp.controller.commands.impl.RegexPatternConstants.COPY_EVENT_COMMAND;

public class CopyCommand extends AbstractCommand {

  CopyCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) throws InvalidCommandException, EventConflictException {
    try {
      Matcher matcher = regexMatching(COPY_EVENT_COMMAND, command);
      if (matcher.find()) {
        copySingleEvent(matcher);
      }

      matcher = regexMatching(COPY_EVENTS_ON_COMMAND, command);
      if (matcher.find()) {
        copyEventsOn(matcher);
      }

      matcher = regexMatching(COPY_EVENTS_BETWEEN_COMMAND, command);
      if (matcher.find()) {
        copyEventsBetween(matcher);
      }
    } catch (IllegalArgumentException e) {
      throw new InvalidCommandException(command + "\nReason : " + e.getMessage());
    } catch (EventConflictException e) {
      throw new EventConflictException(command + "\nReason : " + e.getMessage());
    }
  }

  private void copySingleEvent(Matcher matcher) {
    String eventName = null;
    String eventStartDate = null;
    String copyCalendarName = null;
    String copyStartDate = null;

    eventName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    eventStartDate = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    copyCalendarName = matcher.group(5) != null ? matcher.group(5) : matcher.group(6);
    copyStartDate = matcher.group(7) != null ? matcher.group(7) : matcher.group(8);

    if (eventName != null || eventStartDate != null || copyCalendarName != null
        || copyStartDate != null) {
      throw new InvalidCommandException("Required fields are missing.\n");
    }

    model.copyEvent(eventName, TimeUtil.getTemporalFromString(eventStartDate),
        copyCalendarName, TimeUtil.getTemporalFromString(copyStartDate));
  }

  private void copyEventsOn(Matcher matcher) {
    String fromStartDate = null;
    String copyCalendarName = null;
    String copyStartDate = null;

    fromStartDate = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    copyCalendarName = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    copyStartDate = matcher.group(5) != null ? matcher.group(5) : matcher.group(6);

    if (fromStartDate != null || copyCalendarName != null || copyStartDate != null) {
      throw new InvalidCommandException("Required fields are missing.\n");
    }

    model.copyEvents(TimeUtil.getTemporalFromString(fromStartDate),
        TimeUtil.getEndOfDayFromString(fromStartDate), copyCalendarName,
        TimeUtil.getTemporalFromString(copyStartDate));
  }

  private void copyEventsBetween(Matcher matcher) {
    String fromStartDate = null;
    String toEndDate = null;
    String copyCalendarName = null;
    String copyStartDate = null;

    fromStartDate = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    toEndDate = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    copyCalendarName = matcher.group(4) != null ? matcher.group(5) : matcher.group(6);
    copyStartDate = matcher.group(7) != null ? matcher.group(7) : matcher.group(8);

    if (fromStartDate != null || copyCalendarName != null || copyStartDate != null
        || toEndDate != null) {
      throw new InvalidCommandException("Required fields are missing.\n");
    }

    model.copyEvents(TimeUtil.getTemporalFromString(fromStartDate),
        TimeUtil.getEndOfDayFromString(toEndDate), copyCalendarName,
        TimeUtil.getTemporalFromString(copyStartDate));
  }
}
