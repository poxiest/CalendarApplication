package calendarapp.model.impl;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.IEvent;
import calendarapp.utils.TimeUtil;

import static calendarapp.model.impl.CalendarExporter.exportEventAsGoogleCalendarCsv;

/**
 * The CalendarModel class implements the ICalendarModel interface
 * and is responsible for managing calendar events, including creating, editing,
 * and displaying events, handling conflicts, and exporting events in a specific format.
 */
public class CalendarModel implements ICalendarModel {

  private final List<IEvent> events;
  private final Map<Character, DayOfWeek> dayMap;

  /**
   * Constructs a CalendarModel object, initializing the event list and day mapping.
   */
  public CalendarModel() {
    this.events = new ArrayList<>();
    this.dayMap = initializeDayMap();
  }

  /**
   * Initializes a map to convert characters representing days to DayOfWeek.
   *
   * @return A map mapping day characters to DayOfWeek values.
   */
  private Map<Character, DayOfWeek> initializeDayMap() {
    return Map.of(
        'M', DayOfWeek.MONDAY,
        'T', DayOfWeek.TUESDAY,
        'W', DayOfWeek.WEDNESDAY,
        'R', DayOfWeek.THURSDAY,
        'F', DayOfWeek.FRIDAY,
        'S', DayOfWeek.SATURDAY,
        'U', DayOfWeek.SUNDAY
    );
  }

  /**
   * Creates a new event, either recurring or one-time.
   *
   * @param eventName         The name of the event.
   * @param startTime         The start time of the event.
   * @param endTime           The end time of the event.
   * @param recurringDays     A string of characters representing recurring days.
   * @param occurrenceCount   The number of occurrences of the event (for recurring events).
   * @param recurrenceEndDate The end date of the recurrence (for recurring events).
   * @param description       The description of the event.
   * @param location          The location of the event.
   * @param visibility        The visibility of the event.
   * @param autoDecline       Flag indicating whether the event should auto-decline
   *                          conflicting events.
   * @throws EventConflictException if the event conflicts with an existing event.
   */
  @Override
  public void createEvent(String eventName, Temporal startTime, Temporal endTime,
                          String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
                          String description, String location, String visibility,
                          boolean autoDecline) throws EventConflictException {

    boolean isRecurring = recurringDays != null;
    Integer occurrence = occurrenceCount != null ? Integer.parseInt(occurrenceCount) : null;

    if (isRecurring) {
      createRecurringEvents(eventName, startTime, endTime, description, location, visibility,
          recurringDays, occurrence, recurrenceEndDate, autoDecline);
    } else {
      createSingleEvent(eventName, startTime, endTime, description, location, visibility,
          autoDecline);
    }
  }

  /**
   * Creates a single event.
   *
   * @param eventName   The name of the event.
   * @param startTime   The start time of the event.
   * @param endTime     The end time of the event.
   * @param description The description of the event.
   * @param location    The location of the event.
   * @param visibility  The visibility of the event.
   * @param autoDecline Flag indicating whether the event should auto-decline conflicting events.
   * @throws EventConflictException if the event conflicts with an existing event.
   */
  private void createSingleEvent(String eventName, Temporal startTime, Temporal endTime,
                                 String description, String location, String visibility,
                                 boolean autoDecline) throws EventConflictException {
    IEvent event = buildEvent(eventName, startTime, endTime, description, location, visibility,
        null, null, null, autoDecline);
    validateAndAddEvent(event, autoDecline);
  }

  /**
   * Creates recurring events based on the provided parameters.
   *
   * @param eventName         The name of the event.
   * @param startTime         The start time of the event.
   * @param endTime           The end time of the event.
   * @param description       The description of the event.
   * @param location          The location of the event.
   * @param visibility        The visibility of the event.
   * @param recurringDays     A string of characters representing recurring days.
   * @param occurrenceCount   The number of occurrences of the event (for recurring events).
   * @param recurrenceEndDate The end date of the recurrence (for recurring events).
   * @throws EventConflictException if any of the recurring events conflict with existing events.
   */
  private void createRecurringEvents(String eventName, Temporal startTime, Temporal endTime,
                                     String description, String location, String visibility,
                                     String recurringDays, Integer occurrenceCount,
                                     Temporal recurrenceEndDate, boolean autoDecline)
      throws EventConflictException {
    List<IEvent> recurringEvents = buildRecurringEvents(eventName, startTime, endTime, description,
        location, visibility, recurringDays, occurrenceCount, recurrenceEndDate);
    validateAndAddEvents(recurringEvents);
  }

