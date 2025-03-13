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
import static calendarapp.utils.TimeUtil.isFirstBeforeSecond;

/**
 * Concrete implementation of Calendar Model managing {@link IEvent} events.
 */
public class CalendarModel implements ICalendarModel {

  /**
   * List to store all the events in the calendar.
   */
  private final List<IEvent> events;

  /**
   * Map to store the recurring days characters to {@link DayOfWeek}.
   */
  private final Map<Character, DayOfWeek> dayMap;

  /**
   * Default constructor of the class.
   * It initialises the events object and the dayMap.
   */
  public CalendarModel() {
    this.events = new ArrayList<>();

    this.dayMap = Map.of(
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
   * Concrete implementation of create event. Checks for autodecline, conflict and
   * all other basic validations to add it into the list.
   *
   * @param eventName         the name of the event.
   * @param startTime         the start time of the event.
   * @param endTime           the end time of the event (can be null for all-day events).
   * @param recurringDays     string representation of days on which the event repeats
   *                          (e.g., "MWF" for Monday, Wednesday, Friday).
   * @param occurrenceCount   number of occurrences for recurring events
   *                          (can be null if recurrenceEndDate is provided).
   * @param recurrenceEndDate the end date for recurring events
   *                          (can be null if occurrenceCount is provided).
   * @param description       the description of the event (optional).
   * @param location          the location of the event (optional).
   * @param visibility        the visibility setting of the event (optional).
   * @param autoDecline       whether to automatically decline conflicting events.
   * @throws EventConflictException When two events overlap.
   */
  @Override
  public void createEvent(String eventName, Temporal startTime, Temporal endTime,
                          String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
                          String description, String location, String visibility,
                          boolean autoDecline) throws EventConflictException {

    boolean isRecurring = recurringDays != null;
    Integer occurrence = occurrenceCount != null ? Integer.parseInt(occurrenceCount) : null;

    if (!isRecurring) {
      IEvent event = createSingleEvent(eventName, startTime, endTime, description, location,
          visibility, recurringDays, occurrence, recurrenceEndDate, autoDecline);

      if (autoDecline) {
        for (IEvent existingEvent : events) {
          if (event.conflictsWith(existingEvent)) {
            throw new EventConflictException("Event conflicts with existing event: "
                + existingEvent);
          }
        }
      }
      events.add(event);
    } else {
      List<IEvent> recurringEvents = createRecurringEvents(
          eventName, startTime, endTime, description, location, visibility,
          recurringDays, occurrence, recurrenceEndDate);

      for (IEvent newEvent : recurringEvents) {
        for (IEvent existingEvent : events) {
          if (newEvent.conflictsWith(existingEvent)) {
            throw new EventConflictException("Recurring event conflicts with existing event: "
                + existingEvent.formatForDisplay());
          }
        }
      }
      events.addAll(recurringEvents);
    }
  }

  /**
   * Concrete implementation of edit event.
   *
   * @param eventName the name of the event(s) to edit.
   * @param startTime the start time to use for finding matching events (optional).
   * @param endTime   the end time to use for finding matching events (optional).
   * @param property  the property to modify (subject, description, location, etc.).
   * @param value     the new value for the specified property.
   */
  @Override
  public void editEvent(String eventName, Temporal startTime, Temporal endTime, String property,
                        String value) {
    List<IEvent> eventsToEdit = findEvents(eventName, startTime, endTime);

    List<IEvent> updatedEvents = new ArrayList<>();
    for (IEvent event : eventsToEdit) {
      IEvent updatedEvent = event.updateProperty(property, value);

      if (updatedEvent.shouldAutoDecline()) {
        for (IEvent existingEvent : events) {
          if (existingEvent != event && updatedEvent.conflictsWith(existingEvent)) {
            throw new EventConflictException("Event conflicts with existing event: "
                + existingEvent);
          }
        }
      }

      updatedEvents.add(updatedEvent);
    }

    events.removeAll(eventsToEdit);
    events.addAll(updatedEvents);
  }

  /**
   * Gets a list of {@link IEvent} between the given timelines sorted in ascending order.
   *
   * @param startDateTime the start of the time range.
   * @param endDateTime   the end of the time range (if null, defaults to one day after startTime).
   * @return List of events found.
   */
  @Override
  public List<IEvent> getEventsBetween(Temporal startDateTime, Temporal endDateTime) {
    if (endDateTime == null) {
      endDateTime = (TimeUtil.getLocalDateTimeFromTemporal(startDateTime)
          .toLocalDate().plusDays(1).atStartOfDay());
    }
    Temporal finalEndDateTime = endDateTime;
    return events.stream()
        .filter(event -> event.hasIntersectionWith(startDateTime, finalEndDateTime))
        .sorted((event1, event2) -> event2.getDifference(event1))
        .collect(Collectors.toList());
  }

  /**
   * Exports the calendar events to a CSV file.
   *
   * @param filename the base name of the file to export to.
   * @return Absolute path of the file.
   */
  @Override
  public String export(String filename) {
    String filePath = filename + ".csv";
    return exportEventAsGoogleCalendarCsv(events, filePath);
  }

  /**
   * Returns the status as Busy/Available of the user.
   *
   * @param dateTime the date and time to check.
   * @return Status of the user at the specified time.
   */
  @Override
  public String showStatus(Temporal dateTime) {
    boolean isBusy = events.stream().anyMatch(event -> event.isActiveAt(dateTime));
    if (isBusy) {
      return EventConstants.Status.BUSY;
    } else {
      return EventConstants.Status.AVAILABLE;
    }
  }

  /**
   * Creates a single event.
   *
   * @param eventName         the name of the event.
   * @param startTime         the start time of the event.
   * @param endTime           the end time of the event (can be null for all-day events).
   * @param description       the description of the event (optional).
   * @param location          the location of the event (optional).
   * @param visibility        the visibility setting of the event (optional).
   * @param recurringDays     string representation of days on which the event repeats
   *                          (e.g., "MWF" for Monday, Wednesday, Friday).
   * @param occurrenceCount   number of occurrences for recurring events
   *                          (can be null if recurrenceEndDate is provided).
   * @param recurrenceEndDate the end date for recurring events
   *                          (can be null if occurrenceCount is provided).
   * @param autoDecline       whether to automatically decline conflicting events.
   * @return Return new event.
   */
  private IEvent createSingleEvent(String eventName, Temporal startTime, Temporal endTime,
                                   String description, String location, String visibility,
                                   String recurringDays, Integer occurrenceCount,
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
   * Creates single events of recurring condition.
   *
   * @param eventName         the name of the event.
   * @param startTime         the start time of the event.
   * @param endTime           the end time of the event (can be null for all-day events).
   * @param description       the description of the event (optional).
   * @param location          the location of the event (optional).
   * @param visibility        the visibility setting of the event (optional).
   * @param recurringDays     string representation of days on which the event repeats
   *                          (e.g., "MWF" for Monday, Wednesday, Friday).
   * @param occurrenceCount   number of occurrences for recurring events
   *                          (can be null if recurrenceEndDate is provided).
   * @param recurrenceEndDate the end date for recurring events
   *                          (can be null if occurrenceCount is provided).
   * @return List of events.
   */
  private List<IEvent> createRecurringEvents(String eventName, Temporal startTime, Temporal endTime,
                                             String description, String location,
                                             String visibility, String recurringDays,
                                             Integer occurrenceCount, Temporal recurrenceEndDate) {

    List<IEvent> recurringEvents = new ArrayList<>();
    Set<DayOfWeek> daysOfWeek = parseDaysOfWeek(recurringDays);
    if (endTime == null) {
      endTime = startTime.plus(1, ChronoUnit.DAYS);
    }
    Duration eventDuration = Duration.between(startTime, endTime);

    int occurrencesCreated = 0;
    Temporal currentStartTime = startTime;

    while ((occurrenceCount != null && occurrencesCreated < occurrenceCount)
        || (recurrenceEndDate != null
        && isFirstBeforeSecond(currentStartTime, recurrenceEndDate))) {

      DayOfWeek currentDay = DayOfWeek.of(currentStartTime.get(ChronoField.DAY_OF_WEEK));

      if (daysOfWeek.contains(currentDay)) {
        Temporal eventEndTime = currentStartTime.plus(eventDuration);

        IEvent event = createSingleEvent(
            eventName, currentStartTime, eventEndTime,
            description, location, visibility, recurringDays,
            occurrenceCount, recurrenceEndDate, true
        );

        recurringEvents.add(event);
        occurrencesCreated++;
      }

      currentStartTime = currentStartTime.plus(1, ChronoUnit.DAYS);
    }

    return recurringEvents;
  }

  /**
   * Parses the recurring days of the recurrence properties.
   *
   * @param daysString eg: "MRW", "FSU"
   * @return Set of {@link DayOfWeek} to calculate the events.
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
   * Method used to find {@link IEvent}.
   *
   * @param eventName Name of the event.
   * @param startTime Start time of the event.
   * @param endTime   End time of the event.
   * @return List of found {@link IEvent}.
   */
  private List<IEvent> findEvents(String eventName, Temporal startTime, Temporal endTime) {
    return events.stream()
        .filter(event -> event.matchesName(eventName))
        .filter(event -> event.isWithinTimeRange(startTime, endTime))
        .collect(Collectors.toList());
  }
}