package calendarapp.controller.commands.impl;

import java.util.List;
import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.PrintEventsResponseDTO;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.PRINT_FROM_TO_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.PRINT_ON_PATTERN;
import static calendarapp.utils.TimeUtil.getTemporalFromString;

/**
 * Print Command implementation for printing calendar events within a specified time range.
 * Parses the print command and displays the events through the view.
 */
public class PrintCommand extends AbstractCommand {

  /**
   * The start date and time to print events from.
   */
  private String startDateTime;

  /**
   * The end date and time to print events until.
   */
  private String endDateTime;

  /**
   * The specific date to print events for.
   */
  private String on;

  /**
   * Creates a new PrintCommand with the specified model and view.
   *
   * @param model The calendar model to retrieve events from.
   * @param view  The view to use for displaying events.
   */
  PrintCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Executes the print command by parsing the command string
   * and displaying them through the view.
   *
   * @param command The command string containing print instructions.
   * @throws InvalidCommandException If the syntax is invalid or required fields are missing.
   */
  @Override
  public void execute(String command) throws InvalidCommandException {
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

    List<PrintEventsResponseDTO> eventsToShow =
        model.getEventsForPrinting(getTemporalFromString(startDateTime),
            getTemporalFromString(endDateTime));
    if (!eventsToShow.isEmpty()) {
      view.displayMessage("Events:\n");
      for (PrintEventsResponseDTO event : eventsToShow) {
        view.displayMessage(String.format("• %s - %s to %s %s\n",
            event.getEventName(),
            event.getStartTime(),
            event.getEndTime(),
            event.getLocation() != null && !event.getLocation().isEmpty()
                ? "- Location: " + event.getLocation()
                : ""));
      }
    } else {
      view.displayMessage("No events found.\n");
    }
  }
}