  /**
   * Builds a single event object.
   *
   * @param eventName         The name of the event.
   * @param startTime         The start time of the event.
   * @param endTime           The end time of the event.
   * @param description       The description of the event.
   * @param location          The location of the event.
   * @param visibility        The visibility of the event.
   * @param occurrenceCount   The number of occurrences of the event (for recurring events).
   * @param recurrenceEndDate The end date of the recurrence (for recurring events).
   * @param autoDecline       Flag indicating whether the event should auto-decline conflicting
   *                          events.
   * @return A newly created event.
   */
  private IEvent buildEvent(String eventName, Temporal startTime, Temporal endTime,
                            String description, String location, String visibility,
                            Integer occurrenceCount, String recurringDays,
                            Temporal recurrenceEndDate, boolean autoDecline) {
    return Event.builder()
        .name(eventName)
        .startTime(startTime)
        .endTime(endTime)
        .description(description)
        .location(location)
        .visibility(visibility)
        .recurringDays(recurringDays)
        .occurrenceCount(occurrenceCount)
        .recurrenceEndDate(recurrenceEndDate)
        .isAutoDecline(autoDecline)
        .build();
  }

  /**
   * Builds a list of recurring events based on the provided parameters.
   *
   * @param eventName         The name of the event.
   * @param startTime         The start time of the event.
   * @param endTime           The end time of the event.
   * @param description       The description of the event.
   * @param location          The location of the event.
   * @param visibility        The visibility of the event.
   * @param recurringDays     A string of characters representing recurring days.
   * @param occurrenceCount   The number of occurrences of the event (for recurring events).
   * @param recurrenceEndDate The end date of the recurrence (for recurring events).
   * @return A list of recurring events.
   */
  private List<IEvent> buildRecurringEvents(String eventName, Temporal startTime, Temporal endTime,
                                            String description, String location, String visibility,
                                            String recurringDays, Integer occurrenceCount,
                                            Temporal recurrenceEndDate) {
    Set<DayOfWeek> daysOfWeek = parseDaysOfWeek(recurringDays);
    Duration eventDuration = Duration.between(startTime, endTime == null
        ? startTime.plus(1, ChronoUnit.DAYS) : endTime);

    List<IEvent> recurringEvents = new ArrayList<>();
    Temporal currentStartTime = startTime;
    int occurrencesCreated = 0;

    while ((occurrenceCount != null && occurrencesCreated < occurrenceCount)
        || (recurrenceEndDate != null && TimeUtil.isFirstBeforeSecond(currentStartTime,
        recurrenceEndDate))) {

      DayOfWeek currentDay = DayOfWeek.of(currentStartTime.get(ChronoField.DAY_OF_WEEK));
      if (daysOfWeek.contains(currentDay)) {
        recurringEvents.add(buildEvent(eventName, currentStartTime,
            currentStartTime.plus(eventDuration), description, location, visibility,
            occurrenceCount, recurringDays, recurrenceEndDate, true));
        occurrencesCreated++;
      }
      currentStartTime = currentStartTime.plus(1, ChronoUnit.DAYS);
    }
    return recurringEvents;
  }

  /**
   * Validates and adds a single event to the calendar.
   *
   * @param event       The event to be added.
   * @param autoDecline Flag indicating whether the event should auto-decline conflicting events.
   * @throws EventConflictException if the event conflicts with an existing event.
   */
  private void validateAndAddEvent(IEvent event, boolean autoDecline)
      throws EventConflictException {
    if (autoDecline) {
      for (IEvent existingEvent : events) {
        if (TimeUtil.isConflicting(event.getStartTime(), event.getEndTime(),
            existingEvent.getStartTime(), existingEvent.getEndTime())) {
          throw new EventConflictException("Event conflicts with existing event: " + existingEvent);
        }
      }
    }
    events.add(event);
  }

  /**
   * Validates and adds a list of recurring events to the calendar.
   *
   * @param newEvents The list of events to be added.
   * @throws EventConflictException if any of the recurring events conflict with existing events.
   */
  private void validateAndAddEvents(List<IEvent> newEvents)
      throws EventConflictException {
    for (IEvent newEvent : newEvents) {
      for (IEvent existingEvent : events) {
        if (newEvent.getName() != existingEvent.getName() &&
            TimeUtil.isConflicting(newEvent.getStartTime(), newEvent.getEndTime(),
                existingEvent.getStartTime(), existingEvent.getEndTime())) {
          throw new EventConflictException("Recurring event conflicts with existing event: "
              + existingEvent);
        }
      }
    }
    events.addAll(newEvents);
  }

