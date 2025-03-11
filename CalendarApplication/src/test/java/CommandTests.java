import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.Scanner;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.impl.AbstractCalendarController;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.impl.CalendarModel;
import calendarapp.view.ICalendarView;

import static org.junit.Assert.assertEquals;

public class CommandTests {
  private ICalendarController controller;
  private ICalendarModel model;
  private MockView view;

  @Before
  public void setUp() {
    model = new CalendarModel();
    view = new MockView();
  }

  @Test
  public void AllDayEventTest() {
    controller = new MockController("create event abc on \"2025-12-22\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• abc - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        view.getResult());
  }

  @Test
  public void AllDayEventTest2() {
    controller = new MockController("create event abc on \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• abc - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        view.getResult());
  }

  @Test
  public void SingleDayEvent() {
    controller = new MockController("create event Standup from \"2025-11-11T11:00\" " +
        "to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Standup - 2025-11-11T11:00 to 2025-11-11T12:00 \n",
        view.getResult());
  }

  // Subject with space
  @Test
  public void SingleDayEvent1() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T12:00 \n",
        view.getResult());
  }

  // Single event span Two days
  // check this if it should display or not
  @Test
  public void SingleEventSpanTwoDays() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-11T11:00\" to \"2025-11-12T11:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-12T11:00 \n",
        view.getResult());
  }

  @Test
  public void SingleEventSpanTwoDays1() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-11T11:00\" to \"2025-11-12T11:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-12T11:00 \n",
        view.getResult());
  }


  // Multiple events on same time
  @Test
  public void MultipleEventsOnSameTime() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "create event \"Sprint Planning2\" from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T12:00 \n" +
            "• Sprint Planning2 - 2025-11-11T11:00 to 2025-11-11T12:00 \n",
        view.getResult());
  }

  // Multiple events on Different time same day
  @Test
  public void MultipleEventsOnDifferentTime() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "create event \"Sprint Planning2\" from \"2025-11-11T12:00\" to \"2025-11-11T01:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T12:00 \n" +
            "• Sprint Planning2 - 2025-11-11T12:00 to 2025-11-11T01:00 \n",
        view.getResult());
  }

  // Multiple events on same time
  @Test(expected = EventConflictException.class)
  public void MultipleEventsOnSameTimeAutoDecline() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "create event --autoDecline \"Sprint Planning2\" from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
  }


  // TODO
  @Test
  public void testInteractive1() {
    controller = new MockController("CREATE EVENT abc ON \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• abc - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        view.getResult());
  }

  @Test
  public void testInteractive2() {
    controller = new MockController("CREATE EVENT ABC ON \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• ABC - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        view.getResult());
  }

  // Create
  @Test
  public void testInteractive3() {
    controller = new MockController("Create event \"Happy Event\" ON \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Happy Event - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        view.getResult());
  }

  // Create two events
  @Test
  public void testInteractive4() {
    controller = new MockController("create event abc on \"2025-12-22T10:00\"" +
        "\ncreate event cdb on \"2025-12-22T11:00\"\nprint events on \"2025-12-22\"",
        model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• abc - 2025-12-22T00:00 to 2025-12-23T00:00 \n" +
            "• cdb - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        view.getResult());
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
