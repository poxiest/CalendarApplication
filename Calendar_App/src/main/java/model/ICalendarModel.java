package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import calendar.ConflictException;
import events.IEvent;
import events.SingleEventProperties;

/**
 * This interface represents a calendar model which supports
 * various operations on a collection of calendar events.
 */
public interface ICalendarModel {

  /**
   * Creates a single event in the calendar with a startDateTime,
   * an endDateTime, and a subject.
   * @param eventName the name of the event.
   * @param startDateTime the start date time of the event.
   * @param endDateTime the end date time of the event.
   * @param autoDecline true if declining conflicts, false otherwise.
   * @throws ConflictException if there is a conflict the a created event.
   */
  void createSingleEvent(
      String eventName,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      boolean autoDecline
  ) throws ConflictException;
  
  /**
   * Creates a recurring event that repeats N times on specific weekdays.
   * Weekdays are denoted {M,T,W,R,F,S,U} for Monday through Sunday.
   * @param eventName the name of the event.
   * @param startDateTime the start date time of the event.
   * @param endDateTime the end date time of the event.
   * @param weekdays the weekdays to include.
   * @param n the number of weeks to repeat this schedule.
   * @param autoDecline true if declining conflicts, false otherwise.
   * @throws ConflictException if there is a conflict the a created event.
   */
  void createNRecurringEvents(
      String eventName,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      List<DayOfWeek> weekdays,
      int n,
      boolean autoDecline
  ) throws ConflictException;

  /**
   * Creates a recurring event until a specific date.
   * Weekdays are denoted {M,T,W,R,F,S,U} for Monday through Sunday.
   * @param eventName the name of the event.
   * @param startDateTime the start date time of the event.
   * @param endDateTime the end date time of the event.
   * @param weekdays the weekdays to include.
   * @param untilDateTime the date to stop scheduling this event.
   * @param autoDecline true if declining conflicts, false otherwise.
   * @throws ConflictException if there is a conflict the a created event.
   */
  void createRecurringEventsUntil(
      String eventName,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      List<DayOfWeek> weekdays,
      LocalDateTime untilDateTime,
      boolean autoDecline
  ) throws ConflictException;

  /**
   * Creates a single all day event.
   * @param eventName the name of the event.
   * @param date the date to schedule this event.
   * @param autoDecline true if declining conflicts, false otherwise.
   * @throws ConflictException if there is a conflict with the created event.
   */
  void createSingleAllDayEvent(
      String eventName,
      LocalDate date,
      boolean autoDecline
  ) throws ConflictException;

  /**
   * Creates a recurring all day event that repeats N times
   * on specific weekdays.
   * Weekdays are denoted {M,T,W,R,F,S,U} for Monday through Sunday.
   * @param eventName the name of the event.
   * @param startDate the date to start scheduling this event.
   * @param weekdays the weekdays to include.
   * @param n the number of weeks to repeat this schedule.
   * @throws ConflictException if there is a conflict with the created event.
   */
  void createNRecurringAllDayEvents(
      String eventName,
      LocalDate startDate,
      List<DayOfWeek> weekdays,
      int n
  ) throws ConflictException;

  /**
   * Creates a recurring all day event until a specific date (inclusive).
   * Weekdays are denoted {M,T,W,R,F,S,U} for Monday through Sunday.
   * @param eventName the name of the event
   * @param startDate the date to start scheduling this event.
   * @param weekdays the weekdays to include.
   * @param endDate the date to stop scheduling this event (inclusive).
   * @throws ConflictException if there is a conflict with the created event.
   */
  void createRecurringAllDayEventsUntil(
      String eventName,
      LocalDate startDate,
      List<DayOfWeek> weekdays,
      LocalDate endDate
  ) throws ConflictException;

  /**
   * Returns the events in this calendar on the specified date.
   * @param date the date to query for events.
   * @return the list of events on the provided date.
   */
  List<IEvent> queryDate(LocalDate date);

  /**
   * Returns the events in this calendar on the specified date range.
   * @param startDateTime the start LocalDateTime to query for events.
   * @param endDateTime the end LocalDateTime to query for events.
   * @return the list of events in the provided date range.
   */
  List<IEvent> queryDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Changes the property of the given event.
   * @param property the property to change.
   * @param eventName the name of the event.
   * @param startDateTime the start date time of the event.
   * @param endDateTime the end date time of the event.
   * @param newPropertyValue the new property value.
   * @throws ConflictException if there is a conflict with the updated event.
   */
  void editSingle(
      SingleEventProperties property, 
      String eventName, 
      LocalDateTime startDateTime, 
      LocalDateTime endDateTime, 
      String newPropertyValue
  ) throws ConflictException;

  /**
   * Changes the property of the given event starting at a dateStringTtimeString.
   * @param property the property to change.
   * @param eventName the name of the event.
   * @param startDateTime the start date time of the event.
   * @param newPropertyValue the new property value.
   * @throws ConflictException if there is a conflict with an updated event.
   */
  void editFromDate(
      SingleEventProperties property, 
      String eventName, 
      LocalDateTime startDateTime, 
      String newPropertyValue
  ) throws ConflictException;

  /**
   * Changes the property of all events with the given name.
   * @param property the property to change.
   * @param eventName the name of the event.
   * @param newPropertyValue the new property value.
   * @throws ConflictException if there is a conflict with an updated event.
   */
  void editAll(
      SingleEventProperties property, 
      String eventName, 
      String newPropertyValue
  ) throws ConflictException;

  /**
   * Prints a bulleted list of all events on the specified date
   * along with their start times, end times, and locations if
   * any.
   * @param dateString the date to print events on.
   * @returns the string for the controller to provide to the view.
   */
  String printEventsOnDate(LocalDate dateString);

  /**
   * Prints a bulleted list of all events in the given interval
   * including their start times, end times, and locations if
   * any.
   * @param startDateTime the start date time.
   * @param endDateTime the end date time.
   */
  String printEventsFromTo(LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Prints the busy status if the user has events scheduled on the given
   * day and time, otherwise available.
   * @param dateTime the dateStringTtimeString to show busy status.
   */
  String show(LocalDateTime dateTime);

  <R> R accept(ICalendarVisitor<R> visitor);
}
