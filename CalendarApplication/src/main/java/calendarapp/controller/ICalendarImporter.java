package calendarapp.controller;

import java.util.List;

import calendarapp.model.dto.CalendarImporterDTO;

/**
 * Interface for importing calendar events from external files into the application.
 */
public interface ICalendarImporter {
  /**
   * Imports events from the specified file path and returns a list of parsed event DTOs.
   *
   * @param filePath the path to the file to import
   * @return a list of CalendarImporterDTOs representing the imported events
   * @throws Exception if an error occurs during file reading or parsing
   */
  List<CalendarImporterDTO> importEvents(String filePath) throws Exception;
}