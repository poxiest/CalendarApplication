package calendarapp.controller.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import calendarapp.controller.ICalendarExporter;
import calendarapp.model.EventVisibility;
import calendarapp.model.IEvent;
import calendarapp.model.dto.CalendarExporterDTO;

import static calendarapp.utils.TimeUtil.formatDate;
import static calendarapp.utils.TimeUtil.formatTime;
import static calendarapp.utils.TimeUtil.isAllDayEvent;

/**
 * Exports calendar events to a CSV file format.
 * Implements the ICalendarExporter interface.
 */
public class CsvCalendarExporter implements ICalendarExporter {
  @Override
  public String export(List<CalendarExporterDTO> events, String filePath) {
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

      for (CalendarExporterDTO event : events) {
        writer.write(formatEventAsCsvRow(event));
        writer.write(Constants.CsvFormat.LINE_END);
      }
    } catch (IOException e) {
      throw new RuntimeException("Error while exporting: " + e.getMessage());
    }

    return new File(filePath).getAbsolutePath();
  }

  private String determinePrivacyFlag(String visibility) {
    return visibility.equals(EventVisibility.PRIVATE.toString().toLowerCase())
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
  private String formatEventAsCsvRow(CalendarExporterDTO event) {
    boolean isAllDay = isAllDayEvent(event.getStartDate(), event.getEndDate());

    return String.join(Constants.CsvFormat.DELIMITER,
        escapeField(event.getSubject()),
        formatDate(event.getStartDate()),
        isAllDay ? "" : formatTime(event.getStartDate()),
        formatDate(event.getEndDate()),
        isAllDay ? "" : formatTime(event.getEndDate()),
        isAllDay ? Constants.CsvFormat.TRUE_VALUE : Constants.CsvFormat.FALSE_VALUE,
        escapeField(event.getDescription()),
        escapeField(event.getLocation()),
        determinePrivacyFlag(event.getVisibility())
    );
  }
}

