package calendarapp.model;

import java.time.temporal.Temporal;

/**
 * Interface representing a calendar event in the calendar application.
 */
public interface IEvent {

  /**
   * Gets the name of the event.
   *
   * @return the name of the event.
   */
  String getName();

  /**
   * Gets the start time of the event.
   *
   * @return the start time of the event as a {@link Temporal} object.
   */
  Temporal getStartTime();

  /**
   * Gets the end time of the event.
   *
   * @return the end time of the event as a {@link Temporal} object.
   */
  Temporal getEndTime();

  /**
   * Gets the description of the event.
   *
   * @return the description of the event as a String.
   */
  String getDescription();

  /**
   * Gets the location where the event is taking place.
   *
   * @return the location of the event as a String.
   */
  String getLocation();

  /**
   * Gets the visibility setting of the event.
   *
   * @return the visibility of the event as an {@link EventVisibility} object.
   */
  EventVisibility getVisibility();

  /**
   * Gets the recurring days of the event (if applicable).
   * The format is a string representing days of the week: M (Monday), T (Tuesday),
   * W (Wednesday), R (Thursday), F (Friday), S (Saturday), U (Sunday).
   * For example, "MWF" represents Monday, Wednesday, and Friday.
   *
   * @return the recurring days as a String or null if the event is not recurring.
   */
  String getRecurringDays();

  /**
   * Gets the occurrence count for recurring events.
   *
   * @return the number of occurrences for a recurring event, or null if not applicable.
   */
  Integer getOccurrenceCount();

  /**
   * Gets the recurrence end date for recurring events.
   * This date signifies when the recurring events stop repeating.
   *
   * @return the recurrence end date as a {@link Temporal} object, or null if not applicable.
   */
  Temporal getRecurrenceEndDate();


  /**
   * Updates the property of the event.
   * The property name and value are passed as strings, and the event is updated accordingly.
   *
   * @param name  the property name to be updated.
   * @param value the new value for the property.
   * @return a new {@link IEvent} instance with the updated property.
   * @throws IllegalArgumentException if the property is not valid or the value is invalid.
   */
  IEvent updateProperty(String name, String value) throws IllegalArgumentException;

  IEvent deepCopyEvent();
}
