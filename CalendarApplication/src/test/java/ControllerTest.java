import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;
import java.util.Scanner;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.impl.AbstractCalendarController;
import calendarapp.model.impl.CalendarModel;
import calendarapp.model.ICalendarModel;
import calendarapp.model.IEvent;
import calendarapp.view.ICalendarView;

import static org.junit.Assert.assertEquals;

public class ControllerTest {
  private ICalendarController controller;
  private ICalendarModel model;
  private MockView view;

  @Before
  public void setUp() {
    model = new CalendarModel();
    view = new MockView();
  }

  @Test
  public void testInteractive() {
    controller = new MockController("create event abc on \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Name: abc Start Time: 2025-12-22T00:00 End Time: 2025-12-22T23:59:59" +
            " Description: null Location: null Visibility: PRIVATE Recurring Days: null" +
            " Occurrence Count: null Recurrence End Date: null Auto Decline: false\n",
        view.getResult());
  }

  @Test
  public void testInteractive1() {
    controller = new MockController("CREATE EVENT abc ON \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Name: abc Start Time: 2025-12-22T00:00 End Time: 2025-12-22T23:59:59" +
            " Description: null Location: null Visibility: PRIVATE Recurring Days: null" +
            " Occurrence Count: null Recurrence End Date: null Auto Decline: false\n",
        view.getResult());
  }

  @Test
  public void testInteractive2() {
    controller = new MockController("CREATE EVENT ABC ON \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Name: ABC Start Time: 2025-12-22T00:00 End Time: 2025-12-22T23:59:59" +
            " Description: null Location: null Visibility: PRIVATE Recurring Days: null" +
            " Occurrence Count: null Recurrence End Date: null Auto Decline: false\n",
        view.getResult());
  }

  @Test
  public void testInteractive3() {
    controller = new MockController("Create event \"Happy Event\" ON \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Name: Happy Event Start Time: 2025-12-22T00:00 End Time: 2025-12-22T23:59:59" +
            " Description: null Location: null Visibility: PRIVATE Recurring Days: null" +
            " Occurrence Count: null Recurrence End Date: null Auto Decline: false\n",
        view.getResult());
  }

  @Test
  public void testInteractive4() {
    controller = new MockController("create event abc on \"2025-12-22T10:00\"" +
        "\ncreate event cdb on \"2025-12-22T11:00\"\nprint events on \"2025-12-22\"",
        model, view);
    controller.go();
    assertEquals("Name: abc Start Time: 2025-12-22T00:00 End Time: 2025-12-22T23:59:59" +
            " Description: null Location: null Visibility: PRIVATE Recurring Days: null " +
            "Occurrence Count: null Recurrence End Date: null Auto Decline: false\n" +
            "Name: cdb Start Time: 2025-12-22T00:00 End Time: 2025-12-22T23:59:59 Description:" +
            " null Location: null Visibility: PRIVATE Recurring Days: null Occurrence Count: null" +
            " Recurrence End Date: null Auto Decline: false\n",
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

  private static class MockController extends AbstractCalendarController {
    public MockController(String input, ICalendarModel calendarApplication,
                          ICalendarView calendarView) {
      super(new StringReader(input), calendarApplication, calendarView);
    }

    @Override
    public void go() {
      Scanner scanner = new Scanner(in);
      while (scanner.hasNextLine()) {
        processCommand(scanner.nextLine());
      }
    }
  }
}
