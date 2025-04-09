package calendarapp.model.impl;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    public static final List<String> RECURRING_PROPERTIES_LIST = List.of(RECURRING_DAYS,
        RECURRENCE_END_DATE, OCCURRENCE_COUNT);
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
    public static final Map<Character, DayOfWeek> DAY_MAP = new HashMap<>();

    static {
      DAY_MAP.put('M', DayOfWeek.MONDAY);
      DAY_MAP.put('T', DayOfWeek.TUESDAY);
      DAY_MAP.put('W', DayOfWeek.WEDNESDAY);
      DAY_MAP.put('R', DayOfWeek.THURSDAY);
      DAY_MAP.put('F', DayOfWeek.FRIDAY);
      DAY_MAP.put('S', DayOfWeek.SATURDAY);
      DAY_MAP.put('U', DayOfWeek.SUNDAY);
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
        DayOfWeek dayOfWeek = DAY_MAP.get(day);
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
    public static final String DEFAULT_CALENDAR_NAME = "Personal";
    public static final String CALENDAR_NAME = "name";
    public static final String CALENDAR_TIME_ZONE = "timezone";
  }
}