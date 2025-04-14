package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import calendar.ConflictException;
import events.Event;
import events.IEvent;
import events.IRecurringSequenceEvent;
import events.IRecurringUntilEvent;
import events.RecurringSequenceEvent;
import events.RecurringUntilEvent;
import events.SingleEventProperties;

/**
 * Implementation of the ICalendar interface which allows
 * performing operations on a list of Events. These operations
 * include creating, editing, querying, printing, exporting
 * to an external file, and showing status at a given dateTime.
 */
public class CalendarModel implements ICalendarModel {

  protected List<IEvent> events;

  /**
   * A constructor for the calendar model which initializes
   * an empty list of calendar events.
   */
  public CalendarModel() {
    this.events = new ArrayList<IEvent>(0);
  }

  @Override
  public void createSingleEvent(
      String eventName,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      boolean autoDecline
  ) throws ConflictException {
    IEvent event = new Event.EventBuilder()
        .setId(UUID.randomUUID())
        .subject(eventName)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .build();
    if (autoDecline) {
      checkConflict(event);
    }
    this.events.add(event);
  }
  
  @Override
  public void createNRecurringEvents(
      String eventName,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      List<DayOfWeek> weekdays,
      int n,
      boolean autoDecline
  ) throws ConflictException {
    int index = 1;
    UUID recurringSeriesId = UUID.randomUUID();
    List<IRecurringSequenceEvent> eventsInSeries = new ArrayList<IRecurringSequenceEvent>(0);
    try {
      while (index <= n) {
        if (weekdays.contains(startDateTime.getDayOfWeek())) {
          IRecurringSequenceEvent event = 
              new RecurringSequenceEvent
              .RecurringSequenceEventBuilder()
              .setId(UUID.randomUUID())
              .subject(eventName)
              .startDateTime(startDateTime)
              .endDateTime(endDateTime)
              .weekdays(weekdays)
              .sequenceSize(n)
              .build();
          checkConflict(event);
          event.setRecurringSequenceId(recurringSeriesId);
          eventsInSeries.add(event);
          events.add(event);
          index += 1;
        }
        startDateTime = startDateTime.plusDays(1);
        endDateTime = endDateTime.plusDays(1);
      }
      for (IRecurringSequenceEvent event: eventsInSeries) {
        event.setEventsInSeries(eventsInSeries);
      }
    }
    catch (ConflictException ex) {
      for (IRecurringSequenceEvent event: eventsInSeries) {
        events.remove(event);
      }
      throw ex;
    }
  }

  @Override
  public void createRecurringEventsUntil(
      String eventName,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      List<DayOfWeek> weekdays,
      LocalDateTime untilDateTime,
      boolean autoDecline
  ) throws ConflictException {
    UUID recurringSeriesId = UUID.randomUUID();
    List<IRecurringUntilEvent> eventsInSeries = new ArrayList<IRecurringUntilEvent>(0);
    try {
      while (! endDateTime.isAfter(untilDateTime)) {
        if (weekdays.contains(startDateTime.getDayOfWeek())) {
          IRecurringUntilEvent event = 
              new RecurringUntilEvent
              .RecurringUntilEventBuilder()
              .setId(UUID.randomUUID())
              .subject(eventName)
              .startDateTime(startDateTime)
              .endDateTime(endDateTime)
              .weekdays(weekdays)
              .untilDateTime(untilDateTime)
              .build();
          checkConflict(event);
          event.setRecurringSequenceId(recurringSeriesId);
          eventsInSeries.add(event);
          events.add(event);
        }
        startDateTime = startDateTime.plusDays(1);
        endDateTime = endDateTime.plusDays(1);
      }
      for (IRecurringUntilEvent event: eventsInSeries) {
        event.setEventsInSeries(eventsInSeries);
      }
    }
    catch (ConflictException ex) {
      for (IRecurringUntilEvent event: eventsInSeries) {
        events.remove(event);
      }
      throw ex;
    }
  }

