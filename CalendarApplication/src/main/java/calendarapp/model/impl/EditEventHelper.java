package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.LinkedHashMap;
import java.util.Map;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.Constants;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.utils.TimeUtil;

/**
 * Helper class to validate edit event request.
 */
public class EditEventHelper {
  public static final String[] EDIT_EVENT_PRECEDENCE = {
      Constants.PropertyKeys.LOCATION,
      Constants.PropertyKeys.RECURRING_DAYS,
      Constants.PropertyKeys.OCCURRENCE_COUNT,
      Constants.PropertyKeys.RECURRENCE_END_DATE,
      Constants.PropertyKeys.DESCRIPTION,
      Constants.PropertyKeys.VISIBILITY,
      Constants.PropertyKeys.END_TIME,
      Constants.PropertyKeys.START_TIME,
      Constants.PropertyKeys.NAME};

  /**
   * Determines which single property (based on the given precedence) should be updated.
   *
   * @param existing The current event values.
   * @param changes  The map with proposed changes.
   * @return A LinkedHashMap containing only one entry—the property to be updated and its new value.
   */
  public static Map<String, String> getEditEventChanges(EventsResponseDTO existing, Map<String,
      String> changes) {
    Map<String, String> update = new LinkedHashMap<>();
    for (String property : EDIT_EVENT_PRECEDENCE) {
      if (changes.containsKey(property)) {
        String newValue = changes.get(property);

        if (!hasValueChanged(existing, property, newValue)) {
          continue;
        }

        if (property.equals(Constants.PropertyKeys.OCCURRENCE_COUNT)
            || property.equals(Constants.PropertyKeys.RECURRENCE_END_DATE)) {
          if (existing.getRecurringDays() == null || existing.getRecurringDays().trim().isEmpty()) {
            throw new InvalidCommandException("Cannot edit recurrence property on a single event.");
          }
          if (property.equals(Constants.PropertyKeys.OCCURRENCE_COUNT)) {
            if (existing.getOccurrenceCount() != null && newValue == null) {
              throw new InvalidCommandException("Can update only occurrence count.");
            }
          } else {
            if (existing.getRecurrenceEndDate() != null && newValue == null) {
              throw new InvalidCommandException("Can only update recurrence end date.");
            }
          }
        }
        update.put(property, newValue);
      }
    }
    if (update.entrySet().stream().filter(e ->
        e.getKey().equals(Constants.PropertyKeys.NAME)
            || e.getKey().equals(Constants.PropertyKeys.START_TIME)
            || e.getKey().equals(Constants.PropertyKeys.END_TIME)).count() > 1) {
      throw new InvalidCommandException("Please edit only one of Name, Start Time and End Time at"
          + " a time.");
    }
    return update;
  }

  /**
   * Checks whether the provided new value differs from the existing value in the
   * EventResponseDTO for the given property.
   *
   * @param existing The existing EventResponseDTO.
   * @param property The event property key.
   * @param newValue The new value (String) for that property.
   * @return true if the value is different, false otherwise.
   */
  public static boolean hasValueChanged(EventsResponseDTO existing, String property,
                                        String newValue) {
    if (property.equals(Constants.PropertyKeys.NAME)) {
      String current = existing.getEventName();
      return !stringEquals(current, newValue);
    } else if (property.equals(Constants.PropertyKeys.START_TIME)) {
      return timeUtilHelper(existing.getStartTime(), newValue);
    } else if (property.equals(Constants.PropertyKeys.END_TIME)) {
      return timeUtilHelper(existing.getEndTime(), newValue);
    } else if (property.equals(Constants.PropertyKeys.LOCATION)) {
      String current = existing.getLocation();
      return !stringEquals(current, newValue);
    } else if (property.equals(Constants.PropertyKeys.RECURRING_DAYS)) {
      String current = existing.getRecurringDays();
      return !stringEquals(current, newValue);
    } else if (property.equals(Constants.PropertyKeys.OCCURRENCE_COUNT)) {
      Integer current = existing.getOccurrenceCount();
      String currentStr = current == null ? null : current.toString();
      return !stringEquals(currentStr, newValue);
    } else if (property.equals(Constants.PropertyKeys.RECURRENCE_END_DATE)) {
      return timeUtilHelper(existing.getRecurrenceEndDate(), newValue);
    } else if (property.equals(Constants.PropertyKeys.DESCRIPTION)) {
      String current = existing.getDescription();
      return !stringEquals(current, newValue);
    } else if (property.equals(Constants.PropertyKeys.VISIBILITY)) {
      String current = existing.getVisibility();
      return !stringEquals(current, newValue);
    }
    return false;
  }

  private static boolean stringEquals(String s1, String s2) {
    if (s1 == null) {
      return (s2 == null || s2.trim().isEmpty());
    }
    return s1.equals(s2);
  }

  private static boolean timeUtilHelper(Temporal value, String newValue) {
    if (value == null && newValue == null) {
      return false;
    }
    if (value == null || newValue == null) {
      return true;
    }
    return !TimeUtil.isEqual(value, TimeUtil.getTemporalFromString(newValue));
  }
}