import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.impl.CalendarController;
import calendarapp.controller.impl.CalendarControllerFactory;
import calendarapp.model.ICalendarModel;
import calendarapp.model.dto.CalendarExporterDTO;
import calendarapp.model.dto.CopyEventRequestDTO;
import calendarapp.model.dto.EventsResponseDTO;
import calendarapp.view.ICalendarView;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link CalendarControllerFactory}.
 */
public class CalendarControllerFactoryTest {
  private ICalendarController controller;
  private ICalendarView view;
  private ICalendarModel model;

  private final String filepath = System.getProperty("user.dir").contains("CalendarApplication")
      ? System.getProperty("user.dir") : System.getProperty("user.dir") + File.separator +
      "CalendarApplication";

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
        filepath + File.separator + ("src") + File.separator + "test" + File.separator + "java" + File.separator + "positiveTestcase.txt", model,
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
        filepath + File.separator + ("src") + File.separator + "test" + File.separator + "java" + File.separator + "positiveTestcase.txt", model,
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
      assertEquals("Filename has no extension.", e.getMessage());
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

  @Test(expected = IllegalArgumentException.class)
  public void IllegalTextFile2() {
    try {
      controller = CalendarControllerFactory.getController("headless",
          ".", model,
          view);
    } catch (IllegalArgumentException e) {
      assertEquals("Filename has no extension.", e.getMessage());
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void IllegalTextFile3() {
    try {
      controller = CalendarControllerFactory.getController("headless",
          "null", model,
          view);
    } catch (IllegalArgumentException e) {
      assertEquals("Filename has no extension.", e.getMessage());
      throw e;
    }
  }

  private static class MockView implements ICalendarView {
    @Override
    public void displayMessage(String message) {
      System.out.println(message);
    }
  }

  private static class MockCalendarModel implements ICalendarModel {

    @Override
    public void createEvent(String eventName, String startTime, String endTime,
                            String recurringDays, String occurrenceCount,
                            String recurrenceEndDate, String description,
                            String location, String visibility, boolean autoDecline) {
      System.out.println("message");

    }

    @Override
    public void editEvent(String eventName, String startTime, String endTime, String property,
                          String value) {
      System.out.println("message");

    }

    @Override
    public List<EventsResponseDTO> getEvents(String eventName, String startTime, String endTime,
                                             String on) {
      return List.of();
    }

    @Override
    public List<CalendarExporterDTO> getEventsForExport() {
      return new ArrayList<>();
    }

    @Override
    public String showStatus(String dateTime) {
      return "";
    }

    @Override
    public void createCalendar(String calendarName, String timezone) {
      // empty for test purposes.
    }

    @Override
    public void editCalendar(String calendarName, String propertyName, String propertyValue) {
      // empty for test purposes.
    }

    @Override
    public void setCalendar(String calendarName) {
      // empty for test purposes.
    }

    @Override
    public void copyEvent(CopyEventRequestDTO copyEventRequestDTO) {
      // empty for test purposes.
    }

    @Override
    public List<String> getCalendars() {
      return List.of();
    }
  }
}
