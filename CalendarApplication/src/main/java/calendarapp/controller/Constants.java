package calendarapp.controller;

import java.util.Map;
import java.util.Set;

import calendarapp.controller.exporter.CsvCalendarExporter;
import calendarapp.controller.importer.CsvCalendarImporter;

import static calendarapp.controller.Constants.SupportExportFormats.CSV;

/**
 * Class containing exporter constants.
 */
public class Constants {
  /*
  Create command regex pattern constants.
   */
  public static final String EVENT = "(?i)create\\s+event";
  public static final String CALENDAR = "(?i)create\\s+calendar";
  public static final String CREATE_AUTO_DECLINE_PATTERN = "--autoDecline";
  public static final String CREATE_EVENT_NAME_PATTERN =
      "(?i)\\s+event\\s+(?:\"([^\"]+)\"|(\\S+))\\s+(on|from)";
  public static final String CREATE_FROM_TO_PATTERN =
      "(?i)\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String CREATE_ON_PATTERN = "(?i)\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String CREATE_REPEATS_F0R_PATTERN =
      "(?i)\\s+repeats\\s+(?:\"([^\"]+)\"|(\\S+))\\s+for\\s+(\\d+)\\s+times";
  public static final String CREATE_REPEATS_UNTIL_PATTERN =
      "(?i)\\s+repeats\\s+(?:\"([^\"]+)\"|(\\S+))\\s+until\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String CREATE_OPTIONAL_PARAMETERS =
      "(?i)(description|location|visibility)\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String IS_RECURRING_EVENT = "repeats";
  public static final String CREATE_NEW_CALENDAR = "(?i)calendar\\s+--name\\s+(?:\"([^\"]+)\"|"
      + "(\\S+))\\s+--timezone\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Edit command regex pattern constants.
   */
  public static final String EDIT_FROM_TO_PATTERN =
      "(?i)\\s+event\\s+([a-zA-Z_]+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+from\\s+(?:\"([^\"]+)\""
          + "|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))\\s+with\\s+(?:\"([^\"]+)\"|(\\S+))$";
  public static final String EDIT_FROM_PATTERN =
      "(?i)\\s+events\\s+([a-zA-Z_]+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+from\\s+(?:\"([^\"]+)\""
          + "|(\\S+))\\s+with\\s+(?:\"([^\"]+)\"|(\\S+))$";
  public static final String EDIT_EVENT_NAME_PATTERN =
      "(?i)\\s+events\\s+([a-zA-Z_]+)\\s+(?:\"([^\"]+)\"|([^\\s\"]+))\\s+(?:\"([^\"]+)\""
          + "|([^\\s\"]+))$";
  public static final String EDIT_CALENDAR_PATTERN = "(?i)\\s+--name\\s+(?:\"([^\"]+)\"|(\\S+))"
      + "\\s+--property\\s+([a-zA-Z_]+)\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Export command regex pattern constants.
   */
  public static final String EXPORT_FILENAME_PATTERN = "(?i)\\s+cal\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Print command regex pattern constants.
   */
  public static final String PRINT_FROM_TO_PATTERN =
      "(?i)\\s+events\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String PRINT_ON_PATTERN = "(?i)\\s+events\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))";

  /*
  Show command regex pattern constants.
   */
  public static final String STATUS_ON_PATTERN = "(?i)\\s+status\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Use calendar command regex pattern constant.
   */
  public static final String USE_COMMAND = "(?i)\\s+calendar\\s+--name\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Copy command regex pattern constants.
   */
  public static final String COPY_EVENT_COMMAND = "(?i)\\s+event\\s+(?:\"([^\"]+)\"|(\\S+))"
      + "\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))\\s+--target\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\""
      + "([^\"]+)\"|(\\S+))$";
  public static final String COPY_EVENTS_ON_COMMAND = "(?i)\\s+events\\s+on\\s+(?:\"([^\"]+)\"|"
      + "(\\S+))\\s+--target\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))$";
  public static final String COPY_EVENTS_BETWEEN_COMMAND = "(?i)\\s+events\\s+between\\s+(?:\""
      + "([^\"]+)\"|(\\S+))\\s+and\\s+(?:\"([^\"]+)\"|(\\S+))\\s+--target\\s+(?:\"([^\"]+)\"|"
      + "(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /**
   * A map of supported export formats to their corresponding calendar exporter.
   */
  public static final Map<String, ICalendarExporter> EXPORTER_MAP = Map.of(
      CSV, new CsvCalendarExporter()
  );
  /**
   * A map of supported import formats to their corresponding calendar importer.
   */
  public static final Map<String, ICalendarImporter> IMPORTER_MAP = Map.of(
      calendarapp.controller.Constants.SupportImportFormats.CSV, new CsvCalendarImporter()
  );

  /**
   * Constants representing CSV header field names for calendar export.
   */
  public static final class CsvHeaders {
    public static final String SUBJECT = "Subject";
    public static final String START_DATE = "Start Date";
    public static final String START_TIME = "Start Time";
    public static final String END_DATE = "End Date";
    public static final String END_TIME = "End Time";
    public static final String ALL_DAY_EVENT = "All Day Event";
    public static final String DESCRIPTION = "Description";
    public static final String LOCATION = "Location";
    public static final String PRIVATE = "Private";
  }

  /**
   * Constants related to CSV formatting for calendar export.
   */
  public static final class CsvFormat {
    public static final String DELIMITER = ",";
    public static final String LINE_END = "\n";
    public static final String TRUE_VALUE = "TRUE";
    public static final String FALSE_VALUE = "FALSE";
  }

  /**
   * Defines supported file formats for exporting calendar data.
   */
  public static final class SupportExportFormats {
    public static final String CSV = "csv";
    public static final Set<String> SUPPORTED_EXPORT_FORMATS = Set.of(CSV);
  }

  /**
   * Defines supported file formats for importing calendar data.
   */
  public static final class SupportImportFormats {
    public static final String CSV = "csv";
  }
}
