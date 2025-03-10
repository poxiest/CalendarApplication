import org.junit.Before;
import org.junit.Test;

import java.util.List;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.InvalidCommandFileException;
import calendarapp.controller.impl.CalendarControllerFactory;
import calendarapp.model.calendar.CalendarApplication;
import calendarapp.model.calendar.ICalendarApplication;
import calendarapp.model.event.IEvent;
import calendarapp.view.ICalendarView;

import static org.junit.Assert.assertEquals;

public class HeadlessControllerTest {

  private ICalendarController controller;
  private ICalendarApplication model;
  private MockView view;

  @Before
  public void setUp() {
    model = new CalendarApplication();
    view = new MockView();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHeadless() {
    controller = CalendarControllerFactory.getController("headless", "",
        model, view);
  }

  @Test(expected = InvalidCommandFileException.class)
  public void testHeadless1() {
    controller = CalendarControllerFactory.getController("headless",
        "./CalendarApplication/src/test/resources/withoutExitCommand.txt",
        model, view);
    controller.go();
  }

  @Test
  public void testHeadless2() {
    controller = CalendarControllerFactory.getController("headless",
        "./CalendarApplication/src/test/resources/positiveTestcase.txt",
        model, view);
    controller.go();
    assertEquals("Name: test Start Time: 2025-11-11T00:00 End Time: 2025-11-11T23:59:59" +
            " Description: null Location: null Visibility: PRIVATE Recurring Days: null" +
            " Occurrence Count: null Recurrence End Date: null Auto Decline: false\n" +
            "Exiting Application",
        view.getResult());
  }

  private static class MockView implements ICalendarView {
    private final StringBuilder resultBuilder = new StringBuilder();

    @Override
    public void displayMessage(String message) {
      resultBuilder.append(message);
    }

    @Override
    public void displayEvents(List<IEvent> events) {
      for (IEvent event : events) {
        resultBuilder.append(event.toString());
      }
    }

    public String getResult() {
      return resultBuilder.toString();
    }
  }
}
