package calendarapp.model;

import java.time.temporal.Temporal;
import java.util.List;

/**
 * Interface for calendar events in the calendar application.
 */
public interface IEvent {

  /**
   * Determines whether this event has a time conflict with another event.
   *
   * @param other the event to check for conflicts with.
   * @return true if the events conflict (overlap in time), false otherwise.
   */
  boolean conflictsWith(IEvent other);

  /**
   * Checks if the event is active (ongoing) at the specified date and time.
   *
   * @param dateTime the date and time to check.
   * @return true if the event is active at the specified time, false otherwise.
   */
  boolean isActiveAt(Temporal dateTime);

  /**
   * Checks if this event matches the specified name.
   *
   * @param eventName the name to match against, or null to match any event.
   * @return true if the event name matches or if eventName is null, false otherwise.
   */
  boolean matchesName(String eventName);

  /**
   * Determines if this event falls within the specified time range.
   *
   * @param startDateTime the start of the time range, or null for no start constraint.
   * @param endDateTime   the end of the time range, or null for no end constraint.
   * @return true if the event is within the specified time range, false otherwise.
   */
  boolean isWithinTimeRange(Temporal startDateTime, Temporal endDateTime);

  /**
   * Creates a new event with the specified property updated to the given value.
   *
   * @param property the property to update.
   * @param value    the new value for the property.
   * @return a new IEvent instance with the updated property.
   * @throws IllegalArgumentException if the property is not supported or the value is invalid.
   */
  IEvent updateProperty(String property, String value) throws IllegalArgumentException;

  /**
   * Formats the event details for display in the user interface.
   *
   * @return a formatted string representation of the event suitable for display.
   */
  String formatForDisplay();

  /**
   * Formats the event details for export to external formats.
   *
   * @return a string representation of the event suitable for export.
   */
  String formatForExport();

  /**
   * Indicates whether this event should automatically decline conflicting events.
   *
   * @return true if this event should auto-decline conflicts, false otherwise.
   */
  boolean shouldAutoDecline();

  /**
   * Determines if this event has any time intersection with the specified time range.
   * An intersection occurs if the event's time period overlaps at all with the
   * given time range.
   *
   * @param startTime the start of the time range to check.
   * @param endTime   the end of the time range to check.
   * @return true if there is any intersection, false otherwise.
   */
  boolean hasIntersectionWith(Temporal startTime, Temporal endTime);

  /**
   * Find the difference between start date times.
   *
   * @param event Other event to check with.
   * @return Difference between two times in int.
   */
  int getDifference(IEvent event);

  List<IEvent> createRecurringEvents();
}