package calendarapp.model.impl;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import calendarapp.model.EventConflictException;
import calendarapp.model.EventVisibility;
import calendarapp.model.ICalendarModel;
import calendarapp.model.IEvent;

import static calendarapp.model.impl.CalendarExporter.exportEventAsGoogleCalendarCsv;
import static calendarapp.utils.TimeUtil.getLocalDateTimeFromString;
import static calendarapp.utils.TimeUtil.isEqual;
import static calendarapp.utils.TimeUtil.isFirstAfterSecond;
import static calendarapp.utils.TimeUtil.isFirstBeforeSecond;

public class CalendarModel implements ICalendarModel {

  private static final String PROPERTY_NAME = "eventname";
  private static final String PROPERTY_START_TIME = "from";
  private static final String PROPERTY_END_TIME = "to";
  private static final String PROPERTY_DESCRIPTION = "description";
  private static final String PROPERTY_LOCATION = "location";
  private static final String PROPERTY_VISIBILITY = "visibility";

  private static final String STATUS_BUSY = "Busy";
  private static final String STATUS_AVAILABLE = "Available";

  private final List<IEvent> events;

  public CalendarModel() {
    this.events = new ArrayList<>();
  }

  @Override
  public void createEvent(String eventName, Temporal startTime, Temporal endTime,
                          String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
                          String description, String location, String visibility,
                          boolean autoDecline) throws EventConflictException {

    boolean isRecurring = recurringDays != null;
    Integer occurrence = occurrenceCount != null ? Integer.parseInt(occurrenceCount) : null;
    EventVisibility visibilityEnum = visibility != null ?
        EventVisibility.getVisibility(visibility) : EventVisibility.PRIVATE;

    if (!isRecurring) {
      IEvent event = createSingleEvent(eventName, startTime, endTime, description, location,
          visibilityEnum, recurringDays, occurrence, recurrenceEndDate, autoDecline);

      if (autoDecline) {
        for (IEvent existingEvent : events) {
          if (event.conflictsWith(existingEvent)) {
            throw new EventConflictException("Event conflicts with existing event: "
                + existingEvent.getName());
          }
        }
      }
      events.add(event);
    } else {
      List<Event> recurringEvents = createRecurringEvents(
          eventName, startTime, endTime, description, location, visibilityEnum,
          recurringDays, occurrence, recurrenceEndDate);

      for (Event newEvent : recurringEvents) {
        for (IEvent existingEvent : events) {
          if (newEvent.conflictsWith(existingEvent)) {
            throw new EventConflictException("Recurring event conflicts with existing event: " +
                existingEvent.getName());
          }
        }
      }
      events.addAll(recurringEvents);
    }
  }

  @Override
  public void editEvent(String eventName, Temporal startTime, Temporal endTime, String property,
                        String value) {
    List<IEvent> eventsToEdit = findEvents(eventName, startTime, endTime);

    List<IEvent> updatedEvents = new ArrayList<>();
    for (IEvent event : eventsToEdit) {
      IEvent updatedEvent = updateEventProperty(event, property, value);
      updatedEvents.add(updatedEvent);
    }

    events.removeAll(eventsToEdit);
    events.addAll(updatedEvents);
  }

  @Override
  public List<IEvent> printEvents(Temporal startDateTime, Temporal endDateTime) {
    if (endDateTime == null) {
      endDateTime = ((LocalDateTime) startDateTime)
          .toLocalDate().atTime(23, 59, 59);
    }
    return findEvents(null, startDateTime, endDateTime);
  }

  @Override
  public void export(String filename) throws IOException {
    String filePath = filename + ".csv";
    exportEventAsGoogleCalendarCsv(events, filePath);

  }

  @Override
  public String showStatus(Temporal dateTime) {
    boolean isBusy = events.stream().anyMatch(event -> isEventActiveAt(event, dateTime));
    if (isBusy) {
      return STATUS_BUSY;
    } else {
      return STATUS_AVAILABLE;
    }
  }

  private boolean isEventActiveAt(IEvent event, Temporal dateTime) {
    return isEqual(dateTime, event.getStartDateTime()) || isEqual(dateTime, event.getEndDateTime())
        || (isFirstAfterSecond(dateTime, event.getStartDateTime())
        && isFirstBeforeSecond(dateTime, event.getEndDateTime()));
  }

