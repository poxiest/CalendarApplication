package calendarapp.controller.commands.impl;

import java.util.List;
import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.IEvent;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.PRINT_FROM_TO_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.PRINT_ON_PATTERN;
import static calendarapp.utils.TimeUtil.getLocalDateTimeFromString;

public class PrintCommand extends AbstractCommand {

  private String startDateTime;
  private String endDateTime;
  private String on;

  PrintCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    Matcher matcher = regexMatching(PRINT_FROM_TO_PATTERN, command);
    if (matcher.find()) {
      startDateTime = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      endDateTime = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    }

    matcher = regexMatching(PRINT_ON_PATTERN, command);
    if (matcher.find()) {
      on = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    if ((startDateTime == null && endDateTime == null) && on != null) {
      startDateTime = on;
    }

    if (startDateTime == null) {
      throw new InvalidCommandException(command + "\nReason : Required fields are missing.\n");
    }

    try {
      List<IEvent> eventsToShow = model.printEvents(getLocalDateTimeFromString(startDateTime),
          getLocalDateTimeFromString(endDateTime));
      if (!eventsToShow.isEmpty()) {
        view.displayMessage("Events:\n");
        for (IEvent event : eventsToShow) {
          view.displayMessage("• " + event.formatForDisplay() + "\n");
        }
      } else {
        view.displayMessage("No events found.\n");
      }
    } catch (
        IllegalArgumentException e) {
      throw new InvalidCommandException(command + "\nReason : " + e.getMessage());
    }
  }
}
