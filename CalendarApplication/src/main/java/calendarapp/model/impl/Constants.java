package calendarapp.model.impl;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains constant values used throughout the calendar application.
 */
public class Constants {
  /**
   * Constants representing property keys used for event creation and modification.
   */
  public static final class PropertyKeys {
    public static final String NAME = "eventname";
    public static final String START_TIME = "from";
    public static final String END_TIME = "to";
    public static final String DESCRIPTION = "description";
    public static final String LOCATION = "location";
    public static final String VISIBILITY = "visibility";
    public static final String RECURRING_DAYS = "recurring_days";
    public static final String OCCURRENCE_COUNT = "occurrence_count";
    public static final String RECURRENCE_END_DATE = "recurrence_end_date";
  }

  /**
   * Constants representing possible calendar status values.
   */
  public static final class Status {
    public static final String BUSY = "Busy";
    public static final String AVAILABLE = "Available";
  }

  /**
   * Provides utilities for parsing characters into DayOfWeek values.
   */
  public static final class DaysOfWeek {
    /**
     * Maps characters to corresponding DayOfWeek values.
     */
    public static final Map<Character, DayOfWeek> dayMap = new HashMap<>();

    static {
      dayMap.put('M', DayOfWeek.MONDAY);
      dayMap.put('T', DayOfWeek.TUESDAY);
      dayMap.put('W', DayOfWeek.WEDNESDAY);
      dayMap.put('R', DayOfWeek.THURSDAY);
      dayMap.put('F', DayOfWeek.FRIDAY);
      dayMap.put('S', DayOfWeek.SATURDAY);
      dayMap.put('U', DayOfWeek.SUNDAY);
    }

    /**
     * Parses a string of day characters into a set of DayOfWeek values.
     *
     * @param daysString the string containing day characters (e.g., "MWF")
     * @return a set of corresponding DayOfWeek values
     * @throws IllegalArgumentException if any character is invalid
     */
    public static Set<DayOfWeek> parseDaysOfWeek(String daysString) {
      Set<DayOfWeek> days = new HashSet<>();
      for (char day : daysString.toUpperCase().toCharArray()) {
        DayOfWeek dayOfWeek = dayMap.get(day);
        if (dayOfWeek == null) {
          throw new IllegalArgumentException("Invalid day character: " + day);
        }
        days.add(dayOfWeek);
      }
      return days;
    }
  }

  /**
   * Defines property keys for calendar attributes.
   */
  public static final class Calendar {
    public static final String DEFAULT_TIME_ZONE = "America/New_York";
    public static final String CALENDAR_NAME = "name";
    public static final String CALENDAR_TIME_ZONE = "timezone";
  }
}