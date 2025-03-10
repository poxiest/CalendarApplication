package calendarapp.controller.commands.impl;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.model.event.IEvent;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.PRINT_FROM_TO_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.PRINT_ON_PATTERN;
import static calendarapp.utils.TimeUtil.getLocalDateTimeFromString;

public class PrintCommand extends AbstractCommand {

  private String startDateTime;
  private String endDateTime;
  private String on;

  PrintCommand(ICalendarApplication model, ICalendarView view) {
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
      view.displayMessage("Required fields are missing. Cannot process the command.\n");
      return;
    }

    try {
      List<IEvent> eventsToShow = model.printEvents(getLocalDateTimeFromString(startDateTime),
          getLocalDateTimeFromString(endDateTime)).stream()
          .sorted((o1, o2) ->
              (int) ChronoUnit.SECONDS.between(o2.getStartDateTime(), o1.getStartDateTime()))
          .collect(Collectors.toList());
      view.displayEvents(eventsToShow);
    } catch (
        IllegalArgumentException e) {
      view.displayMessage("Error viewing: " + e.getMessage() + ".\n\n");
    }
  }
}
