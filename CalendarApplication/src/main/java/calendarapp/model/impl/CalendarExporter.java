package calendarapp.model.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventVisibility;
import calendarapp.model.IEvent;

import static calendarapp.utils.TimeUtil.formatDate;
import static calendarapp.utils.TimeUtil.formatTime;
import static calendarapp.utils.TimeUtil.isAllDayEvent;

/**
 * Utility class for exporting calendar events to CSV format compatible with Google Calendar.
 * Provides methods to convert events to formatted CSV strings and write them to a file.
 */
public class CalendarExporter {

  // TODO: try to move as a interface
  /**
   * Exports a list of events to a CSV file in Google Calendar format.
   *
   * @param events   The list of events to export.
   * @param filePath The path where the CSV file should be saved.
   * @return The absolute path to the created file.
   * @throws RuntimeException If an I/O error occurs during file writing.
   */
  public static String exportEventAsGoogleCalendarCsv(List<IEvent> events, String filePath) {

    if (filePath.split("\\.").length <= 1
        || !filePath.split("\\.")[1].equalsIgnoreCase("csv")) {
      throw new InvalidCommandException("Only CSV files are supported.");
    }

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
    } catch (IOException e) {
      throw new RuntimeException("Error while exporting :" + e.getMessage());
    }

    File file = new File(filePath);
    return file.getAbsolutePath();
  }

  /**
   * Formats an event for export by delegating to the event's own formatting method.
   *
   * @param event The event to format.
   * @return A CSV-formatted string representation of the event.
   */
  private static String formatEventForExport(IEvent event) {
    return CalendarExporter.formatEventAsCsvRow(
        event.getName(),
        event.getStartTime(),
        event.getEndTime(),
        event.getDescription(),
        event.getLocation(),
        event.getVisibility()
    );
  }

  /**
   * Determines the privacy flag value based on event visibility.
   *
   * @param visibility The visibility setting of the event.
   * @return A string representation of the privacy flag ("TRUE" for private events,
   *     "FALSE" otherwise).
   */
  private static String determinePrivacyFlag(EventVisibility visibility) {
    return EventVisibility.PRIVATE.equals(visibility)
        ? EventConstants.CsvFormat.TRUE_VALUE :
        EventConstants.CsvFormat.FALSE_VALUE;
  }

  /**
   * Escapes a field for CSV format by surrounding it with quotes and escaping internal quotes.
   *
   * @param field The field content to escape.
   * @return The escaped field string.
   */
  private static String escapeField(String field) {
    if (field == null || field.isEmpty()) {
      return "";
    }
    return "\"" + field.replace("\"", "\"\"") + "\"";
  }

  /**
   * Formats an event as a CSV row using the provided event details.
   * This is a utility method that can be used when an IEvent instance is not available.
   *
   * @param name        The name of the event.
   * @param startTime   The start time of the event.
   * @param endTime     The end time of the event.
   * @param description The description of the event.
   * @param location    The location of the event.
   * @param visibility  The visibility setting of the event.
   * @return A CSV-formatted string containing all event information.
   */
  public static String formatEventAsCsvRow(String name, Temporal startTime, Temporal endTime,
                                           String description, String location,
                                           EventVisibility visibility) {
    StringBuilder row = new StringBuilder();

    boolean isAllDay = isAllDayEvent(startTime, endTime);

    row.append(escapeField(name)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(formatDate(startTime)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(isAllDay ? "" : formatTime(startTime)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(formatDate(endTime)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(isAllDay ? "" : formatTime(endTime)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(isAllDay ? EventConstants.CsvFormat.TRUE_VALUE :
        EventConstants.CsvFormat.FALSE_VALUE).append(EventConstants.CsvFormat.DELIMITER);
    row.append(escapeField(description)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(escapeField(location)).append(EventConstants.CsvFormat.DELIMITER);
    row.append(determinePrivacyFlag(visibility));

    return row.toString();
  }
}