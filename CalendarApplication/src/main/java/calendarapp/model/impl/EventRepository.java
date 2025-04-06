package calendarapp.model.impl;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;
import calendarapp.model.IEvent;
import calendarapp.model.IEventRepository;
import calendarapp.model.SearchType;
import calendarapp.model.dto.CalendarExporterDTO;
import calendarapp.model.impl.searchstrategies.SearchEventFactory;
import calendarapp.utils.TimeUtil;

import static calendarapp.model.impl.Constants.DaysOfWeek.parseDaysOfWeek;
import static calendarapp.utils.TimeUtil.isAllDayEvent;
import static calendarapp.utils.TimeUtil.isFirstBeforeSecond;

/**
 * Implements IEventRepository to manage creation, updating, copying,
 * and retrieval of events including handling recurring events and conflicts.
 */
public class EventRepository implements IEventRepository {
  private final List<IEvent> events;
  private final SearchEventFactory searchEventFactory;

  /**
   * Constructs an empty EventRepository.
   * Initializes an empty list for storing events and sets up the search event factory for
   * searching events.
   */
  public EventRepository() {
    this.events = new ArrayList<>();
    searchEventFactory = new SearchEventFactory();
  }

  @Override
  public void create(String eventName, Temporal startTime, Temporal endTime,
                     String description, String location, String visibility,
                     String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
                     boolean autoDecline) throws EventConflictException {

    boolean isRecurring = recurringDays != null;
    Integer occurrence = occurrenceCount != null ? Integer.parseInt(occurrenceCount) : null;

    if (!isRecurring) {
      IEvent event = createSingleEvent(eventName, startTime, endTime, description, location,
          visibility,
          recurringDays, occurrence, recurrenceEndDate);
      validateEvents(List.of(event), null);
      events.add(event);
    } else {
      List<IEvent> recurringEvents = createRecurringEvents(eventName, startTime, endTime,
          description,
          location, visibility, recurringDays, occurrence, recurrenceEndDate);
      validateEvents(recurringEvents, null);
      events.addAll(recurringEvents);
    }

  }

  @Override
  public void update(String eventName, Temporal startTime, Temporal endTime, String property,
                     String value) {
    List<IEvent> eventsToUpdate;
    boolean isSingleEventUpdate = false;
    if (endTime != null) {
      eventsToUpdate = searchEventFactory.search(events, eventName, startTime, endTime, false,
          SearchType.EXACT);
      isSingleEventUpdate = true;
    } else {
      eventsToUpdate = searchEventFactory.search(events, eventName, startTime, endTime,
          isRecurringProperty(property), SearchType.MATCHING);
    }
    List<IEvent> updatedEvents = new ArrayList<>();

    if (!isRecurringProperty(property)) {
      boolean isFirstRecurringEventUpdated = false;
      for (IEvent event : eventsToUpdate) {
        if (event.getRecurringDays() != null && !isSingleEventUpdate) {
          if (!isFirstRecurringEventUpdated) {
            IEvent firstEvent = event.updateProperty(property, value);
            updatedEvents.addAll(getUpdatedRecurringEvents(firstEvent));
            isFirstRecurringEventUpdated = true;
          }
          continue;
        }
        IEvent updatedEvent = event.updateProperty(property, value);
        updatedEvents.add(updatedEvent);
      }
    } else {
      if (isSingleEventUpdate) {
        throw new InvalidCommandException("Cannot update a recurring property for a single event.");
      }
      IEvent firstEvent = eventsToUpdate.get(0).updateProperty(property, value);
      updatedEvents = getUpdatedRecurringEvents(firstEvent);
    }
    validateEvents(updatedEvents, eventsToUpdate);
    events.addAll(updatedEvents);
    events.removeAll(eventsToUpdate);
  }

  @Override
  public List<IEvent> getEvents(String eventName, Temporal startTime,
                                Temporal endTime, SearchType type) {
    return searchEventFactory.search(events, eventName, startTime, endTime, false, type)
        .stream().map(IEvent::deepCopyEvent).collect(Collectors.toList());
  }

