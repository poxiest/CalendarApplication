package calendarapp.model.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import calendarapp.model.EventVisibility;
import calendarapp.model.ICalendarExporter;
import calendarapp.model.IEvent;

import static calendarapp.utils.TimeUtil.formatDate;
import static calendarapp.utils.TimeUtil.formatTime;
import static calendarapp.utils.TimeUtil.isAllDayEvent;

/**
 * Exports calendar events to a CSV file format.
 * Implements the ICalendarExporter interface.
 */
public class CsvCalendarExporter implements ICalendarExporter {
  @Override
  public String export(List<IEvent> events, String filePath) {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.write(String.join(Constants.CsvFormat.DELIMITER,
          Constants.CsvHeaders.SUBJECT,
          Constants.CsvHeaders.START_DATE,
          Constants.CsvHeaders.START_TIME,
          Constants.CsvHeaders.END_DATE,
          Constants.CsvHeaders.END_TIME,
          Constants.CsvHeaders.ALL_DAY_EVENT,
          Constants.CsvHeaders.DESCRIPTION,
          Constants.CsvHeaders.LOCATION,
          Constants.CsvHeaders.PRIVATE));
      writer.write(Constants.CsvFormat.LINE_END);

      for (IEvent event : events) {
        writer.write(formatEventAsCsvRow(event));
        writer.write(Constants.CsvFormat.LINE_END);
      }
    } catch (IOException e) {
      throw new RuntimeException("Error while exporting: " + e.getMessage());
    }

    return new File(filePath).getAbsolutePath();
  }

  private String determinePrivacyFlag(EventVisibility visibility) {
    return EventVisibility.PRIVATE.equals(visibility)
        ? Constants.CsvFormat.TRUE_VALUE
        : Constants.CsvFormat.FALSE_VALUE;
  }

  /**
   * Escapes a CSV field by wrapping it in quotes and handling internal quotes.
   *
   * @param field the field to escape
   * @return the escaped field
   */
  private String escapeField(String field) {
    if (field == null || field.isEmpty()) return "";
    return "\"" + field.replace("\"", "\"\"") + "\"";
  }

  /**
   * Formats event details as a CSV row string.
   *
   * @param event is an IEvent instance
   * @return the formatted CSV row
   */
  private String formatEventAsCsvRow(IEvent event) {
    boolean isAllDay = isAllDayEvent(event.getStartTime(), event.getEndTime());

    return String.join(Constants.CsvFormat.DELIMITER,
        escapeField(event.getName()),
        formatDate(event.getStartTime()),
        isAllDay ? "" : formatTime(event.getStartTime()),
        formatDate(event.getEndTime()),
        isAllDay ? "" : formatTime(event.getEndTime()),
        isAllDay ? Constants.CsvFormat.TRUE_VALUE : Constants.CsvFormat.FALSE_VALUE,
        escapeField(event.getDescription()),
        escapeField(event.getLocation()),
        determinePrivacyFlag(event.getVisibility())
    );
  }
}
