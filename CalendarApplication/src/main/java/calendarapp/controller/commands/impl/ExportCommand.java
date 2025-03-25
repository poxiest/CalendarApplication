package calendarapp.controller.commands.impl;

import java.util.List;
import java.util.regex.Matcher;

import calendarapp.controller.ICalendarExporter;
import calendarapp.controller.InvalidCommandException;
import calendarapp.model.ICalendarModel;
import calendarapp.controller.impl.Constants;
import calendarapp.view.ICalendarView;
import calendarapp.model.dto.CalendarExporterDTO;

import static calendarapp.controller.commands.impl.RegexPatternConstants.EXPORT_FILENAME_PATTERN;
import static calendarapp.controller.impl.Constants.EXPORTER_MAP;

/**
 * Export Command implementation for exporting calendar data to a file.
 * Parses the export command and delegates to the model for the actual export operation.
 */
public class ExportCommand extends AbstractCommand {

  /**
   * The filename to export calendar data to.
   */
  private String filename;

  /**
   * Creates a new ExportCommand with the specified model and view.
   *
   * @param model The calendar model to export data from.
   * @param view  The view to use for displaying information.
   */
  ExportCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  /**
   * Executes the export command by parsing the command string
   * and exporting the calendar data.
   *
   * @param command The command string containing export instructions.
   * @throws InvalidCommandException If the command syntax is invalid, the filename is missing,
   *                                 or an IO error occurs during export.
   */
  @Override
  public void execute(String command) throws InvalidCommandException {
    Matcher matcher = regexMatching(EXPORT_FILENAME_PATTERN, command);
    if (matcher.find()) {
      filename = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    if (filename == null) {
      throw new InvalidCommandException(command + "\nReason : Required fields are missing.\n");
    }

    try {
      String fileExtension = getFileExtension(filename);
      if (!Constants.SupportExportFormats.SUPPORTED_EXPORT_FORMATS.contains(fileExtension)) {
        throw new IllegalArgumentException("Unsupported export format: " + fileExtension
            + ". Supported formats are: " + Constants.SupportExportFormats.SUPPORTED_EXPORT_FORMATS);
      }
      ICalendarExporter exporter = EXPORTER_MAP.get(fileExtension);

      if (exporter == null) {
        throw new IllegalStateException("No exporter for format: " + fileExtension);
      }

      List<CalendarExporterDTO> eventsToExport = model.getEventsForExport();

      view.displayMessage("CSV file Location : " + exporter.export(eventsToExport, filename));
    } catch (Exception e) {
      throw new InvalidCommandException(command + "\nReason : " + e.getMessage());
    }
  }

  /**
   * Extracts and returns the file extension from a given file path.
   *
   * @param filePath the full file path or name
   * @return the file extension, or an empty string if none found
   */
  private String getFileExtension(String filePath) {
    int lastDot = filePath.lastIndexOf(".");
    if (lastDot == -1 || lastDot == filePath.length() - 1) {
      return "";
    }
    return filePath.substring(lastDot + 1);
  }
}