  private Event createSingleEvent(String eventName, Temporal startTime, Temporal endTime,
                                  String description, String location, EventVisibility visibility,
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

  private List<Event> createRecurringEvents(String eventName, Temporal startTime, Temporal endTime,
                                            String description, String location,
                                            EventVisibility visibility, String recurringDays,
                                            Integer occurrenceCount, Temporal recurrenceEndDate) {

    List<Event> recurringEvents = new ArrayList<>();
    Set<DayOfWeek> daysOfWeek = parseDaysOfWeek(recurringDays);
    Duration eventDuration = Duration.between(startTime, endTime);

    int occurrencesCreated = 0;
    while ((occurrenceCount != null && occurrencesCreated < occurrenceCount) ||
        (recurrenceEndDate != null && isFirstBeforeSecond(startTime, recurrenceEndDate))) {

      DayOfWeek currentDay = DayOfWeek.of(startTime.get(ChronoField.DAY_OF_WEEK));

      if (daysOfWeek.contains(currentDay)) {
        Temporal eventEndTime = startTime.plus(eventDuration);

        Event event = createSingleEvent(
            eventName, startTime, eventEndTime,
            description, location, visibility, recurringDays,
            occurrenceCount, recurrenceEndDate, true
        );

        recurringEvents.add(event);
        occurrencesCreated++;
      }

      startTime = startTime.plus(1, ChronoUnit.DAYS);
    }

    return recurringEvents;
  }

  private Set<DayOfWeek> parseDaysOfWeek(String daysString) {
    Set<DayOfWeek> days = new HashSet<>();

    for (char day : daysString.toUpperCase().toCharArray()) {
      switch (day) {
        case 'M':
          days.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          days.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          days.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          days.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          days.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          days.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          days.add(DayOfWeek.SUNDAY);
          break;
        default:
          throw new IllegalArgumentException("Invalid day character: " + day);
      }
    }
    return days;
  }

  private List<IEvent> findEvents(String eventName, Temporal startTime, Temporal endTime) {
    return events.stream()
        .filter(event -> eventName == null || event.getName().equals(eventName))
        .filter(event -> startTime == null || isFirstAfterSecond(event.getStartDateTime(), startTime) || isEqual(event.getStartDateTime(), startTime))
        .filter(event -> endTime == null || isFirstBeforeSecond(event.getEndDateTime(), endTime) || isEqual(event.getEndDateTime(), endTime))
        .collect(Collectors.toList());
  }

  private IEvent updateEventProperty(IEvent event, String property, String value) {
    Event.Builder builder = Event.builder()
        .name(event.getName())
        .startTime(event.getStartDateTime())
        .endTime(event.getEndDateTime())
        .description(event.getDescription())
        .location(event.getLocation())
        .visibility(event.getVisibility())
        .recurringDays(event.getRecurringDays())
        .occurrenceCount(event.getOccurrenceCount())
        .recurrenceEndDate(event.getRecurrenceEndDate())
        .isAutoDecline(event.isAutoDecline());

    switch (property.toLowerCase()) {
      case PROPERTY_NAME:
        builder.name(value);
        break;

      case PROPERTY_START_TIME:
        builder.startTime(getLocalDateTimeFromString(value));
        if (event.isAutoDecline()) {
          Event tempEvent = builder.build();
          for (IEvent existingEvent : events) {
            if (existingEvent != event && tempEvent.conflictsWith(existingEvent)) {
              throw new EventConflictException("Event conflicts with existing event: "
                  + existingEvent.getName() + "\n");
            }
          }
        }
        break;

      case PROPERTY_END_TIME:
        builder.endTime(getLocalDateTimeFromString(value));
        if (event.isAutoDecline()) {
          Event tempEvent = builder.build();
          for (IEvent existingEvent : events) {
            if (existingEvent != event && tempEvent.conflictsWith(existingEvent)) {
              throw new EventConflictException("Event conflicts with existing event: "
                  + existingEvent.getName() + "\n");
            }
          }
        }
        break;

      case PROPERTY_DESCRIPTION:
        builder.description(value);
        break;

      case PROPERTY_LOCATION:
        builder.location(value);
        break;

      case PROPERTY_VISIBILITY:
        if (EventVisibility.getVisibility(value) == EventVisibility.DEFAULT) {
          throw new IllegalArgumentException("Invalid visibility value: " + value + "\n");
        }
        builder.visibility(EventVisibility.getVisibility(value));
        break;

      // TODO: Add logic for changing recurring properties
      default:
        throw new IllegalArgumentException("Cannot edit property: " + property + "\n");
    }

    return builder.build();
  }
}
