package calendarapp.controller.importer;

import java.util.Map;

import calendarapp.controller.ICalendarImporter;


public class Constants {
  public static final Map<String, ICalendarImporter> IMPORTER_MAP = Map.of(
      SupportImportFormats.CSV, new CsvCalendarImporter()
  );

  public static final class SupportImportFormats {
    public static final String CSV = "csv";
  }
}