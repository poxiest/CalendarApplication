package calendar;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import controller.Controller;
import controller.ControllerWithView;
import controller.IController;
import controller.IControllerWithView;
import model.IMultipleCalendarModel;
import model.MultipleCalendarModel;
import view.View;
import view.IView;

/**
 * The entry point for the calendar application which initializes
 * the model, controller, and optionally a view.
 */
public class CalendarApp {
  /**
   * This is the main method that starts the CalendarApp program.
   * It takes either zero, two, or three arguments denoting the mode the
   * program is to be run in. There are three modes, headless and
   * interactive, and gui. In headless mode, a file of commands separated
   * by newline characters must be provided which the program will
   * parse and run sequentially. In interactive mode, the user is
   * able to provide commands through the view. By default this 
   * view is the command line. The gui will then allow users to interact
   * with the calendar app through a graphical user interface.
   * The three ways to start the program are listed below:
   * java -jar Program.jar (gui)
   * java CalendarApp.java --mode interactive (interactive)
   * java CalendarApp.java --mode headless pathToCommandTextFile (headless)
   * @param args --mode, mode, and pathToCommandTextFile.
   * @throws IOException if an IOException occurs based on user input.
   */
  public static void main(String[] args) throws IOException {
    String usage = "usage:\nTo run in interactive mode:\n";
    usage += "java -jar CalendarApp.jar --mode interactive\n\n";
    usage += "To run in headless mode:\n";
    usage += "java -jar Program.jar --mode headless path-of-script-file\n";
    usage += "To run with GUI:\n";
    usage += "java -jar Program.jar\n";
    try {
      IMultipleCalendarModel model = new MultipleCalendarModel();
      switch (args.length) {
        case 0:
          IControllerWithView controllerWithView 
              = new ControllerWithView(model, null, new StringBuffer());
          IView view = new View();
          controllerWithView.setView(view);
          break;
        case 2:
          String flag = args[0].toLowerCase();
          String mode = args[1].toLowerCase();
          if (flag.compareTo("--mode") == 0
              && mode.compareTo("interactive") == 0
          ) {
            Readable in = new InputStreamReader(System.in);
            IController controller = new Controller(model, in, System.out);
            controller.execute();
          }
          break;
        case 3:
          String f = args[0].toLowerCase();
          String m = args[1].toLowerCase();
          if (f.compareTo("--mode") == 0
              && m.compareTo("headless") == 0
          ) {
            String pathToCommandTextFile = args[2];
            File commandFile = new File(pathToCommandTextFile);
            Readable in = new FileReader(commandFile);
            IController controller = new Controller(model, in, System.out);
            controller.execute();
          }
          else {
            throw new IOException(usage);
          }
          break;
        default:
          throw new IOException(usage);
      }
    }
    catch (Exception ex) {
      System.out.println("Error:\n" + ex.getMessage());
    }
  }
}