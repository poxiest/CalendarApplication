package calendarapp.controller.commands.impl;

import java.io.IOException;
import java.util.regex.Matcher;

import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

import static calendarapp.controller.commands.impl.RegexPatternConstants.filenamePatter;

public class ExportCommand extends AbstractCommand {

  private String filename;

  ExportCommand(ICalendarApplication model, ICalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String command) {
    Matcher matcher = regexMatching(filenamePatter, command);
    if (matcher.find()) {
      filename = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    }

    if (filename == null) {
      view.display("Required fields are missing. Cannot process the command.\n");
      return;
    }

    try {
      model.export(filename);
    } catch (IOException e) {
      view.display("Error exporting: " + e.getMessage() + ".\n");
    }
  }
}
