package calendarapp.controller;

import calendarapp.controller.commands.Command;
import calendarapp.controller.commands.CommandFactory;
import calendarapp.controller.commands.CommandFactoryImpl;
import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.view.ICalendarView;

public abstract class AbstractCalendarController implements ICalendarController {

  protected CommandFactory commandFactory;
  protected ICalendarApplication model;
  protected Readable in;
  protected ICalendarView view;

  AbstractCalendarController(Readable in, ICalendarApplication model, ICalendarView view) {
    this.in = in;
    this.view = view;
    this.model = model;
    commandFactory = new CommandFactoryImpl(model, view);
  }

  protected void processCommand(String commandString) {

//    String pattern = "\"[^\"]*\"|\\S+";
//
//    Pattern regex = Pattern.compile(pattern);
//    Matcher matcher = regex.matcher(commandString);
//
//    List<String> tokenList = new ArrayList<>();
//    while (matcher.find()) {
//      tokenList.add(matcher.group());
//    }
//
//    String[] commandArray = tokenList.toArray(new String[0]);

    Command command = commandFactory.createCommand(commandString);

    if (command != null) {
      command.execute(commandString);
    }
  }
}