  @Override
  public void copyEvents(List<IEvent> eventsToCopy, Temporal toStartTime,
                         ZoneId fromZoneId, ZoneId toZoneId) {
    if (eventsToCopy.size() == 0) {
      return;
    }

    Duration differenceBetween = TimeUtil.getDurationDifference(
        TimeUtil.changeZone(eventsToCopy.get(0).getStartTime(), fromZoneId, toZoneId), toStartTime);

    for (IEvent event : eventsToCopy) {
      Temporal startTime = TimeUtil.addDuration(TimeUtil.changeZone(event.getStartTime(),
          fromZoneId, toZoneId), differenceBetween);
      Temporal endTime = TimeUtil.addDuration(TimeUtil.changeZone(event.getEndTime(), fromZoneId,
          toZoneId), differenceBetween);
      create(event.getName(), startTime, endTime, event.getDescription(), event.getLocation(),
          event.getVisibility().getValue(), null, null, null, true);
    }
  }

  @Override
  public void changeTimeZone(ZoneId fromZoneId, ZoneId toZoneId) {
    List<IEvent> updatedEvents = new ArrayList<>();
    for (IEvent event : events) {
      Temporal startTime = TimeUtil.changeZone(event.getStartTime(),
          fromZoneId, toZoneId);
      Temporal endTime = TimeUtil.changeZone(event.getEndTime(), fromZoneId,
          toZoneId);
      Temporal recurrenceEndDate = null;
      if (event.getRecurrenceEndDate() != null) {
        recurrenceEndDate = TimeUtil.changeZone(event.getRecurrenceEndDate(), fromZoneId,
            toZoneId);
      }
      updatedEvents.add(createSingleEvent(event.getName(), startTime, endTime,
          event.getDescription(),
          event.getLocation(), event.getVisibility().getValue(), event.getRecurringDays(),
          event.getOccurrenceCount(), recurrenceEndDate));
    }
    events.clear();
    events.addAll(updatedEvents);
  }

  @Override
  public List<CalendarExporterDTO> getEventsForExport() {
    return events.stream()
        .map(event -> CalendarExporterDTO.builder()
            .subject(event.getName())
            .startDate(event.getStartTime())
            .endDate(event.getEndTime())
            .isAllDayEvent(isAllDayEvent(event.getStartTime(), event.getEndTime()))
            .description(event.getDescription())
            .location(event.getLocation())
            .visibility(event.getVisibility() != null ? event.getVisibility().getValue() : null)
            .build())
        .collect(Collectors.toList());
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
  private IEvent createSingleEvent(String eventName, Temporal startTime, Temporal endTime,
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
  private List<IEvent> createRecurringEvents(String eventName, Temporal startTime, Temporal endTime,
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
        || (recurrenceEndDate != null && isFirstBeforeSecond(currentStartTime,
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
   * validateEvents checks for conflicts before adding a new event.
   *
   * @param newEvents contains list of new events that needs to be added
   * @param oldEvents contains list of old events that are to be updated if present
   * @throws EventConflictException if there is conflict while adding a new event
   */
  private void validateEvents(List<IEvent> newEvents, List<IEvent> oldEvents)
      throws EventConflictException {
    if (oldEvents != null) {
      events.removeAll(oldEvents);
    }

    for (IEvent newEvent : newEvents) {
      for (IEvent existingEvent : events) {
        if (TimeUtil.isConflicting(newEvent.getStartTime(), newEvent.getEndTime(),
            existingEvent.getStartTime(), existingEvent.getEndTime())) {
          if (oldEvents != null) {
            events.addAll(oldEvents);
          }
          throw new EventConflictException("Event conflicts with existing event: "
              + existingEvent.getName());
        }
      }
    }
  }

  /**
   * Checks if the given property is related to event recurrence.
   *
   * @param property the name of the property
   * @return true if it is a recurring-related property, false otherwise
   */
  private boolean isRecurringProperty(String property) {
    String lowerCaseProperty = property.toLowerCase();
    return lowerCaseProperty.equals(Constants.PropertyKeys.RECURRING_DAYS)
        || lowerCaseProperty.equals(Constants.PropertyKeys.OCCURRENCE_COUNT)
        || lowerCaseProperty.equals(Constants.PropertyKeys.RECURRENCE_END_DATE);
  }

  /**
   * Reconstructs the list of recurring events based on the updated first event.
   *
   * @param firstEvent the modified base event
   * @return the list of updated recurring events
   */
  private List<IEvent> getUpdatedRecurringEvents(IEvent firstEvent) {
    return createRecurringEvents(firstEvent.getName(), firstEvent.getStartTime(),
        firstEvent.getEndTime(), firstEvent.getDescription(), firstEvent.getLocation(),
        firstEvent.getVisibility().getValue(), firstEvent.getRecurringDays(),
        firstEvent.getOccurrenceCount(),
        firstEvent.getRecurrenceEndDate());
  }
}
