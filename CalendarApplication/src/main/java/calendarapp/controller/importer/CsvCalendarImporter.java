package calendarapp.controller.importer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import calendarapp.controller.ICalendarImporter;
import calendarapp.controller.exporter.Constants.CsvFormat;
import calendarapp.model.EventVisibility;
import calendarapp.model.dto.CalendarImporterDTO;

import static calendarapp.utils.TimeUtil.parseAndFormatDateTime;

/**
 * Imports calendar events from a CSV file format.
 * Implements the ICalendarImporter interface.
 */
public class CsvCalendarImporter implements ICalendarImporter {
  @Override
  public List<CalendarImporterDTO> importEvents(String filePath) throws Exception {
    List<CalendarImporterDTO> importedEvents = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line = reader.readLine();
      if (line == null) {
        throw new Exception("Empty CSV file");
      }

      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          continue;
        }

        CalendarImporterDTO eventDto = parseCsvLine(line);
        importedEvents.add(eventDto);
      }
    }

    return importedEvents;
  }

  /**
   * Parses a CSV line into a CalendarImporterDTO object.
   *
   * @param line the CSV line to parse
   * @return the parsed CalendarImporterDTO
   * @throws Exception if there is an error during parsing
   */
  private CalendarImporterDTO parseCsvLine(String line) throws Exception {
    List<String> fields = parseCsvFields(line);

    if (fields.size() < 9) {
      throw new Exception("CSV line has insufficient fields");
    }

    String subject = fields.get(0);
    String startDate = fields.get(1);
    String startTime = fields.get(2);
    String endDate = fields.get(3);
    String endTime = fields.get(4);
    String description = fields.get(6).isEmpty() ? null : fields.get(6);
    ;
    String location = fields.get(7).isEmpty() ? null : fields.get(7);
    String visibility = CsvFormat.TRUE_VALUE.equalsIgnoreCase(fields.get(8).trim()) ?
        EventVisibility.PRIVATE.toString().toLowerCase() :
        EventVisibility.PUBLIC.toString().toLowerCase();

    String startDateTime = parseAndFormatDateTime(startDate, startTime);
    String endDateTime = parseAndFormatDateTime(endDate, endTime);


    return CalendarImporterDTO.builder()
        .eventName(subject)
        .startTime(startDateTime)
        .endTime(endDateTime)
        .description(description)
        .location(location)
        .visibility(visibility)
        .build();
  }

  /**
   * Parses CSV fields, handling quoted fields correctly.
   *
   * @param line the CSV line to parse
   * @return a list of parsed fields
   */
  private List<String> parseCsvFields(String line) {
    List<String> fields = new ArrayList<>();
    StringBuilder field = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if (c == '"') {
        // Check if this is an escaped quote
        if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
          field.append('"');
          i++; // Skip the next quote
        } else {
          inQuotes = !inQuotes;
        }
      } else if (c == ',' && !inQuotes) {
        fields.add(field.toString());
        field.setLength(0);
      } else {
        field.append(c);
      }
    }

    // Add the last field
    fields.add(field.toString());

    return fields;
  }
}