package calendarapp.controller;

import java.util.List;

import calendarapp.model.dto.CalendarExporterDTO;

/**
 * Interface for CalendarExporter. CalendarExporter supports multiple file
 * formats.
 */
public interface ICalendarExporter {
  /**
   * Exports the provided list of events to a file.
   *
   * @param events   The list of events to export.
   * @param filePath The path where the exported file should be saved.
   * @return The absolute path to the created file.
   */
  String export(List<CalendarExporterDTO> events, String filePath);
}
