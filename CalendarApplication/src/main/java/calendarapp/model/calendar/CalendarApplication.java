package calendarapp.model.calendar;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import calendarapp.model.event.Event;
import calendarapp.model.event.EventConflictException;
import calendarapp.model.event.EventVisibility;
import calendarapp.model.event.IEvent;

public class CalendarApplication implements ICalendarApplication {

  private static final String PROPERTY_NAME = "name";
  private static final String PROPERTY_START_TIME = "start_time";
  private static final String PROPERTY_END_TIME = "end_time";
  private static final String PROPERTY_DESCRIPTION = "description";
  private static final String PROPERTY_LOCATION = "location";
  private static final String PROPERTY_VISIBILITY = "visibility";
  private static final String VISIBILITY_PRIVATE = "private";
  private static final String VISIBILITY_PUBLIC = "public";

  public static final String STATUS_BUSY = "BUSY";
  public static final String STATUS_AVAILABLE = "AVAILABLE";

  private final List<IEvent> events;

  public CalendarApplication() {
    this.events = new ArrayList<>();
  }

  @Override
  public void createEvent(String eventName, LocalDateTime startTime, LocalDateTime endTime,
                          String recurringDays, Integer occurrenceCount, LocalDate recurrenceEndDate,
                          String description, String location, EventVisibility visibility,
                          boolean autoDecline) throws EventConflictException {

    boolean isRecurring = recurringDays != null;
    if (!isRecurring) {
      Event event = createSingleEvent(eventName, startTime, endTime, description, location, visibility,
              recurringDays, occurrenceCount, recurrenceEndDate, autoDecline);

      if (autoDecline) {
        for (IEvent existingEvent : events) {
          if (event.conflictsWith(existingEvent)) {
            throw new EventConflictException("Event conflicts with existing event: " + existingEvent.getName());
          }
        }
      }
      events.add(event);
    } else {
      List<Event> recurringEvents = createRecurringEvents(
              eventName, startTime, endTime, description, location, visibility,
              recurringDays, occurrenceCount, recurrenceEndDate);

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
  public void editEvent(String eventName, LocalDateTime startTime, LocalDateTime endTime, String property, String value) {
    if (eventName == null || property == null) {
      throw new IllegalArgumentException("Event name and property cannot be null");
    }

    List<IEvent> eventsToEdit;

    if (startTime == null && endTime == null) {
      eventsToEdit = findEventsByName(eventName);
    } else if (endTime == null) {
      eventsToEdit = findEventsByNameAndStartTime(eventName, startTime);
    } else {
      eventsToEdit = findEventsByStartAndEndTimeAndNameIfPresent(eventName, startTime, endTime);
    }

    List<IEvent> updatedEvents = new ArrayList<>();
    for (IEvent event : eventsToEdit) {
      IEvent updatedEvent = updateEventProperty((Event) event, property, value);
      updatedEvents.add(updatedEvent);
    }
    events.removeAll(eventsToEdit);
    events.addAll(updatedEvents);
  }

  @Override
  public List<IEvent> printEvents(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (startDateTime == null && endDateTime == null) {
      throw new IllegalArgumentException("Start time and end time cannot be null");
    }
    if (endDateTime == null) {
      startDateTime = startDateTime.toLocalDate().atStartOfDay();
      endDateTime = startDateTime.toLocalDate().atTime(23, 59, 59, 999999999);
    }
    return findEventsByStartAndEndTimeAndNameIfPresent(null, startDateTime, endDateTime);
  }

  @Override
  public void export() {

  }

  @Override
  public void showStatus(LocalDateTime dateTime) {
    boolean isBusy = events.stream()
            .anyMatch(event -> isEventActiveAt(event, dateTime));

    if (isBusy) {
      System.out.println(STATUS_BUSY);
    } else {
      System.out.println(STATUS_AVAILABLE);
    }
  }

  private boolean isEventActiveAt(IEvent event, LocalDateTime dateTime) {
    return !dateTime.isBefore(event.getStartDateTime()) &&
            !dateTime.isAfter(event.getEndDateTime());
  }

  private Event createSingleEvent(String eventName, LocalDateTime startTime, LocalDateTime endTime,
                                  String description, String location, EventVisibility visibility,
                                  String recurringDays, Integer occurrenceCount,
                                  LocalDate recurrenceEndDate, boolean autoDecline) {
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

  private List<Event> createRecurringEvents(String eventName, LocalDateTime startTime, LocalDateTime endTime,
                                            String description, String location, EventVisibility visibility,
                                            String recurringDays, Integer occurrenceCount, LocalDate recurrenceEndDate) {

    List<Event> recurringEvents = new ArrayList<>();
    Set<DayOfWeek> daysOfWeek = parseDaysOfWeek(recurringDays);
    Duration eventDuration = Duration.between(startTime, endTime);
    LocalDate startDate = startTime.toLocalDate();

    int occurrencesCreated = 0;
    while ((occurrenceCount == null || occurrencesCreated < occurrenceCount) &&
            (recurrenceEndDate == null || !startDate.isAfter(recurrenceEndDate))) {

      DayOfWeek currentDay = startDate.getDayOfWeek();

      if (daysOfWeek.contains(currentDay)) {
        LocalDateTime eventStartTime = LocalDateTime.of(
                startDate,
                startTime.toLocalTime()
        );

        LocalDateTime eventEndTime = eventStartTime.plus(eventDuration);

        Event event = createSingleEvent(
                eventName, eventStartTime, eventEndTime,
                description, location, visibility, recurringDays,
                occurrenceCount, recurrenceEndDate, true
        );

        recurringEvents.add(event);
        occurrencesCreated++;
      }

      startDate = startDate.plusDays(1);
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

  private List<IEvent> findEventsByName(String eventName) {
    return events.stream()
            .filter(event -> event.getName().equals(eventName))
            .collect(Collectors.toList());
  }

  private List<IEvent> findEventsByNameAndStartTime(String eventName, LocalDateTime startTime) {
    return events.stream()
            .filter(event -> event.getName().equals(eventName))
            .filter(event -> event.getStartDateTime().isAfter(startTime))
            .collect(Collectors.toList());
  }

  private List<IEvent> findEventsByStartAndEndTimeAndNameIfPresent(String eventName, LocalDateTime startTime, LocalDateTime endTime) {
    Stream<IEvent> filteredEvents = events.stream();

    if (eventName != null && !eventName.trim().isEmpty()) {
      filteredEvents = filteredEvents.filter(event -> event.getName().equals(eventName));
    }

    return filteredEvents
            .filter(event -> event.getStartDateTime().isEqual(startTime))
            .filter(event -> event.getEndDateTime().isEqual(endTime))
            .collect(Collectors.toList());
  }

  private IEvent updateEventProperty(Event event, String property, String value) {
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
        LocalDateTime startTime = parseDateTime(value);
        builder.startTime(startTime);
        if (event.isAutoDecline()) {
          Event tempEvent = builder.build();
          for (IEvent existingEvent: events) {
            if (existingEvent != event && tempEvent.conflictsWith(existingEvent)) {
              throw new EventConflictException("Event conflicts with existing event: " + existingEvent.getName());
            }
          }
        }
        break;

      case PROPERTY_END_TIME:
        LocalDateTime endTime = parseDateTime(value);
        builder.endTime(endTime);
        if (event.isAutoDecline()) {
          Event tempEvent = builder.build();
          for (IEvent existingEvent: events) {
            if (existingEvent != event && tempEvent.conflictsWith(existingEvent)) {
              throw new EventConflictException("Event conflicts with existing event: " + existingEvent.getName());
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
        if (VISIBILITY_PRIVATE.equalsIgnoreCase(value)) {
          builder.visibility(EventVisibility.PRIVATE);
        } else if (VISIBILITY_PUBLIC.equalsIgnoreCase(value)) {
          builder.visibility(EventVisibility.PUBLIC);
        } else {
          throw new IllegalArgumentException("Invalid visibility value: " + value);
        }
        break;

      // TODO: Add logic for changing recurring properties
      default:
        throw new IllegalArgumentException("Cannot edit property: " + property);
    }

    return builder.build();
  }

  private LocalDateTime parseDateTime(String value) {
    value = value.replace(" ", "T");
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    return LocalDateTime.parse(value, formatter);
  }

}
