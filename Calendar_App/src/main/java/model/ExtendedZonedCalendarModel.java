package model;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

/**
 * An extended representation of a ZonedCalendarModel which allows for
 * interacting with events by id. This extension also allows setting multiple
 * event properties via creation or editing.
 */
public class ExtendedZonedCalendarModel extends ZonedCalendarModel 
    implements IExtendedZonedCalendarModel {

  /**
   * A constructor for an ExtendedZonedCalendarModel
   * instance which simply calls the constructor
   * of the ZonedCalendarModel with the given id.
   * @param zoneId the zoneId to associate with this model.
   */
  public ExtendedZonedCalendarModel(ZoneId zoneId) {
    super(zoneId);
  }

  @Override
  public void createSingle(
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate
  ) throws ConflictException {
    IEvent event = new Event
        .EventBuilder()
        .setId(UUID.randomUUID())
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .isAllDayEvent(isAllDayEvent)
        .description(description)
        .location(location)
        .isPrivate(isPrivate)
        .build();
    this.addEventAndCheckConflicts(event);
  }

  @Override
  public void createRecurringUntil(
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      LocalDateTime onDateTime,
      UUID recurringSequenceId
  ) throws ConflictException {
    List<IRecurringUntilEvent> eventsInSeries = new ArrayList<>(0);
    try {
      while (! endDateTime.toLocalDate().isAfter(onDateTime.toLocalDate())) {
        if (weekdays.contains(startDateTime.getDayOfWeek())) {
          IRecurringUntilEvent event = new RecurringUntilEvent
              .RecurringUntilEventBuilder()
              .setId(UUID.randomUUID())
              .subject(subject)
              .startDateTime(startDateTime)
              .endDateTime(endDateTime)
              .isAllDayEvent(isAllDayEvent)
              .description(description)
              .location(location)
              .isPrivate(isPrivate)
              .weekdays(weekdays)
              .untilDateTime(onDateTime)
              .build();
          addEventAndCheckConflicts(event);
          eventsInSeries.add(event);
        }
        startDateTime = startDateTime.plusDays(1);
        endDateTime = endDateTime.plusDays(1);
      }
      for (IRecurringUntilEvent event: eventsInSeries) {
        event.setRecurringSequenceId(recurringSequenceId);
        event.setEventsInSeries(eventsInSeries);
      }
    }
    catch (ConflictException ex) {
      for (IRecurringUntilEvent event: eventsInSeries) {
        this.events.remove(event);
      }
      throw ex;
    }
  }

  @Override
  public void createRecurringSequence(
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      Integer sequenceSize,
      UUID recurringSequenceId
  ) throws ConflictException {
    int index = 1;
    List<IRecurringSequenceEvent> eventsInSeries = new ArrayList<IRecurringSequenceEvent>(0);
    try {
      while (index <= sequenceSize) {
        if (weekdays.contains(startDateTime.getDayOfWeek())) {
          IRecurringSequenceEvent event = new RecurringSequenceEvent
              .RecurringSequenceEventBuilder()
              .setId(UUID.randomUUID())
              .subject(subject)
              .startDateTime(startDateTime)
              .endDateTime(endDateTime)
              .isAllDayEvent(isAllDayEvent)
              .description(description)
              .location(location)
              .isPrivate(isPrivate)
              .weekdays(weekdays)
              .sequenceSize(sequenceSize)
              .build();
          addEventAndCheckConflicts(event);
          eventsInSeries.add(event);
          index += 1;
        }
        startDateTime = startDateTime.plusDays(1);
        endDateTime = endDateTime.plusDays(1);
      }
      for (IRecurringSequenceEvent event: eventsInSeries) {
        event.setRecurringSequenceId(recurringSequenceId);
        event.setEventsInSeries(eventsInSeries);
      }
    }
    catch (ConflictException ex) {
      for (IRecurringSequenceEvent event: eventsInSeries) {
        this.events.remove(event);
      }
      throw ex;
    }
  }

  @Override
  public IEvent getById(UUID id) throws IOException {
    List<IEvent> foundEvents = events.stream()
        .filter(event -> event.getId().equals(id))
        .collect(Collectors.toList());
    if (foundEvents.isEmpty()) {
      throw new IOException();
    }
    else {
      return foundEvents.get(0);
    }
  }

  @Override
  public void editSingleById(
      UUID id, 
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate
  ) throws ConflictException, IOException {
    IEvent foundEvent = getById(id);
    IEvent updatedEvent = new Event
        .EventBuilder()
        .setId(foundEvent.getId())
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .isAllDayEvent(isAllDayEvent)
        .description(description)
        .location(location)
        .isPrivate(isPrivate)
        .build();
    try {
      this.events.remove(foundEvent);
      addEventAndCheckConflicts(updatedEvent);
    }
    catch (Exception ex) {
      this.events.add(foundEvent);
      throw ex;
    }
  }

  @Override
  public void editSingleRecurringUntilById(
      UUID id, 
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      LocalDateTime untilDateTime
  ) throws ConflictException, IOException {
    IRecurringUntilEvent updatedEvent = new RecurringUntilEvent
        .RecurringUntilEventBuilder()
        .setId(id)
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .isAllDayEvent(isAllDayEvent)
        .description(description)
        .location(location)
        .isPrivate(isPrivate)
        .weekdays(weekdays)
        .untilDateTime(untilDateTime)
        .build();
    IRecurringUntilEvent foundEvent = (IRecurringUntilEvent) getById(id);
    updatedEvent.setEventsInSeries(foundEvent.getEventsInSeries());
    updatedEvent.setRecurringSequenceId(foundEvent.getRecurringSequenceId());
    try {
      this.events.remove(foundEvent);
      addEventAndCheckConflicts(updatedEvent);
    }
    catch (Exception ex) {
      this.events.add(foundEvent);
      throw ex;
    }
  }

  @Override
  public void editSingleRecurringSequenceById(
      UUID id, 
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      Integer sequenceSize
  ) throws ConflictException, IOException {
    IRecurringSequenceEvent updatedEvent = new RecurringSequenceEvent
        .RecurringSequenceEventBuilder()
        .setId(id)
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .isAllDayEvent(isAllDayEvent)
        .description(description)
        .location(location)
        .isPrivate(isPrivate)
        .weekdays(weekdays)
        .sequenceSize(sequenceSize)
        .build();
    IRecurringSequenceEvent foundEvent = (IRecurringSequenceEvent) getById(id);
    updatedEvent.setEventsInSeries(foundEvent.getEventsInSeries());
    updatedEvent.setRecurringSequenceId(foundEvent.getRecurringSequenceId());
    try {
      this.events.remove(foundEvent);
      addEventAndCheckConflicts(updatedEvent);
    }
    catch (Exception ex) {
      this.events.add(foundEvent);
      throw ex;
    }
  }

  @Override
  public void editThisAndFollowingUntilById(
      UUID id, 
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      LocalDateTime untilDateTime
  ) throws ConflictException, IOException {
    IEvent foundEvent = getById(id);
    IRecurringUntilEvent casted = (IRecurringUntilEvent) foundEvent;
    UUID recurringSequenceId = casted.getRecurringSequenceId();
    List<IRecurringUntilEvent> thisAndFollowing = casted.getThisAndFollowing();
    try {
      for (IRecurringUntilEvent event: thisAndFollowing) {
        this.events.remove(event);
      }
      createRecurringUntil(
          subject, 
          startDateTime, 
          endDateTime, 
          isAllDayEvent, 
          description, 
          location, 
          isPrivate, 
          weekdays, 
          untilDateTime,
          recurringSequenceId
      );
    }
    catch (Exception ex) {
      for (IRecurringUntilEvent event: thisAndFollowing) {
        this.events.add(event);
        throw ex;
      }
    }
  }

  @Override
  public void editAllUntilById(
      UUID id, 
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      LocalDateTime untilDateTime
  ) throws ConflictException, IOException {
    IEvent foundEvent = getById(id);
    IRecurringUntilEvent casted = (IRecurringUntilEvent) foundEvent;
    UUID recurringSequenceId = casted.getRecurringSequenceId();
    List<IRecurringUntilEvent> thisAndFollowing = casted.getEventsInSeries();
    try {
      for (IRecurringUntilEvent event: thisAndFollowing) {
        this.events.remove(event);
      }
      createRecurringUntil(
          subject, 
          startDateTime, 
          endDateTime, 
          isAllDayEvent, 
          description, 
          location, 
          isPrivate, 
          weekdays, 
          untilDateTime,
          recurringSequenceId
      );
    }
    catch (Exception ex) {
      for (IRecurringUntilEvent event: thisAndFollowing) {
        this.events.add(event);
        throw ex;
      }
    }
  }

  @Override
  public void editThisAndFollowingSequenceById(
      UUID id, 
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      Integer sequenceSize
  ) throws ConflictException, IOException {
    IEvent foundEvent = getById(id);
    IRecurringSequenceEvent casted = (IRecurringSequenceEvent) foundEvent;
    UUID recurringSequenceId = casted.getRecurringSequenceId();
    List<IRecurringSequenceEvent> thisAndFollowing = casted.getThisAndFollowing();
    try {
      for (IRecurringSequenceEvent event: thisAndFollowing) {
        this.events.remove(event);
      }
      createRecurringSequence(
          subject, 
          startDateTime, 
          endDateTime, 
          isAllDayEvent, 
          description, 
          location, 
          isPrivate, 
          weekdays, 
          sequenceSize,
          recurringSequenceId
      );
    }
    catch (Exception ex) {
      for (IRecurringSequenceEvent event: thisAndFollowing) {
        this.events.add(event);
        throw ex;
      }
    }
  }

  @Override
  public void editAllSequenceById(
      UUID id, 
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate,
      List<DayOfWeek> weekdays,
      Integer sequenceSize
  ) throws ConflictException, IOException {
    IEvent foundEvent = getById(id);
    IRecurringSequenceEvent casted = (IRecurringSequenceEvent) foundEvent;
    UUID recurringSequenceId = casted.getRecurringSequenceId();
    List<IRecurringSequenceEvent> thisAndFollowing = casted.getEventsInSeries();
    try {
      for (IRecurringSequenceEvent event: thisAndFollowing) {
        this.events.remove(event);
      }
      createRecurringSequence(
          subject, 
          startDateTime, 
          endDateTime, 
          isAllDayEvent, 
          description, 
          location, 
          isPrivate, 
          weekdays, 
          sequenceSize,
          recurringSequenceId
      );
    }
    catch (Exception ex) {
      for (IRecurringSequenceEvent event: thisAndFollowing) {
        this.events.add(event);
        throw ex;
      }
    }
  }

}
