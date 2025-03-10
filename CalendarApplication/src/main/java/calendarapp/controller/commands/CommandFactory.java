package calendarapp.controller.commands;

public interface CommandFactory {
  Command getCommand(String command);
}
