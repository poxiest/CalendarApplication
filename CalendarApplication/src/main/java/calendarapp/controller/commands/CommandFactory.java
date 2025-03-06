package calendarapp.controller.commands;

public interface CommandFactory {
  Command createCommand(String command);
}
