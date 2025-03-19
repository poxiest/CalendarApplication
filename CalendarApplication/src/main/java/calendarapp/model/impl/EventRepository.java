package calendarapp.model.impl;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import calendarapp.model.EventConflictException;
import calendarapp.model.IEventRepository;
import calendarapp.utils.TimeUtil;

import static calendarapp.model.impl.EventConstants.DaysOfWeek.parseDaysOfWeek;

public class EventRepository implements IEventRepository {
  private final List<Event> events;

  public EventRepository(List<Event> events) {
    this.events = new ArrayList<>(events);
  }

  @Override
  public void create(String eventName, Temporal startTime, Temporal endTime,
                     String description, String location, String visibility,
                     String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
                     boolean autoDecline) throws EventConflictException {

    boolean isRecurring = recurringDays != null;
    Integer occurrence = occurrenceCount != null ? Integer.parseInt(occurrenceCount) : null;

    if (!isRecurring) {
      Event event = createSingleEvent(eventName, startTime, endTime, description, location, visibility,
          recurringDays, occurrence, recurrenceEndDate);
      validateEvents(List.of(event), null);
      events.add(event);
    } else {
      List<Event> recurringEvents = createRecurringEvents(eventName, startTime, endTime, description,
          location, visibility, recurringDays, occurrence, recurrenceEndDate);
      validateEvents(recurringEvents, null);
      events.addAll(recurringEvents);
    }

  }

  @Override
  public void update(String eventName, Temporal startTime, Temporal endTime, String property,
                     String value, boolean isRecurringEvents) {
    List<Event> eventsToUpdate = searchMatchingEvents(eventName, startTime, endTime, isRecurringEvents);
    List<Event> updatedEvents = new ArrayList<>();

    if (!isRecurringEvents) {
      for (Event event : eventsToUpdate) {
        Event updatedEvent = event.updateProperty(property, value);
        updatedEvents.add(updatedEvent);
      }
    } else {
      Event firstEvent = eventsToUpdate.get(0).updateProperty(property, value);
      updatedEvents = createRecurringEvents(firstEvent.getName(), firstEvent.getStartTime(),
          firstEvent.getEndTime(), firstEvent.getDescription(), firstEvent.getLocation(),
          firstEvent.getVisibility().getValue(), firstEvent.getRecurringDays(), firstEvent.getOccurrenceCount(),
          firstEvent.getRecurrenceEndDate());
    }
    validateEvents(updatedEvents, eventsToUpdate);
    events.addAll(updatedEvents);
    events.removeAll(eventsToUpdate);
  }

  @Override
  public IEventRepository get(String eventName, Temporal startTime, Temporal endTime) {
    return new EventRepository(searchMatchingEvents(eventName, startTime, endTime, false));
  }

  @Override
  public List<String> getFormattedEvents(Temporal startTime, Temporal endTime) {
    List<Event> requiredEvents = findOverlappingEvents(startTime, endTime);
    return requiredEvents.stream()
        .map(this::formatEventForDisplay)
        .collect(Collectors.toList());
  }

  @Override
  public boolean isActiveAt(Temporal time) {
    return !findOverlappingEvents(time, time).isEmpty();
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
   * @return A newly created event.
   */
  private Event createSingleEvent(String eventName, Temporal startTime, Temporal endTime,
                                  String description, String location, String visibility,
                                  String recurringDays, Integer occurrenceCount,
                                  Temporal recurrenceEndDate) {
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
        .isAutoDecline(true)
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
  private List<Event> createRecurringEvents(String eventName, Temporal startTime, Temporal endTime,
                                            String description, String location, String visibility,
                                            String recurringDays, Integer occurrenceCount,
                                            Temporal recurrenceEndDate) {

    Set<DayOfWeek> daysOfWeek = parseDaysOfWeek(recurringDays);
    Duration eventDuration = Duration.between(startTime, endTime == null
        ? startTime.plus(1, ChronoUnit.DAYS) : endTime);

    List<Event> recurringEvents = new ArrayList<>();
    Temporal currentStartTime = startTime;
    int occurrencesCreated = 0;

    while ((occurrenceCount != null && occurrencesCreated < occurrenceCount)
        || (recurrenceEndDate != null && TimeUtil.isFirstBeforeSecond(currentStartTime,
        recurrenceEndDate))) {

      DayOfWeek currentDay = DayOfWeek.of(currentStartTime.get(ChronoField.DAY_OF_WEEK));
      if (daysOfWeek.contains(currentDay)) {
        recurringEvents.add(createSingleEvent(eventName, currentStartTime,
            currentStartTime.plus(eventDuration), description, location, visibility,
            recurringDays, occurrenceCount, recurrenceEndDate));
        occurrencesCreated++;
      }
      currentStartTime = currentStartTime.plus(1, ChronoUnit.DAYS);
    }
    return recurringEvents;
  }

  /**
   * validateEvents checks for conflicts before adding a new event
   *
   * @param newEvents contains list of new events that needs to be added
   * @param oldEvents contains list of old events that are to be updated if present
   * @throws EventConflictException if there is conflict while adding a new event
   */
  private void validateEvents(List<Event> newEvents, List<Event> oldEvents)
      throws EventConflictException {
    if (oldEvents != null) {
      events.removeAll(oldEvents);
    }

    for (Event newEvent : newEvents) {
      for (Event existingEvent : events) {
        if (!Objects.equals(newEvent.getName(), existingEvent.getName()) &&
            TimeUtil.isConflicting(newEvent.getStartTime(), newEvent.getEndTime(),
                existingEvent.getStartTime(), existingEvent.getEndTime())) {
          if (oldEvents != null) {
            events.addAll(oldEvents);
          }
          throw new EventConflictException("Event conflicts with existing event: "
              + existingEvent);
        }
      }
    }
  }

  /**
   * Finds events matching the specified name and time range.
   *
   * @param eventName The name of the event to search for.
   * @param startTime The start time for the search range.
   * @param endTime   The end time for the search range.
   * @return A list of events matching the criteria.
   */
  private List<Event> searchMatchingEvents(String eventName, Temporal startTime,
                                           Temporal endTime, boolean isRecurring) {
    return events.stream()
        .filter(event -> eventName == null || event.getName().equals(eventName))
        .filter(event -> TimeUtil.isWithinTimeRange(startTime, endTime,
            event.getStartTime(), event.getEndTime()))
        .filter(event -> !isRecurring || (event.getRecurringDays() != null))
        .sorted((event1, event2) ->
            Math.toIntExact(TimeUtil.difference(event2.getStartTime(), event1.getStartTime())))
        .collect(Collectors.toList());
  }

  private List<Event> findOverlappingEvents(Temporal startTime, Temporal endTime) {
    return events.stream()
        .filter(event -> TimeUtil.isConflicting(event.getStartTime(),
            event.getEndTime(), startTime, endTime))
        .sorted((event1, event2) ->
            Math.toIntExact(TimeUtil.difference(event2.getStartTime(), event1.getStartTime())))
        .collect(Collectors.toList());
  }

  private String formatEventForDisplay(Event event) {
    return String.format("• %s - %s to %s %s",
        event.getName(),
        event.getStartTime(),
        event.getEndTime(),
        event.getLocation() != null && !event.getLocation().isEmpty()
            ? "- Location: " + event.getLocation()
            : "");
  }
}
