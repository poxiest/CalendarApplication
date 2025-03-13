package calendarapp.controller.commands.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendarapp.controller.commands.Command;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

public abstract class AbstractCommand implements Command {

  protected ICalendarModel model;
  protected ICalendarView view;

  AbstractCommand(ICalendarModel model, ICalendarView view) {
    this.model = model;
    this.view = view;
  }

  protected Matcher regexMatching(String regexPattern, String command) {
    Pattern pattern = Pattern.compile(regexPattern);
    return pattern.matcher(command.trim());
  }
}
