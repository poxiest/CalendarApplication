package calendarapp.controller.commands;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;

/**
 * Interface representing a command in the calendar application.
 * Each concrete command implementation must provide execution logic
 * for specific command operations.
 */
public interface Command {

  /**
   * Executes the specified command with the given command string.
   *
   * @param command The command string containing instructions and parameters
   */
  void execute(String command) throws InvalidCommandException;
}