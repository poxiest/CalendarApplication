package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.Constants.CREATE_AUTO_DECLINE_PATTERN;
import static calendarapp.controller.Constants.CREATE_EVENT_NAME_PATTERN;
import static calendarapp.controller.Constants.CREATE_FROM_TO_PATTERN;
import static calendarapp.controller.Constants.CREATE_ON_PATTERN;
import static calendarapp.controller.Constants.CREATE_OPTIONAL_PARAMETERS;
import static calendarapp.controller.Constants.CREATE_REPEATS_F0R_PATTERN;
import static calendarapp.controller.Constants.CREATE_REPEATS_UNTIL_PATTERN;
import static calendarapp.controller.Constants.IS_RECURRING_EVENT;

/**
 * Create Command implementation for creating calendar events.
 * Parses the input command and creates events with various properties including
 * recurring events with different recurrence patterns.
 */
public class CreateEventCommand extends AbstractCommand {

  /**
   * The name of the event to be created.
   */
  private String eventName;

  /**
   * The start date and time of the event.
   */
  private String startDateTime;

  /**
   * The end date and time of the event.
   */
  private String endDateTime;

  /**
   * The date for single-day events.
   */
  private String on;

  /**
   * The days on which a recurring event repeats.
   */
  private String recurringDays;

  /**
   * The number of occurrences for a recurring event.
   */
  private String occurrenceCount;

  /**
   * The end date for a recurring event.
   */
  private String recurrenceEndDate;

  /**
   * The description of the event.
   */
  private String description;

  /**
   * The location where the event takes place.
   */
  private String location;

  /**
   * The visibility setting for the event.
   */
  private String visibility;

  /**
   * Whether conflicting events should be automatically declined.
   */
  private boolean autoDecline;

  /**
   * Creates a new CreateCommand with the specified model and view.
   *
   * @param model The calendar model to use for creating events.
   * @param view  The view to use for displaying information.
   */
  CreateEventCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Executes the create command by parsing the command string,
   * extracting event details, and creating the event in the model.
   *
   * @param command The command string containing event creation instructions.
   * @throws InvalidCommandException If the syntax is invalid or required fields are missing.
   * @throws EventConflictException  If the new event conflicts with existing events.
   */
  @Override
  public void execute(String command) throws InvalidCommandException, EventConflictException {
    parseCommand(command);
    try {
      model.createEvent(eventName, startDateTime, endDateTime, recurringDays, occurrenceCount,
          recurrenceEndDate, description, location, visibility, autoDecline);
    } catch (IllegalArgumentException e) {
      throw new InvalidCommandException(command + "\nReason : " + e.getMessage());
    } catch (EventConflictException e) {
      throw new EventConflictException(command + "\nReason : " + e.getMessage());
    }
  }

  /**
   * Parses the input command to get the properties.
   *
   * @param command String input of create command.
   */
  private void parseCommand(String command) {
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

    boolean isRecurring = command.toLowerCase().contains(IS_RECURRING_EVENT.toLowerCase());
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

    if (eventName == null || startDateTime == null
        || (isRecurring && recurringDays == null)) {
      throw new InvalidCommandException(command + "\nReason : Required fields are missing.\n");
    }
  }
}