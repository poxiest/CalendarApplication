package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.EDIT_EVENT_NAME_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.EDIT_FROM_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.EDIT_FROM_TO_PATTERN;
import static calendarapp.utils.TimeUtil.getLocalDateTimeFromString;

public class EditCommand extends AbstractCommand {

  private String propertyName;
  private String newPropertyValue;
  private String eventName;
  private String startDateTime;
  private String endDateTime;

  EditCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    Matcher matcher = regexMatching(EDIT_FROM_TO_PATTERN, command);
    if (matcher.find()) {
      propertyName = matcher.group(1);
      eventName = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      startDateTime = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
      endDateTime = matcher.group(6) != null ? matcher.group(6) : matcher.group(7);
      newPropertyValue = matcher.group(8) != null ? matcher.group(8) : matcher.group(9);
    }

    matcher = regexMatching(EDIT_FROM_PATTERN, command);
    if (matcher.find()) {
      propertyName = matcher.group(1);
      eventName = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      startDateTime = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
      newPropertyValue = matcher.group(6) != null ? matcher.group(6) : matcher.group(7);
    }

    matcher = regexMatching(EDIT_EVENT_NAME_PATTERN, command);
    if (matcher.find()) {
      propertyName = matcher.group(1);
      eventName = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      newPropertyValue = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
    }

    if (eventName == null || newPropertyValue == null || propertyName == null) {
      view.displayMessage("Required fields are missing. Cannot process the command.\n\n");
      return;
    }

//    view.display( propertyName + "\n" + eventName + "\n" + newPropertyValue + "\n" + fromDateTimeString + "\n" + toDateTimeString + "\n" );

    try {
      model.editEvent(eventName, getLocalDateTimeFromString(startDateTime),
          getLocalDateTimeFromString(endDateTime), propertyName, newPropertyValue);
    } catch (IllegalArgumentException e) {
      view.displayMessage("Error editing event: " + e.getMessage() + ".\n\n");
    }
  }
}
