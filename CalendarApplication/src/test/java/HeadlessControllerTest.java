import org.junit.Before;
import org.junit.Test;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.InvalidCommandException;
import calendarapp.controller.impl.CalendarControllerFactory;
import calendarapp.model.ICalendarModel;
import calendarapp.model.impl.CalendarModel;
import calendarapp.view.ICalendarView;

import static org.junit.Assert.assertEquals;

/**
 * Test for Headless controller mode.
 */
public class HeadlessControllerTest {

  private ICalendarController controller;
  private ICalendarModel model;
  private MockView view;
  private String filepath = System.getProperty("user.dir").contains("CalendarApplication")
      ? System.getProperty("user.dir") : System.getProperty("user.dir") + "/CalendarApplication";

  @Before
  public void setUp() {
    model = new CalendarModel();
    view = new MockView();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHeadless() {
    controller = CalendarControllerFactory.getController("headless", "",
        model, view);
  }

  @Test(expected = InvalidCommandException.class)
  public void testHeadless1() {
    try {
      controller = CalendarControllerFactory.getController("headless",
          filepath + "/src/test/java/withoutExitCommand.txt",
          model, view);
      controller.start();
    } catch (Exception e) {
      assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
          + "Processing command: create event test on \"2025-11-11\"\n"
          + "\n"
          + "Enter command or enter 'exit' to exit the calendar application.\n"
          + "Processing command: print events from \"2025-11-09\" to \"2025-11-25\"\n"
          + "Events:\n"
          + "• test - 2025-11-11T00:00 to 2025-11-12T00:00 \n"
          + "\n"
          + "Enter command or enter 'exit' to exit the calendar application.\n", view.getResult());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHeadless1_1() {
    controller = CalendarControllerFactory.getController("headless",
        filepath + "/src/test/java/withoutExitCommand",
        model, view);
    controller.start();
  }

  @Test
  public void testHeadless2() {
    controller = CalendarControllerFactory.getController("headless",
        filepath + "/src/test/java/positiveTestcase.txt", model, view);
    controller.start();
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n" +
            "Processing command: create event test on \"2025-11-11\"\n" +
            "\n" +
            "Enter command or enter 'exit' to exit the calendar application.\n" +
            "Processing command: print events from \"2025-11-09\" to \"2025-11-25\"\n" +
            "Events:\n" +
            "• test - 2025-11-11T00:00 to 2025-11-12T00:00 \n" +
            "\n" +
            "Enter command or enter 'exit' to exit the calendar application.\n" +
            "Exiting application.\n",
        view.getResult());
  }

  @Test
  public void testGracefulExit() {
    controller = CalendarControllerFactory.getController("headless",
        filepath + "/src/test/java/SingleEventConflictCommand.txt",
        model, view);
    controller.start();
    assertEquals("Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: create event test1 from \"2025-11-12T12:00\" to "
        + "\"2025-11-12T13:00\"\n"
        + "\n"
        + "Enter command or enter 'exit' to exit the calendar application.\n"
        + "Processing command: create event test2 from \"2025-11-12T12:00\" to "
        + "\"2025-11-12T13:00\"\n"
        + "\n"
        + "ENCOUNTERED ERROR : create event test2 from \"2025-11-12T12:00\" to "
        + "\"2025-11-12T13:00\"\n"
        + "Reason : Event conflicts with existing event: test1\n"
        + "Exiting application gracefully.\n", view.getResult());
  }

  private static class MockView implements ICalendarView {
    private final StringBuilder resultBuilder = new StringBuilder();

    @Override
    public void displayMessage(String message) {
      resultBuilder.append(message);
    }

    public String getResult() {
      return resultBuilder.toString();
    }
  }
}
