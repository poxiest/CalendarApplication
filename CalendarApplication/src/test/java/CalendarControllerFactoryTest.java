import org.junit.Before;
import org.junit.Test;

import java.time.temporal.Temporal;
import java.util.List;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.impl.CalendarControllerFactory;
import calendarapp.controller.impl.HeadlessCalendarController;
import calendarapp.controller.impl.InteractiveCalendarController;
import calendarapp.model.ICalendarModel;
import calendarapp.model.IEvent;
import calendarapp.view.ICalendarView;

import static org.junit.Assert.assertEquals;

public class CalendarControllerFactoryTest {
  private ICalendarController controller;
  private ICalendarView view;
  private ICalendarModel model;

  @Before
  public void setup() {
    view = new MockView();
    model = new MockCalendarModel();
  }

  @Test
  public void testControllerFactory() {
    controller = CalendarControllerFactory.getController("interactive",
        null, model, view);
    assertEquals(InteractiveCalendarController.class, controller.getClass());
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
        "/Users/vishaakroot/NEU/pdp/pdp_assignments/group/CalendarApplication" +
            "/src/test/resources/positiveTestcase.txt", model,
        view);
    assertEquals(HeadlessCalendarController.class, controller.getClass());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControllerFactory4() {
    controller = CalendarControllerFactory.getController("unknown",
        null, model, view);
  }

  @Test
  public void testControllerFactory5() {
    controller = CalendarControllerFactory.getController("HEADLESS",
        "/Users/vishaakroot/NEU/pdp/pdp_assignments/group/CalendarApplication" +
            "/src/test/resources/positiveTestcase.txt", model,
        view);
    assertEquals(HeadlessCalendarController.class, controller.getClass());
  }

  @Test
  public void testControllerFactory6() {
    controller = CalendarControllerFactory.getController("INTERACTIVE",
        null, model,
        view);
    assertEquals(InteractiveCalendarController.class, controller.getClass());
  }

  private static class MockView implements ICalendarView {

    @Override
    public void displayMessage(String message) {
      System.out.println(message);
    }

    @Override
    public void displayEvents(List<IEvent> events) {
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
    public List<IEvent> printEvents(Temporal startTime, Temporal endTime) {
      return List.of();
    }

    @Override
    public void export(String filename) {
      System.out.println("message");
    }

    @Override
    public String showStatus(Temporal dateTime) {
      return "";
    }
  }
}
