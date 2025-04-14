package model;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import calendar.ConflictException;
import events.IEvent;

/**
 * An extended representation of a ZonedCalendarModel which allows for
 * interacting with events by id. This extension also allows setting multiple
 * event properties via creation or editing.
 */
public interface IExtendedZonedCalendarModel extends IZonedCalendarModel {

  /**
   * Gets an event by the given id.
   * @param id the id of the event.
   * @return the event with the given id.
   * @throws IOException if there is no event with this id.
   */
  public IEvent getById(UUID id) throws IOException;

  /**
   * Creates a single event with the given properties.
   * @param subject the subject for the event.
   * @param startDateTime the start date time for the event.
   * @param endDateTime the end date time for the event.
   * @param isAllDayEvent the is all day value for this event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate the isPrivate value for this event.
   * @throws ConflictException if the created event results in a conflict.
   */
  public void createSingle(
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate
  ) throws ConflictException;

  /**
   * Creates a recurring until event which recurs on specified
   * weekdays until the on date time.
   * @param subject the subject for the event.
   * @param startDateTime the start date time for the event.
   * @param endDateTime the end date time for the event.
   * @param isAllDayEvent the is all day value for this event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate the isPrivate value for this event.
   * @param weekdays the weekdays this event recurs on.
   * @param onDateTime the on date time when this series stops.
   * @param recurringSequenceId the id associated with this series.
   * @throws ConflictException if an event in the series results in a conflict.
   */
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
  ) throws ConflictException;

  /**
   * Creates a recurring until event which recurs on specified
   * weekdays for n times.
   * @param subject the subject for the event.
   * @param startDateTime the start date time for the event.
   * @param endDateTime the end date time for the event.
   * @param isAllDayEvent the is all day value for this event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate the isPrivate value for this event.
   * @param weekdays the weekdays this event recurs on.
   * @param sequenceSize the number of occurrences of this event.
   * @param recurringSequenceId the id associated with this series.
   * @throws ConflictException if an event in the series results in a conflict.
   */
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
  ) throws ConflictException;


  /**
   * Edits a single event with the given id, applying
   * the given properties.
   * @param id the id of the event to edit.
   * @param subject the subject for the event.
   * @param startDateTime the start date time for the event.
   * @param endDateTime the end date time for the event.
   * @param isAllDayEvent the is all day value for this event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate the isPrivate value for this event.
   * @throws ConflictException if the updated event results in a conflict.
   */
  public void editSingleById(
      UUID id, 
      String subject,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Boolean isAllDayEvent,
      String description,
      String location,
      Boolean isPrivate
  ) throws ConflictException, IOException;

  /**
   * Edits a recurring until event with the given id, applying
   * the given properties.
   * @param id the id of the event to edit.
   * @param subject the subject for the event.
   * @param startDateTime the start date time for the event.
   * @param endDateTime the end date time for the event.
   * @param isAllDayEvent the is all day value for this event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate the isPrivate value for this event.
   * @param weekdays the weekdays this event recurs on.
   * @param onDateTime the on date time when this series stops.
   * @throws ConflictException if a conflict results from this edit.
   * @throws IOException if there is no event with this id.
   */
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
      LocalDateTime onDateTime
  ) throws ConflictException, IOException;

  /**
   * Edits a single recurring sequence event by id, applying
   * the given properties.
   * @param subject the subject for the event.
   * @param startDateTime the start date time for the event.
   * @param endDateTime the end date time for the event.
   * @param isAllDayEvent the is all day value for this event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate the isPrivate value for this event.
   * @param weekdays the weekdays this event recurs on.
   * @param sequenceSize the number of occurrences of this event.
   * @throws ConflictException if an event in the series results in a conflict.
   * @throws IOException if there is no event with this id.
   */
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
  ) throws ConflictException, IOException;

  /**
   * Edits this recurring sequence event and all events
   * that follow it in the series, applying the given
   * properties.
   * @param subject the subject for the event.
   * @param startDateTime the start date time for the event.
   * @param endDateTime the end date time for the event.
   * @param isAllDayEvent the is all day value for this event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate the isPrivate value for this event.
   * @param weekdays the weekdays this event recurs on.
   * @param sequenceSize the number of occurrences of this event.
   * @throws ConflictException if an event in the series results in a conflict.
   * @throws IOException if there is no event with this id.
   */
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
  ) throws ConflictException, IOException;

  /**
   * Edits this recurring until event and and all events
   * that follow it in the series, applying the given
   * properties.
   * @param id the id of the event to edit.
   * @param subject the subject for the event.
   * @param startDateTime the start date time for the event.
   * @param endDateTime the end date time for the event.
   * @param isAllDayEvent the is all day value for this event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate the isPrivate value for this event.
   * @param weekdays the weekdays this event recurs on.
   * @param onDateTime the on date time when this series stops.
   * @throws ConflictException if a conflict results from this edit.
   * @throws IOException if there is no event with this id.
   */
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
      LocalDateTime onDateTime
  ) throws ConflictException, IOException;

  /**
   * Edits all recurring until events in this recurring series, identified
   * by the id of one event in the series, applying the given properties.
   * @param id the id of the event to edit.
   * @param subject the subject for the event.
   * @param startDateTime the start date time for the event.
   * @param endDateTime the end date time for the event.
   * @param isAllDayEvent the is all day value for this event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate the isPrivate value for this event.
   * @param weekdays the weekdays this event recurs on.
   * @param onDateTime the on date time when this series stops.
   * @throws ConflictException if a conflict results from this edit.
   * @throws IOException if there is no event with this id.
   */
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
      LocalDateTime onDateTime
  ) throws ConflictException, IOException;

  /**
   * Edits all recurring sequence events in this recurring series, identified
   * by the id of one event in the series, applying the given properties.
   * @param subject the subject for the event.
   * @param startDateTime the start date time for the event.
   * @param endDateTime the end date time for the event.
   * @param isAllDayEvent the is all day value for this event.
   * @param description the description for this event.
   * @param location the location for this event.
   * @param isPrivate the isPrivate value for this event.
   * @param weekdays the weekdays this event recurs on.
   * @param sequenceSize the number of occurrences of this event.
   * @throws ConflictException if an event in the series results in a conflict.
   * @throws IOException if there is no event with this id.
   */
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
  ) throws ConflictException, IOException;

}
