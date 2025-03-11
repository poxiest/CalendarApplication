package calendarapp.model.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import calendarapp.model.EventVisibility;
import calendarapp.model.IEvent;

import static calendarapp.utils.TimeUtil.formatDate;
import static calendarapp.utils.TimeUtil.formatTime;
import static calendarapp.utils.TimeUtil.isAllDayEvent;

public class CalendarExporter {

  public static void exportEventAsGoogleCalendarCsv(List<IEvent> events, String filePath) throws IOException {
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
        writer.write(formatEventAsCsvRow(event));
        writer.write(EventConstants.CsvFormat.LINE_END);
      }
    }
  }

  private static String formatEventAsCsvRow(IEvent event) {
    StringBuilder row = new StringBuilder();

    boolean isAllDay = isAllDayEvent(event.getStartDateTime(), event.getEndDateTime());

    row.append(escapeField(event.getName())).append(EventConstants.CsvFormat.DELIMITER);
    row.append(formatDate(event.getStartDateTime())).append(EventConstants.CsvFormat.DELIMITER);
    row.append(isAllDay ? "" : formatTime(event.getStartDateTime())).append(EventConstants.CsvFormat.DELIMITER);
    row.append(formatDate(event.getEndDateTime())).append(EventConstants.CsvFormat.DELIMITER);
    row.append(isAllDay ? "" : formatTime(event.getEndDateTime())).append(EventConstants.CsvFormat.DELIMITER);
    row.append(isAllDay ? EventConstants.CsvFormat.TRUE_VALUE : EventConstants.CsvFormat.FALSE_VALUE).append(EventConstants.CsvFormat.DELIMITER);
    row.append(escapeField(event.getDescription())).append(EventConstants.CsvFormat.DELIMITER);
    row.append(escapeField(event.getLocation())).append(EventConstants.CsvFormat.DELIMITER);
    row.append(isPrivate(event.getVisibility())).append(EventConstants.CsvFormat.DELIMITER);

    return row.toString();
  }

  private static String isPrivate(EventVisibility visibility) {
    return EventVisibility.PRIVATE.equals(visibility) ? EventConstants.CsvFormat.TRUE_VALUE : EventConstants.CsvFormat.FALSE_VALUE;
  }

  private static String escapeField(String field) {
    if (field == null || field.isEmpty()) {
      return "";
    }
    return "\"" + field.replace("\"", "\"\"") + "\"";
  }
}