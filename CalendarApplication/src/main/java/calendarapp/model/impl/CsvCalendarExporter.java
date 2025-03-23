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

  private String determinePrivacyFlag(EventVisibility visibility) {
    return EventVisibility.PRIVATE.equals(visibility)
        ? Constants.CsvFormat.TRUE_VALUE
        : Constants.CsvFormat.FALSE_VALUE;
  }

  private String escapeField(String field) {
    if (field == null || field.isEmpty()) return "";
    return "\"" + field.replace("\"", "\"\"") + "\"";
  }

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
