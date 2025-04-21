package controller;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Function;

import commands.Copy;
import commands.Create;
import commands.Edit;
import commands.Export;
import commands.IControllerCommand;
import commands.Print;
import commands.Show;
import commands.ShowCalendar;
import commands.Use;
import model.IMultipleCalendarModel;

/**
 * This is the concrete class that implements the
 * IController interface and handles all I/O interactions
 * with the view. This controller performs actions on a
 * multiple calendar model through an instance of the
 * multiple calendar model interface.
 */
public class Controller implements IController {

  protected Readable in;
  protected Appendable out;
  protected IMultipleCalendarModel calendarModel;
  protected Map<String, Function<String[], IControllerCommand>> knownCommands;
  protected Stack<String> commandsExecuted;

  /**
   * A default constructor which initializes the input and output
   * for this program.
   *
   * @param calendarModel the calendarModel used to perform operations.
   * @param in            the input readable associated with this controller.
   * @param out           the output buffer associated with this controller.
   */
  public Controller(
      IMultipleCalendarModel calendarModel,
      Readable in,
      Appendable out
  ) {
    this.calendarModel = calendarModel;
    this.in = in;
    this.out = out;
    knownCommands = new HashMap<>();
    knownCommands.put("create", c -> new Create(c, calendarModel));
    knownCommands.put("edit", c -> new Edit(c, calendarModel));
    knownCommands.put("use", c -> new Use(c, calendarModel));
    knownCommands.put("print", c -> new Print(c, calendarModel, out));
    knownCommands.put("show status", c -> new Show(c, calendarModel, out));
    knownCommands.put("copy", c -> new Copy(c, calendarModel));
    knownCommands.put("export", c -> new Export(c, calendarModel, out));
    knownCommands.put("show calendar", c -> new ShowCalendar(c, calendarModel, out));
    this.commandsExecuted = new Stack<>();
  }

  @Override
  public void execute() throws IOException {
    Objects.requireNonNull(calendarModel);
    if (this.in instanceof FileReader) {
      this.out.append("Now running calendar in headless mode.\n");
    } else if (this.in instanceof InputStreamReader) {
      this.out.append("Now running calendar in interactive mode.\n");
    }
    Scanner scan = new Scanner(this.in);
    boolean acceptingInput = true;
    while (acceptingInput) {
      try {
        String command = scan.nextLine();
        if (!command.isEmpty()) {
          acceptingInput = handleCommand(command);
        }
      } catch (Exception ex) {
        this.out.append(ex.getMessage());
      }
    }
    scan.close();
  }

  protected boolean handleCommand(String command) throws IOException {
    IControllerCommand c;
    String[] commandComponents = command.split(" ");
    if (commandComponents.length < 1) {
      throw new IOException("No command was provided");
    }
    if (commandComponents[0].compareTo("exit") == 0) {
      return false;
    }
    String commandName = !commandComponents[0].equals("show") ? commandComponents[0]
        : commandComponents[0] + " " + commandComponents[1];
    if (commandName.equalsIgnoreCase("show calendar") && !commandsExecuted.contains("use")) {
      // throwing IOException instead of custom exception because they have already
      // used it IOException.
      throw new IOException("Cannot use show calendar command before use calendar command.");
    }
    Function<String[], IControllerCommand> cmd = knownCommands.getOrDefault(
        commandName,
        null
    );
    if (cmd == null) {
      throw new IOException("Invalid command action: " + commandComponents[0]);
    } else {
      c = cmd.apply(commandComponents);
      c.execute();
      commandsExecuted.push(commandName);
    }
    return true;
  }
}