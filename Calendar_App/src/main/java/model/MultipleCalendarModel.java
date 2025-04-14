package model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import calendar.ConflictException;
import events.Event;
import events.IEvent;
import events.IRecurringSequenceEvent;
import events.IRecurringUntilEvent;
import events.RecurringSequenceEvent;
import events.RecurringUntilEvent;

/**
 * A class representing a collection of multiple calendars with
 * methods that perform operations on the collection.
 */
public class MultipleCalendarModel implements IMultipleCalendarModel {

  protected Map<String, IExtendedZonedCalendarModel> calendars;
  private IExtendedZonedCalendarModel activeCalendar;

  /**
   * A constructor for a multiple calendar model which creates a map
   * of calendars and inserts a default calendar with the associated
   * timezone America/New_York.
   */
  public MultipleCalendarModel() {
    this.calendars = new HashMap<String, IExtendedZonedCalendarModel>(0);
    this.calendars.put(
        "default", new ExtendedZonedCalendarModel(ZoneId.systemDefault())
    );
    this.activeCalendar = this.calendars.get("default");
  }

  @Override
  public IExtendedZonedCalendarModel getActiveCalendar() {
    return activeCalendar;
  }

  @Override
  public Set<String> getCalendarKeys() {
    return this.calendars.keySet();
  }

  @Override
  public void createCalendar(
      String name,
      ZoneId timeZone
  ) {
    calendars.put(name, new ExtendedZonedCalendarModel(timeZone));
  }

  @Override
  public void editCalendar(
      String name,
      CalendarProperty property,
      String value
  ) {
    switch (property) {
      case NAME:
        IExtendedZonedCalendarModel calendarModel = calendars.get(name);
        calendars.put(value, calendarModel);
        calendars.remove(name);
        break;
      case TIMEZONE:
        ZoneId newZoneId = ZoneId.of(value);
        this.activeCalendar.updateCalendarZoneId(newZoneId);
        break;
      default:
        break;
    }
  }

  @Override
  public void useCalendar(
      String name
  ) {
    this.activeCalendar = this.calendars.get(name);
  }

  @Override
  public void copyEventWithName(
      String eventName,
      LocalDateTime startDateTime,
      String targetCalendarName,
      LocalDateTime targetDateTime
  ) throws ConflictException {
    IExtendedZonedCalendarModel targetCalendar = this.calendars.get(targetCalendarName);
    LocalDateTime adjustedDateTime = changeTimeZone(
        startDateTime, 
        activeCalendar.getZoneId(), 
        targetCalendar.getZoneId()
    );
    long dayOffset = Duration.between(adjustedDateTime, targetDateTime).toDays();
    long timeOffset = Duration.between(
        adjustedDateTime.toLocalTime(), 
        targetDateTime.toLocalTime()).toMinutes();
    List<IEvent> events = this.activeCalendar.queryDate(startDateTime.toLocalDate());
    List<IEvent> filteredEvents = events
        .stream()
        .filter(e -> e.getSubject().equals(eventName) 
        && e.getStartDateTime().equals(startDateTime))
        .collect(Collectors.toList());
    copyEvents(filteredEvents, targetCalendar, dayOffset, timeOffset);
  }

  @Override
  public void copyEventsOnDate(
      LocalDate date,
      String targetCalendarName,
      LocalDate targetDate
  ) throws ConflictException {
    List<IEvent> events = this.activeCalendar.queryDate(date);
    IExtendedZonedCalendarModel targetCalendar = this.calendars.get(targetCalendarName);
    long dayOffset = Duration.between(
        LocalDateTime.of(date, LocalTime.NOON), 
        LocalDateTime.of(targetDate,LocalTime.NOON)
    ).toDays();
    copyEvents(events, targetCalendar, dayOffset, 0);
  }

  @Override
  public void copyEventsBetween(
      LocalDate startDate,
      LocalDate endDate,
      String targetCalendarName,
      LocalDate targetDate
  ) throws ConflictException {
    IExtendedZonedCalendarModel targetCalendar = this.calendars.get(targetCalendarName);
    LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
    LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
    List<IEvent> events = activeCalendar.queryDateRange(startDateTime, endDateTime);
    long dayOffset = Duration.between(
        LocalDateTime.of(startDate, LocalTime.NOON), 
        LocalDateTime.of(targetDate,LocalTime.NOON)
    ).toDays();
    copyEvents(events, targetCalendar, dayOffset, 0);
  }

  private void copySingleEvent(
      IEvent event,
      IExtendedZonedCalendarModel targetCalendar,
      long dayOffset,
      long timeOffset
  ) throws ConflictException {
    LocalDateTime startDateTime = changeTimeZone(
        event.getStartDateTime(), 
        activeCalendar.getZoneId(), 
        targetCalendar.getZoneId()
    );
    LocalDateTime endDateTime = changeTimeZone(
        event.getEndDateTime(), 
        activeCalendar.getZoneId(), 
        targetCalendar.getZoneId()
    );
    IEvent copiedEvent = new Event
        .EventBuilder()
        .fromEvent(event)
        .startDateTime(startDateTime.plusDays(dayOffset).plusMinutes(timeOffset))
        .endDateTime(endDateTime.plusDays(dayOffset).plusMinutes(timeOffset))
        .build();
    targetCalendar.addEventAndCheckConflicts(copiedEvent);
  }

