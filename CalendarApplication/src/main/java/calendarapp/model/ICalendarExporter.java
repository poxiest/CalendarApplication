package calendarapp.model;

import java.util.List;

public interface ICalendarExporter {
  /**
   * Exports the provided list of events to a file.
   *
   * @param events   The list of events to export.
   * @param filePath The path where the exported file should be saved.
   * @return The absolute path to the created file.
   */
  String export(List<IEvent> events, String filePath);
}

