package calendarapp.model;

import calendarapp.controller.InvalidCommandException;

/**
 * A functional interface that defines an operation for updating a calendar property.
 * Given an existing calendar and a new property value, it returns an updated calendar.
 */
@FunctionalInterface
public interface CalendarUpdateOperation {
  /**
   * Updates the given calendar with the specified property value.
   *
   * @param oldCalendar   the original calendar to update
   * @param propertyValue the new value for the property
   * @return a new instance of {@link ICalendar} with the updated property
   * @throws InvalidCommandException if the update operation is invalid
   */
  ICalendar update(ICalendar oldCalendar, String propertyValue) throws InvalidCommandException;
}