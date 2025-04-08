package calendarapp.controller;

import java.util.List;

import calendarapp.model.dto.CalendarImporterDTO;


public interface ICalendarImporter {
  List<CalendarImporterDTO> importEvents(String filePath) throws Exception;
}