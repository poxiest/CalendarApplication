package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.utils.TimeUtil;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.COPY_EVENTS_BETWEEN_COMMAND;
import static calendarapp.controller.commands.impl.RegexPatternConstants.COPY_EVENTS_ON_COMMAND;
import static calendarapp.controller.commands.impl.RegexPatternConstants.COPY_EVENT_COMMAND;

/**
 * Command to copy events from the current calendar to another calendar.
 * It supports copying a specific event, all events on a day, or events between dates.
 */
public class CopyCommand extends AbstractCommand {
  private String eventName;
  private String eventStartDate;
  private String fromStartDate;
  private String toEndDate;
  private String copyCalendarName;
  private String copyStartDate;

  CopyCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Executes the copy command based on the input string.
   * Parses the command, builds the request, and sends it to the model for processing.
   *
   * @param command the full user input command
   * @throws InvalidCommandException if the command format is incorrect or required fields are missing
   * @throws EventConflictException if there is a conflict when copying events
   */
  @Override
  public void execute(String command) throws InvalidCommandException, EventConflictException {
    CopyEventRequestDTO.Builder builder = CopyEventRequestDTO.builder();

    try {
      Matcher matcher = regexMatching(COPY_EVENT_COMMAND, command);
      if (matcher.find()) {
        eventName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        eventStartDate = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
        copyCalendarName = matcher.group(5) != null ? matcher.group(5) : matcher.group(6);
        copyStartDate = matcher.group(7) != null ? matcher.group(7) : matcher.group(8);

        builder = builder.eventName(eventName)
            .startTime(TimeUtil.getTemporalFromString(eventStartDate))
            .copyCalendarName(copyCalendarName)
            .copyStartDate(TimeUtil.getTemporalFromString(copyStartDate));
      }

      matcher = regexMatching(COPY_EVENTS_ON_COMMAND, command);
      if (matcher.find()) {
        fromStartDate = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        copyCalendarName = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
        copyStartDate = matcher.group(5) != null ? matcher.group(5) : matcher.group(6);

        builder = builder.startTime(TimeUtil.getStartOfDayFromString(fromStartDate))
            .copyCalendarName(copyCalendarName)
            .copyStartDate(TimeUtil.getDateFromString(copyStartDate));
      }

      matcher = regexMatching(COPY_EVENTS_BETWEEN_COMMAND, command);
      if (matcher.find()) {
        fromStartDate = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        toEndDate = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
        copyCalendarName = matcher.group(5) != null ? matcher.group(5) : matcher.group(6);
        copyStartDate = matcher.group(7) != null ? matcher.group(7) : matcher.group(8);

        builder = builder.startTime(TimeUtil.getStartOfDayFromString(fromStartDate))
            .endTime(TimeUtil.getEndOfDayFromString(toEndDate))
            .copyCalendarName(copyCalendarName)
            .copyStartDate(TimeUtil.getDateFromString(copyStartDate));
      }

      if (copyCalendarName == null || copyStartDate == null) {
        throw new InvalidCommandException("Required fields are missing.\n");
      }

      model.copyEvent(builder.build());
    } catch (IllegalArgumentException | InvalidCommandException e) {
      throw new InvalidCommandException(command + "\nReason : " + e.getMessage());
    } catch (EventConflictException e) {
      throw new EventConflictException(command + "\nReason : " + e.getMessage());
    }
  }
}
