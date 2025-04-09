package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.EditEventRequestDTO;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.EDIT_EVENT_NAME_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.EDIT_FROM_PATTERN;
import static calendarapp.controller.commands.impl.RegexPatternConstants.EDIT_FROM_TO_PATTERN;

/**
 * Edit Command implementation for editing existing calendar events.
 * Allows modification of various event properties by parsing the
 * command string and applying changes to the specified event.
 */
public class EditEventCommand extends AbstractCommand {

  /**
   * The name of the property to edit.
   */
  private String propertyName;

  /**
   * The new value for the property.
   */
  private String newPropertyValue;

  /**
   * The name of the event to edit.
   */
  private String eventName;

  /**
   * The start date and time of the event to identify it.
   */
  private String startDateTime;

  /**
   * The end date and time of the event to identify it.
   */
  private String endDateTime;

  /**
   *
   */
  private boolean isMultiple;

  /**
   * Creates a new EditCommand with the specified model and view.
   *
   * @param model The calendar model to use for editing events.
   * @param view  The view to use for displaying information.
   */
  EditEventCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Executes the edit command by parsing the command string
   * and updating the event in the model.
   *
   * @param command The command string containing event editing instructions.
   * @throws InvalidCommandException If the syntax is invalid or required fields are missing.
   * @throws EventConflictException  If the edit event conflicts with existing events.
   */
  @Override
  public void execute(String command) throws InvalidCommandException, EventConflictException {
    parseCommand(command);
    try {
      model.editEvent(EditEventRequestDTO.builder().eventName(eventName)
          .startTime(startDateTime)
          .endTime(endDateTime)
          .propertyName(propertyName)
          .propertyValue(newPropertyValue)
          .isRecurring(isMultiple).build());
    } catch (IllegalArgumentException e) {
      throw new InvalidCommandException(command + "\nReason : " + e.getMessage());
    } catch (EventConflictException e) {
      throw new EventConflictException(command + "\nReason : " + e.getMessage());
    }
  }

  /**
   * Parses the input command to get the properties.
   *
   * @param command String input of edit command.
   */
  private void parseCommand(String command) {
    Matcher matcher = regexMatching(EDIT_FROM_TO_PATTERN, command);
    if (matcher.find()) {
      propertyName = matcher.group(1);
      eventName = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      startDateTime = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
      endDateTime = matcher.group(6) != null ? matcher.group(6) : matcher.group(7);
      newPropertyValue = matcher.group(8) != null ? matcher.group(8) : matcher.group(9);
      isMultiple = false;
    }

    matcher = regexMatching(EDIT_FROM_PATTERN, command);
    if (matcher.find()) {
      propertyName = matcher.group(1);
      eventName = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      startDateTime = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
      newPropertyValue = matcher.group(6) != null ? matcher.group(6) : matcher.group(7);
      isMultiple = true;
    }

    matcher = regexMatching(EDIT_EVENT_NAME_PATTERN, command);
    if (matcher.find()) {
      propertyName = matcher.group(1);
      eventName = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
      newPropertyValue = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
      isMultiple = true;
    }

    if (eventName == null || newPropertyValue == null || propertyName == null) {
      throw new InvalidCommandException(command + "\nReason : Required fields are missing.\n");
    }
  }
}