  @Override
  public void createSingleAllDayEvent(
      String eventName,
      LocalDate date,
      boolean autoDecline
  ) throws ConflictException {
    LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MIN);
    LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);
    IEvent event = new Event.EventBuilder()
        .setId(UUID.randomUUID())
        .subject(eventName)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .isAllDayEvent(true)
        .build();
    if (autoDecline) {
      checkConflict(event);
    }
    this.events.add(event);
  }

  @Override
  public void createNRecurringAllDayEvents(
      String eventName,
      LocalDate startDate,
      List<DayOfWeek> weekdays,
      int n
  ) throws ConflictException {
    int sequenceSize = weekdays.size() * n;
    int index = 1;
    UUID recurringSeriesId = UUID.randomUUID();
    List<IRecurringSequenceEvent> eventsInSeries = new ArrayList<IRecurringSequenceEvent>(0);
    try {
      while (index <= sequenceSize) {
        if (weekdays.contains(startDate.getDayOfWeek())) {
          LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
          LocalDateTime endDateTime = LocalDateTime.of(startDate, LocalTime.MAX);
          IRecurringSequenceEvent event = 
              new RecurringSequenceEvent
              .RecurringSequenceEventBuilder()
              .setId(UUID.randomUUID())
              .subject(eventName)
              .startDateTime(startDateTime)
              .endDateTime(endDateTime)
              .weekdays(weekdays)
              .sequenceSize(sequenceSize)
              .isAllDayEvent(true)
              .build();
          checkConflict(event);
          event.setRecurringSequenceId(recurringSeriesId);
          eventsInSeries.add(event);
          events.add(event);
          index += 1;
        }
        startDate = startDate.plusDays(1);
      }
      for (IRecurringSequenceEvent event: eventsInSeries) {
        event.setEventsInSeries(eventsInSeries);
      }
    }
    catch (ConflictException ex) {
      for (IRecurringSequenceEvent event: eventsInSeries) {
        events.remove(event);
      }
      throw ex;
    }
  }

  @Override
  public void createRecurringAllDayEventsUntil(
      String eventName,
      LocalDate startDate,
      List<DayOfWeek> weekdays,
      LocalDate endDate
  ) throws ConflictException {
    LocalDateTime untilDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
    UUID recurringSeriesId = UUID.randomUUID();
    List<IRecurringUntilEvent> eventsInSeries = new ArrayList<IRecurringUntilEvent>(0);
    try {
      while (!startDate.isAfter(endDate)) {
        if (weekdays.contains(startDate.getDayOfWeek())) {
          LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
          LocalDateTime endDateTime = LocalDateTime.of(startDate, LocalTime.MAX);
          IRecurringUntilEvent event = 
              new RecurringUntilEvent
              .RecurringUntilEventBuilder()
              .setId(UUID.randomUUID())
              .subject(eventName)
              .startDateTime(startDateTime)
              .endDateTime(endDateTime)
              .weekdays(weekdays)
              .untilDateTime(untilDateTime)
              .isAllDayEvent(true)
              .build();
          checkConflict(event);
          event.setRecurringSequenceId(recurringSeriesId);
          eventsInSeries.add(event);
          events.add(event);
        }
        startDate = startDate.plusDays(1);
      }
      for (IRecurringUntilEvent event: eventsInSeries) {
        event.setEventsInSeries(eventsInSeries);
      }
    }
    catch (ConflictException ex) {
      for (IRecurringUntilEvent event: eventsInSeries) {
        events.remove(event);
      }
      throw ex;
    }
  }
  
  @Override
  public List<IEvent> queryDate(LocalDate date) {
    List<IEvent> result = new ArrayList<IEvent>(0);
    for (IEvent event: events) {
      if (event.getStartDate().isEqual(date)) {
        result.add(event);
      }
    }
    return result;
  }

  @Override
  public List<IEvent> queryDateRange(LocalDateTime startDateTime, 
      LocalDateTime endDateTime) {
    List<IEvent> result = new ArrayList<IEvent>(0);
    for (IEvent event: events) {
      if ((event.getStartDateTime().isAfter(startDateTime) 
          || event.getStartDateTime().isEqual(startDateTime))
          && (event.getEndDateTime().isBefore(endDateTime) 
          || event.getEndDateTime().isEqual(endDateTime))) {
        result.add(event);
      }
    }
    return result;
  }

  @Override
  public void editSingle(
      SingleEventProperties property, 
      String eventName, 
      LocalDateTime startDateTime, 
      LocalDateTime endDateTime, 
      String newPropertyValue
  ) throws ConflictException {
    List<IEvent> namedEvent = events.stream()
        .filter(event -> event.getSubject().equals(eventName)
        && event.getStartDateTime().isEqual(startDateTime)
        && event.getEndDateTime().isEqual(endDateTime))
        .collect(Collectors.toList());
    for (IEvent event : namedEvent) {
      IEvent updatedEvent = new Event.EventBuilder()
          .fromEvent(event)
          .setPropertyWithName(property, newPropertyValue)
          .build();
      try {
        events.remove(event);
        checkConflict(updatedEvent);
        events.add(updatedEvent);
      }
      catch (ConflictException ex) {
        events.add(event);
        throw ex;
      }
    }
  }

  @Override
  public void editFromDate(
      SingleEventProperties property, 
      String eventName, 
      LocalDateTime startDateTime, 
      String newPropertyValue
  ) throws ConflictException {
    List<IEvent> namedEvent = events.stream()
                              .filter(event ->
                              event.getSubject().equals(eventName)
                              && (event.getStartDateTime()
                              .isAfter(startDateTime))
                              || event.getStartDateTime()
                              .isEqual(startDateTime))
                              .collect(Collectors.toList());
    for (IEvent event : namedEvent) {
      IEvent updatedEvent = new Event.EventBuilder()
          .fromEvent(event)
          .setPropertyWithName(property, newPropertyValue)
          .build();
      try {
        events.remove(event);
        checkConflict(updatedEvent);
        events.add(updatedEvent);
      }
      catch (ConflictException ex) {
        events.add(event);
        throw ex;
      }
    }
  }

  @Override
  public void editAll(SingleEventProperties property, String eventName, String newPropertyValue) 
      throws ConflictException {
    List<IEvent> namedEvent = events
                              .stream()
                              .filter(event -> event.getSubject()
                              .equals(eventName))
                              .collect(Collectors.toList());
    for (IEvent event : namedEvent) {
      IEvent updatedEvent = new Event.EventBuilder()
          .fromEvent(event)
          .setPropertyWithName(property, newPropertyValue)
          .build();
      try {
        events.remove(event);
        checkConflict(updatedEvent);
        events.add(updatedEvent);
      }
      catch (ConflictException ex) {
        events.add(event);
        throw ex;
      }
    }
  }

  @Override
  public String printEventsOnDate(LocalDate date) {
    List<IEvent> relevantEvents = queryDate(date);
    String result = "";
    for (IEvent event: relevantEvents) {
      if (event.getStartDate().isEqual(date)) {
        result += "• " + event.getSubject() 
                + " " + event.getStartTime().toString() + "-"
                + event.getEndTime().toString();
        if (event.getDescription() != null) {
          result += ". Location: " + event.getLocation();
        }
        result += "\n";
      }
    }
    return result;
  }

  @Override
  public String printEventsFromTo(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    String result = "";
    List<IEvent> relevantEvents = queryDateRange(startDateTime, endDateTime);
    for (IEvent event: relevantEvents) {
      result += "• " + event.getSubject() 
              + " " + event.getStartDateTime().toString() + "-"
              + event.getEndDateTime().toString();
      if (event.getDescription() != null) {
        result += ". Description: " + event.getDescription();
      }
      result += "\n";
    }
    return result;
  }

  @Override
  public String show(LocalDateTime dateTime) throws DateTimeParseException {
    IEvent event = new Event.EventBuilder()
        .subject("checkStatus")
        .startDateTime(dateTime)
        .endDateTime(dateTime)
        .build();
    try {
      checkConflict(event);
      return "Available\n";
    }
    catch (ConflictException ex) {
      return "Busy\n";
    }
  }

  protected void checkConflict(IEvent event) throws ConflictException {
    for (IEvent e: this.events) {
      if (event.getStartDate().isEqual(e.getStartDate())) {
        if ((event.isAllDayEvent() != null 
            && event.isAllDayEvent()) 
            || (e.isAllDayEvent() != null && e.isAllDayEvent())) {
          throw new ConflictException(
            "This event conflicts with all day event: "
            + e.getSubject() 
            + " on: " 
            + e.getStartDate().toString()
          );
        }
        if ((event.getStartTime().isBefore(e.getStartTime()))
            && event.getEndTime().isAfter(e.getStartTime())
            || (e.getStartTime().isBefore(event.getStartTime())
            && e.getEndTime().isAfter(event.getStartTime()))
            || (e.getStartTime().equals(event.getStartTime())
            && e.getEndTime().equals(event.getEndTime()))) {
          throw new ConflictException(
            "This event conflicts with event: "
            + e.getSubject() 
            + " on: " 
            + e.getStartDate().toString() 
            + " at: "
            + e.getStartTime().toString()
          );
        }
      }
    }
  }
}
