package calendarapp.model.calendar;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.model.event.EventVisibility;
import calendarapp.model.event.IEvent;

import static calendarapp.utils.TimeUtil.formatDate;
import static calendarapp.utils.TimeUtil.formatTime;

public class CalendarExporter {

  private static final String SUBJECT = "Subject";
  private static final String START_DATE = "Start Date";
  private static final String START_TIME = "Start Time";
  private static final String END_DATE = "End Date";
  private static final String END_TIME = "End Time";
  private static final String ALL_DAY_EVENT = "All Day Event";
  private static final String DESCRIPTION = "Description";
  private static final String LOCATION = "Location";
  private static final String PRIVATE = "Private";

  private static final String TRUE_VALUE = "TRUE";
  private static final String FALSE_VALUE = "FALSE";

  private static final String CSV_DELIMITER = ",";
  private static final String CSV_LINE_END = "\n";

  public static void exportEventAsGoogleCalendarCsv(List<IEvent> events, String filePath) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.write(String.join(CSV_DELIMITER,
              SUBJECT, START_DATE, START_TIME, END_DATE, END_TIME,
              ALL_DAY_EVENT, DESCRIPTION, LOCATION, PRIVATE));
      writer.write(CSV_LINE_END);

      for (IEvent event : events) {
        writer.write(formatEventAsCsvRow(event));
        writer.write(CSV_LINE_END);
      }
    }
  }

  private static String formatEventAsCsvRow(IEvent event) {
    StringBuilder row = new StringBuilder();

    boolean isAllDay = isAllDayEvent(event.getStartDateTime());

    row.append(escapeField(event.getName())).append(CSV_DELIMITER);
    row.append(formatDate(event.getStartDateTime())).append(CSV_DELIMITER);
    row.append(isAllDay ? "" : formatTime(event.getStartDateTime())).append(CSV_DELIMITER);
    row.append(formatDate(event.getEndDateTime())).append(CSV_DELIMITER);
    row.append(isAllDay ? "" : formatTime(event.getEndDateTime())).append(CSV_DELIMITER);
    row.append(isAllDay ? TRUE_VALUE : FALSE_VALUE).append(CSV_DELIMITER);
    row.append(escapeField(event.getDescription())).append(CSV_DELIMITER);
    row.append(escapeField(event.getLocation())).append(CSV_DELIMITER);
    row.append(isPrivate(event.getVisibility())).append(CSV_DELIMITER);

    return row.toString();
  }

  private static boolean isAllDayEvent(Temporal time) {
    if (time instanceof LocalDate) {
      return true;
    } else if (time instanceof LocalDateTime) {
      LocalDateTime dateTime = (LocalDateTime) time;
      return dateTime.getHour() == 0 && dateTime.getMinute() == 0 && dateTime.getSecond() == 0;
    }
    return false;
  }

  private static String isPrivate(EventVisibility visibility) {
    return EventVisibility.PRIVATE.equals(visibility) ? TRUE_VALUE : FALSE_VALUE;
  }

  private static String escapeField(String field) {
    if (field == null || field.isEmpty()) {
      return "";
    }
    return "\"" + field.replace("\"", "\"\"") + "\"";
  }
}