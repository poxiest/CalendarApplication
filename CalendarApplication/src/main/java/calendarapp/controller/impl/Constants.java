package calendarapp.controller.impl;

import java.util.Map;
import java.util.Set;

import calendarapp.controller.ICalendarExporter;

import static calendarapp.controller.impl.Constants.SupportExportFormats.CSV;

public class Constants {
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
   * A map of supported export formats to their corresponding calendar exporter.
   */
  public static final Map<String, ICalendarExporter> EXPORTER_MAP = Map.of(
      CSV, new CsvCalendarExporter()
  );
}
