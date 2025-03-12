package calendarapp.model.impl;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.IEvent;
import calendarapp.utils.TimeUtil;

import static calendarapp.model.impl.CalendarExporter.exportEventAsGoogleCalendarCsv;
import static calendarapp.utils.TimeUtil.isFirstBeforeSecond;

public class CalendarModel implements ICalendarModel {
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

    if (!isRecurring) {
      IEvent event = createSingleEvent(eventName, startTime, endTime, description, location,
          visibility, recurringDays, occurrence, recurrenceEndDate, autoDecline);

      if (autoDecline) {
        for (IEvent existingEvent : events) {
          if (event.conflictsWith(existingEvent)) {
            throw new EventConflictException("Event conflicts with existing event: " + existingEvent);
          }
        }
      }
      events.add(event);
    } else {
      List<Event> recurringEvents = createRecurringEvents(
          eventName, startTime, endTime, description, location, visibility,
          recurringDays, occurrence, recurrenceEndDate);

      for (Event newEvent : recurringEvents) {
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
            throw new EventConflictException("Event conflicts with existing event: " + existingEvent);
          }
        }
      }

      updatedEvents.add(updatedEvent);
    }

    events.removeAll(eventsToEdit);
    events.addAll(updatedEvents);
  }

  // TODO: Fix multi-day event print
  @Override
  public List<IEvent> printEvents(Temporal startDateTime, Temporal endDateTime) {
    if (endDateTime == null) {
      endDateTime = (TimeUtil.getLocalDateTimeFromTemporal(startDateTime)
          .toLocalDate().plusDays(1).atStartOfDay());
    }

    Temporal finalEndDateTime = endDateTime;
    return events.stream()
        .filter(event -> event.hasIntersectionWith(startDateTime, finalEndDateTime))
        .collect(Collectors.toList());
  }

  // TODO: Return the absolute path
  @Override
  public void export(String filename) throws IOException {
    String filePath = filename + ".csv";
    exportEventAsGoogleCalendarCsv(events, filePath);
  }

  @Override
  public String showStatus(Temporal dateTime) {
    boolean isBusy = events.stream().anyMatch(event -> event.isActiveAt(dateTime));
    if (isBusy) {
      return EventConstants.Status.BUSY;
    } else {
      return EventConstants.Status.AVAILABLE;
    }
  }

  private Event createSingleEvent(String eventName, Temporal startTime, Temporal endTime,
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

  private List<Event> createRecurringEvents(String eventName, Temporal startTime, Temporal endTime,
                                            String description, String location,
                                            String visibility, String recurringDays,
                                            Integer occurrenceCount, Temporal recurrenceEndDate) {

    List<Event> recurringEvents = new ArrayList<>();
    Set<DayOfWeek> daysOfWeek = parseDaysOfWeek(recurringDays);
    if (endTime == null) {
      endTime = startTime.plus(1, ChronoUnit.DAYS);
    }
    Duration eventDuration = Duration.between(startTime, endTime);

    int occurrencesCreated = 0;
    Temporal currentStartTime = startTime;

    while ((occurrenceCount != null && occurrencesCreated < occurrenceCount) ||
        (recurrenceEndDate != null && isFirstBeforeSecond(currentStartTime, recurrenceEndDate))) {

      DayOfWeek currentDay = DayOfWeek.of(currentStartTime.get(ChronoField.DAY_OF_WEEK));

      if (daysOfWeek.contains(currentDay)) {
        Temporal eventEndTime = currentStartTime.plus(eventDuration);

        Event event = createSingleEvent(
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

  private Set<DayOfWeek> parseDaysOfWeek(String daysString) {
    Set<DayOfWeek> days = new HashSet<>();

    // TODO: Convert this to map
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
        .filter(event -> event.matchesName(eventName))
        .filter(event -> event.isWithinTimeRange(startTime, endTime))
        .collect(Collectors.toList());
  }
}