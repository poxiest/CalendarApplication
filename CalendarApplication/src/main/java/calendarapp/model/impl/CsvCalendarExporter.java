package calendarapp.model.impl;

import calendarapp.model.EventVisibility;
import calendarapp.model.ICalendarExporter;
import calendarapp.model.IEvent;
import static calendarapp.utils.TimeUtil.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.temporal.Temporal;
import java.util.List;

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
        writer.write(formatEvent(event));
        writer.write(Constants.CsvFormat.LINE_END);
      }
    } catch (IOException e) {
      throw new RuntimeException("Error while exporting: " + e.getMessage());
    }

    return new File(filePath).getAbsolutePath();
  }

  /**
   * Converts a single event into a formatted CSV row string.
   *
   * @param event the event to format
   * @return the formatted CSV row
   */
  private String formatEvent(IEvent event) {
    return formatEventAsCsvRow(
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
   * @param visibility the visibility of the event
   * @return "TRUE" if private, otherwise "FALSE"
   */
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
   * @param name the name of the event
   * @param startTime the start time of the event
   * @param endTime the end time of the event
   * @param description the description of the event
   * @param location the location of the event
   * @param visibility the visibility of the event
   * @return the formatted CSV row
   */
  private String formatEventAsCsvRow(String name, Temporal startTime, Temporal endTime,
                                     String description, String location, EventVisibility visibility) {
    boolean isAllDay = isAllDayEvent(startTime, endTime);
    return String.join(Constants.CsvFormat.DELIMITER,
        escapeField(name),
        formatDate(startTime),
        isAllDay ? "" : formatTime(startTime),
        formatDate(endTime),
        isAllDay ? "" : formatTime(endTime),
        isAllDay ? Constants.CsvFormat.TRUE_VALUE : Constants.CsvFormat.FALSE_VALUE,
        escapeField(description),
        escapeField(location),
        determinePrivacyFlag(visibility)
    );
  }
}
