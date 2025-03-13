import org.junit.Before;
import org.junit.Test;

import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.impl.CalendarController;
import calendarapp.controller.impl.CalendarControllerFactory;
import calendarapp.model.ICalendarModel;
import calendarapp.view.ICalendarView;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link CalendarControllerFactory}.
 */
public class CalendarControllerFactoryTest {
  private ICalendarController controller;
  private ICalendarView view;
  private ICalendarModel model;

  private String filepath = System.getProperty("user.dir").contains("CalendarApplication") ?
      System.getProperty("user.dir") : System.getProperty("user.dir") + "/CalendarApplication";

  @Before
  public void setup() {
    view = new MockView();
    model = new MockCalendarModel();
  }

  @Test
  public void testControllerFactory() {
    controller = CalendarControllerFactory.getController("interactive",
        null, model, view);
    assertEquals(CalendarController.class, controller.getClass());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControllerFactory1() {
    controller = CalendarControllerFactory.getController("headless",
        null, model, view);
  }

  // File not found
  @Test(expected = IllegalArgumentException.class)
  public void testControllerFactory2() {
    controller = CalendarControllerFactory.getController("headless",
        "positiveTestcase.txt", model, view);
  }

  @Test
  public void testControllerFactory3() {
    controller = CalendarControllerFactory.getController("headless",
        filepath +
            "/src/test/resources/positiveTestcase.txt", model,
        view);
    assertEquals(CalendarController.class, controller.getClass());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControllerFactory4() {
    controller = CalendarControllerFactory.getController("unknown",
        null, model, view);
  }

  @Test
  public void testControllerFactory5() {
    controller = CalendarControllerFactory.getController("HEADLESS",
        filepath +
            "/src/test/resources/positiveTestcase.txt", model,
        view);
    assertEquals(CalendarController.class, controller.getClass());
  }

  @Test
  public void testControllerFactory6() {
    controller = CalendarControllerFactory.getController("INTERACTIVE",
        null, model,
        view);
    assertEquals(CalendarController.class, controller.getClass());
  }

  @Test(expected = IllegalArgumentException.class)
  public void IllegalTextFile() {
    try {
      controller = CalendarControllerFactory.getController("headless",
          "test", model,
          view);
    } catch (IllegalArgumentException e) {
      assertEquals("Only txt files are supported.", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void IllegalTextFile1() {
    try {
      controller = CalendarControllerFactory.getController("headless",
          "test.pdf", model,
          view);
    } catch (IllegalArgumentException e) {
      assertEquals("Only txt files are supported.", e.getMessage());
      throw e;
    }
  }

  private static class MockView implements ICalendarView {

    @Override
    public void displayMessage(String message) {
      System.out.println(message);
    }

    @Override
    public void displayEvents(List<String> events) {
      System.out.println(events);
    }
  }

  private static class MockCalendarModel implements ICalendarModel {

    @Override
    public void createEvent(String eventName, Temporal startTime, Temporal endTime,
                            String recurringDays, String occurrenceCount,
                            Temporal recurrenceEndDate, String description,
                            String location, String visibility, boolean autoDecline) {
      System.out.println("message");

    }

    @Override
    public void editEvent(String eventName, Temporal startTime, Temporal endTime, String property,
                          String value) {
      System.out.println("message");

    }

    @Override
    public List<String> getEventsForPrinting(Temporal startTime, Temporal endTime) {
      return List.of();
    }

    @Override
    public String export(String filename) {
      System.out.println("message");
      return filename;
    }

    @Override
    public String showStatus(Temporal dateTime) {
      return "";
    }
  }
}
