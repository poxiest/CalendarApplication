package calendarapp.model.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.model.EventVisibility;
import calendarapp.model.IEvent;

import static calendarapp.utils.TimeUtil.formatDate;
import static calendarapp.utils.TimeUtil.formatTime;
import static calendarapp.utils.TimeUtil.isAllDayEvent;

public class CalendarExporter {

  public static String exportEventAsGoogleCalendarCsv(List<IEvent> events, String filePath) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.write(String.join(EventConstants.CsvFormat.DELIMITER,
          EventConstants.CsvHeaders.SUBJECT,
          EventConstants.CsvHeaders.START_DATE,
          EventConstants.CsvHeaders.START_TIME,
          EventConstants.CsvHeaders.END_DATE,
          EventConstants.CsvHeaders.END_TIME,
          EventConstants.CsvHeaders.ALL_DAY_EVENT,
          EventConstants.CsvHeaders.DESCRIPTION,
          EventConstants.CsvHeaders.LOCATION,
          EventConstants.CsvHeaders.PRIVATE));
      writer.write(EventConstants.CsvFormat.LINE_END);

      for (IEvent event : events) {
        writer.write(formatEventForExport(event));
        writer.write(EventConstants.CsvFormat.LINE_END);
      }
    }

    File file = new File(filePath);
    return file.getAbsolutePath();
  }

  private static String formatEventForExport(IEvent event) {
    return event.formatForExport();
  }

  public static String determinePrivacyFlag(EventVisibility visibility) {
    return EventVisibility.PRIVATE.equals(visibility) ?
        EventConstants.CsvFormat.TRUE_VALUE :
        EventConstants.CsvFormat.FALSE_VALUE;
  }

  public static String escapeField(String field) {
    if (field == null || field.isEmpty()) {
      return "";
    }
    return "\"" + field.replace("\"", "\"\"") + "\"";
  }

  public static String formatEventAsCsvRow(String name, Temporal startTime, Temporal endTime,
                                           String description, String location, EventVisibility visibility) {
    StringBuilder row = new StringBuilder();

    boolean isAllDay = isAllDayEvent(startTime, endTime);

    row.append(escapeField(name)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(formatDate(startTime)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(isAllDay ? "" : formatTime(startTime)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(formatDate(endTime)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(isAllDay ? "" : formatTime(endTime)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(isAllDay ? EventConstants.CsvFormat.TRUE_VALUE : EventConstants.CsvFormat.FALSE_VALUE).append(EventConstants.CsvFormat.DELIMITER);
    row.append(escapeField(description)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(escapeField(location)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(determinePrivacyFlag(visibility));

    return row.toString();
  }
}