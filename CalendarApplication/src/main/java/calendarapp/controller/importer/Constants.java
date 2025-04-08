package calendarapp.controller.importer;

import java.util.Map;
import java.util.Set;

import calendarapp.controller.ICalendarImporter;


public class Constants {

  public static final Map<String, ICalendarImporter> IMPORTER_MAP = Map.of(
      SupportImportFormats.CSV, new CsvCalendarImporter()
  );

  public static final class CsvImport {
    public static final String DATE_FORMAT = "MM/dd/yyyy";
    public static final String TIME_FORMAT = "h:mm a";
  }

  public static final class SupportImportFormats {
    public static final String CSV = "csv";
    public static final Set<String> SUPPORTED_IMPORT_FORMATS = Set.of(CSV);
  }
}