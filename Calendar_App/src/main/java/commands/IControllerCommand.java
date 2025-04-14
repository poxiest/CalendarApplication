package commands;

import java.io.IOException;

/**
 * An interface for classes which implement command handlers. This
 * interface and its implementations follow the command design pattern
 * and represent possible actions this program can perform.
 */
public interface IControllerCommand {

  /**
   * Processes the given command components
   * passed to the controller following the 
   * command design pattern.
   * @throws IOException if there is an error processing the command.
   */
  void execute() throws IOException;

}
