package calendarapp.controller.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendarapp.model.ICalendarApplication;
import calendarapp.view.ICalendarView;

public abstract class AbstractCommand implements Command {

  protected ICalendarApplication model;
  protected ICalendarView view;

  AbstractCommand(ICalendarApplication model, ICalendarView view) {
    this.model = model;
    this.view = view;
  }

  protected Matcher regexMatching(String regexPattern, String command) {
    Pattern pattern = Pattern.compile(regexPattern);
    return pattern.matcher(command);
  }
}