  /**
   * Edits an event based on the specified property and value.
   *
   * @param eventName The name of the event.
   * @param startTime The start time of the event.
   * @param endTime   The end time of the event.
   * @param property  The property to be edited.
   * @param value     The new value of the property.
   */
  @Override
  public void editEvent(String eventName, Temporal startTime, Temporal endTime, String property,
                        String value, boolean isRecurringEvents) {
    List<IEvent> eventsToEdit = findEvents(eventName, startTime, endTime, isRecurringEvents);
    List<IEvent> updatedEvents = new ArrayList<>();

    // TODO: Edit events should work for all events both single/recurring
    if (!isRecurringEvents) {
      for (IEvent event : eventsToEdit) {
        IEvent updatedEvent = event.updateProperty(property, value);
        events.remove(event);
        if (updatedEvent.isAutoDecline()) {
          checkForConflicts(updatedEvent);
        }
        updatedEvents.add(updatedEvent);
      }
      events.addAll(updatedEvents);
    } else {
      IEvent firstEvent = eventsToEdit.get(0);
      firstEvent = firstEvent.updateProperty(property, value);
      createEvent(firstEvent.getName(), firstEvent.getStartTime(), firstEvent.getEndTime(),
          firstEvent.getRecurringDays(), firstEvent.getOccurrenceCount() == null ? null
              : Integer.toString(firstEvent.getOccurrenceCount()),
          firstEvent.getRecurrenceEndDate(), firstEvent.getDescription(),
          firstEvent.getLocation(), firstEvent.getVisibility().getValue(),
          firstEvent.isAutoDecline());
    }
    events.removeAll(eventsToEdit);
  }

  /**
   * Checks if an event conflicts with existing events in the calendar.
   *
   * @param updatedEvent The event to check for conflicts.
   * @throws EventConflictException if a conflict is found.
   */
  private void checkForConflicts(IEvent updatedEvent) {
    for (IEvent existingEvent : events) {
      if (TimeUtil.isConflicting(updatedEvent.getStartTime(), updatedEvent.getEndTime(),
          existingEvent.getStartTime(), existingEvent.getEndTime())) {
        throw new EventConflictException("Event conflicts with existing event: " + existingEvent);
      }
    }
  }

  /**
   * Returns a list of events that fall within the given date range, formatted for display.
   *
   * @param startDateTime The start date-time for the range.
   * @param endDateTime   The end date-time for the range.
   * @return A list of formatted event strings.
   */
  @Override
  public List<String> getEventsForPrinting(Temporal startDateTime, Temporal endDateTime) {
    if (endDateTime == null) {
      endDateTime = TimeUtil.getLocalDateTimeFromTemporal(startDateTime).toLocalDate()
          .plusDays(1).atStartOfDay();
    }

    Temporal finalEndDateTime = endDateTime;
    return events.stream()
        .filter(event -> TimeUtil.isConflicting(event.getStartTime(),
            event.getEndTime(), startDateTime, finalEndDateTime))
        .sorted((event1, event2) ->
            Math.toIntExact(TimeUtil.difference(event2.getStartTime(), event1.getStartTime())))
        .map(IEvent::formatForDisplay)
        .collect(Collectors.toList());
  }

  /**
   * Exports the events to a CSV file.
   *
   * @param filename The name of the file to export.
   * @return The file path of the exported CSV.
   */
  @Override
  public String export(String filename) {
    return exportEventAsGoogleCalendarCsv(events, filename);
  }

  /**
   * Checks the availability status of the user for a given date-time.
   *
   * @param dateTime The date-time to check.
   * @return The availability status ("busy" or "available").
   */
  @Override
  public String showStatus(Temporal dateTime) {
    boolean isBusy = events.stream().anyMatch(event ->
        TimeUtil.isActiveAt(dateTime, event.getStartTime(), event.getEndTime()));
    return isBusy ? EventConstants.Status.BUSY : EventConstants.Status.AVAILABLE;
  }

  /**
   * Parses a string of day characters into a set of DayOfWeek values.
   *
   * @param daysString A string of characters representing the days (e.g., "MWF").
   * @return A set of DayOfWeek values corresponding to the input string.
   */
  private Set<DayOfWeek> parseDaysOfWeek(String daysString) {
    Set<DayOfWeek> days = new HashSet<>();
    for (char day : daysString.toUpperCase().toCharArray()) {
      DayOfWeek dayOfWeek = dayMap.get(day);
      if (dayOfWeek == null) {
        throw new IllegalArgumentException("Invalid day character: " + day);
      }
      days.add(dayOfWeek);
    }
    return days;
  }

  /**
   * Finds events matching the specified name and time range.
   *
   * @param eventName The name of the event to search for.
   * @param startTime The start time for the search range.
   * @param endTime   The end time for the search range.
   * @return A list of events matching the criteria.
   */
  private List<IEvent> findEvents(String eventName, Temporal startTime,
                                  Temporal endTime, boolean isRecurring) {
    return events.stream()
        .filter(event -> event.getName().equals(eventName))
        .filter(event -> TimeUtil.isWithinTimeRange(startTime, endTime,
            event.getStartTime(), event.getEndTime()))
        .filter(event -> !isRecurring || (event.getRecurringDays() != null))
        .sorted((event1, event2) ->
            Math.toIntExact(TimeUtil.difference(event2.getStartTime(), event1.getStartTime())))
        .collect(Collectors.toList());
  }
}
