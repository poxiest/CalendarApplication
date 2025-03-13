package calendarapp.model.impl;

/**
 * Contains constant values used throughout the calendar application.
 */
public class EventConstants {

  /**
   * Constants representing CSV header field names for calendar export.
   */
  public static final class CsvHeaders {
    public static final String SUBJECT = "Subject";
    public static final String START_DATE = "Start Date";
    public static final String START_TIME = "Start Time";
    public static final String END_DATE = "End Date";
    public static final String END_TIME = "End Time";
    public static final String ALL_DAY_EVENT = "All Day Event";
    public static final String DESCRIPTION = "Description";
    public static final String LOCATION = "Location";
    public static final String PRIVATE = "Private";
  }

  /**
   * Constants related to CSV formatting for calendar export.
   */
  public static final class CsvFormat {
    public static final String DELIMITER = ",";
    public static final String LINE_END = "\n";
    public static final String TRUE_VALUE = "TRUE";
    public static final String FALSE_VALUE = "FALSE";
  }

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
  }

  /**
   * Constants representing possible calendar status values.
   */
  public static final class Status {
    public static final String BUSY = "Busy";
    public static final String AVAILABLE = "Available";
  }
}