  private void copyRecurringSequenceEvent(
      IRecurringSequenceEvent event, 
      IExtendedZonedCalendarModel targetCalendar,
      long dayOffset,
      long timeOffset
  ) throws ConflictException {
    List<IRecurringSequenceEvent> thisAndFollowing = event.getThisAndFollowing();
    long dayShift = dayOffset % 7;
    List<DayOfWeek> shiftedWeekdays = new ArrayList<DayOfWeek>(0);
    for (DayOfWeek dayOfWeek: event.getWeekdays()) {
      shiftedWeekdays.add(dayOfWeek.plus(dayShift));
    }
    for (IRecurringSequenceEvent eventInSeries: thisAndFollowing) {
      LocalDateTime startDateTime = changeTimeZone(
          eventInSeries.getStartDateTime(), 
          activeCalendar.getZoneId(), 
          targetCalendar.getZoneId()
      );
      LocalDateTime endDateTime = changeTimeZone(
          eventInSeries.getEndDateTime(), 
          activeCalendar.getZoneId(), 
          targetCalendar.getZoneId()
      );
      IRecurringSequenceEvent copiedEvent = new RecurringSequenceEvent
          .RecurringSequenceEventBuilder()
          .fromEvent(event)
          .startDateTime(startDateTime.plusDays(dayOffset).plusMinutes(timeOffset))
          .endDateTime(endDateTime.plusDays(dayOffset).plusMinutes(timeOffset))
          .sequenceSize(thisAndFollowing.size())
          .weekdays(shiftedWeekdays)
          .build();
      targetCalendar.addEventAndCheckConflicts(copiedEvent);
      copiedEvent.setEventsInSeries(thisAndFollowing);
    }
  }

  private void copyRecurringUntilEvent(
      IRecurringUntilEvent event, 
      IExtendedZonedCalendarModel targetCalendar,
      long dayOffset,
      long timeOffset
  ) throws ConflictException {
    List<IRecurringUntilEvent> thisAndFollowing = event.getThisAndFollowing();
    long dayShift = dayOffset % 7;
    List<DayOfWeek> shiftedWeekdays = new ArrayList<DayOfWeek>(0);
    for (DayOfWeek dayOfWeek: event.getWeekdays()) {
      shiftedWeekdays.add(dayOfWeek.plus(dayShift));
    }
    for (IRecurringUntilEvent eventInSeries: thisAndFollowing) {
      LocalDateTime startDateTime = changeTimeZone(
          eventInSeries.getStartDateTime(), 
          activeCalendar.getZoneId(), 
          targetCalendar.getZoneId()
      );
      LocalDateTime endDateTime = changeTimeZone(
          eventInSeries.getEndDateTime(), 
          activeCalendar.getZoneId(), 
          targetCalendar.getZoneId()
      );
      IRecurringUntilEvent copiedEvent = new RecurringUntilEvent
          .RecurringUntilEventBuilder()
          .fromEvent(event)
          .startDateTime(startDateTime.plusDays(dayOffset).plusMinutes(timeOffset))
          .endDateTime(endDateTime.plusDays(dayOffset).plusMinutes(timeOffset))
          .untilDateTime(changeTimeZone(
              eventInSeries.getUntilDatetime(), 
              this.activeCalendar.getZoneId(), 
              targetCalendar.getZoneId())
          )
          .weekdays(shiftedWeekdays)
          .build();
      targetCalendar.addEventAndCheckConflicts(copiedEvent);
      copiedEvent.setEventsInSeries(thisAndFollowing);
    }
  }

  private LocalDateTime changeTimeZone(LocalDateTime dateTime, ZoneId from, ZoneId to) {
    ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, from);
    zonedDateTime = zonedDateTime.withZoneSameInstant(to);
    return zonedDateTime.toLocalDateTime();
  }

  private void copyEvents(
      List<IEvent> events, 
      IExtendedZonedCalendarModel targetCalendar, 
      long dayOffset, 
      long timeOffset
  ) throws ConflictException {
    Set<UUID> recurringIds = new HashSet<UUID>();
    for (IEvent event: events) {
      if (event instanceof RecurringUntilEvent) {
        RecurringUntilEvent eventCopy = (RecurringUntilEvent) event;
        if (! recurringIds.contains(eventCopy.getRecurringSequenceId())) {
          copyRecurringUntilEvent(eventCopy, targetCalendar, dayOffset, timeOffset);
        }
      }
      else if (event instanceof RecurringSequenceEvent) {
        RecurringSequenceEvent eventCopy = (RecurringSequenceEvent) event;
        if (! recurringIds.contains(eventCopy.getRecurringSequenceId())) {
          copyRecurringSequenceEvent(eventCopy, targetCalendar, dayOffset, timeOffset);
        }
      }
      else {
        copySingleEvent(event, targetCalendar, dayOffset, timeOffset);
      }
    }
  }

}
