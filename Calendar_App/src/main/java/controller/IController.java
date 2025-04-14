package controller;

import java.io.IOException;

/**
 * This interface represents a Controller that handles user inputs and
 * communicates instructions to a View and a Model.
 */
public interface IController {

  /**
   * Initiates this controller to handle application I/O.
   * @throws IOException if an IOException occurs based on user input.
   */
  public void execute() throws IOException;
    
}
