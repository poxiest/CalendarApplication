package calendarapp.controller.commands.impl;

import java.util.List;
import java.util.regex.Matcher;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.model.event.IEvent;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.printFromToPattern;
import static calendarapp.controller.commands.impl.RegexPatternConstants.printOnPattern;
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
    Matcher matcher = regexMatching(printFromToPattern, command);
    if (matcher.find()) {
      startDateTime = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
      endDateTime = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    }

    matcher = regexMatching(printOnPattern, command);
    if (matcher.find()) {
      on = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    if ((startDateTime == null && endDateTime == null) && on != null) {
      startDateTime = on;
    }

    if (startDateTime == null) {
      view.display("Required fields are missing. Cannot process the command.\n");
      return;
    }

    try {
      List<IEvent> eventsToShow = model.printEvents(getLocalDateTimeFromString(startDateTime),
          getLocalDateTimeFromString(endDateTime));
      for (IEvent event : eventsToShow) {
        view.display(event.toString() + "\n");
      }
    } catch (IllegalArgumentException e) {
      view.display("Error viewing: " + e.getMessage() + ".\n");
    }
  }
}