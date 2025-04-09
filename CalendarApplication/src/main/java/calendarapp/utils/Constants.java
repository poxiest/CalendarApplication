package calendarapp.utils;

import java.time.format.DateTimeFormatter;

public class Constants {
  public static final String CALENDAR_NAME = "name";
  public static final String CALENDAR_TIME_ZONE = "timezone";

  public static final String FIND_EVENT_NAME = "eventName";
  public static final String FIND_START_TIME = "startTime";
  public static final String FIND_END_TIME = "endTime";
  public static final String FIND_ON = "on";

  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM-dd"
      + "-yyyy HH:mm");
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

  public static final String IS_MULTIPLE = "isMultiple";
}
