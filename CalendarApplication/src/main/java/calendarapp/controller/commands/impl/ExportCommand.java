package calendarapp.controller.commands.impl;

import java.io.IOException;
import java.util.regex.Matcher;

import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.EXPORT_FILENAME_PATTERN;

public class ExportCommand extends AbstractCommand {

  private String filename;

  ExportCommand(ICalendarModel model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    Matcher matcher = regexMatching(EXPORT_FILENAME_PATTERN, command);
    if (matcher.find()) {
      filename = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    if (filename == null) {
      view.displayMessage("Required fields are missing. Cannot process the command.\n\n");
      return;
    }

    try {
      model.export(filename);
    } catch (IOException e) {
      view.displayMessage("Error exporting: " + e.getMessage() + ".\n");
    }
  }
}
