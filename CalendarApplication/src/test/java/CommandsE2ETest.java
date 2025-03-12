import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;
import java.util.Scanner;

import calendarapp.controller.ICalendarController;
import calendarapp.controller.InvalidCommandException;
import calendarapp.controller.impl.AbstractCalendarController;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendarModel;
import calendarapp.model.IEvent;
import calendarapp.model.impl.CalendarModel;
import calendarapp.view.ICalendarView;
import calendarapp.view.impl.CLIView;

import static org.junit.Assert.assertEquals;

public class CommandsE2ETest {
  private ICalendarController controller;
  private ICalendarModel model;
  private ICalendarView view;
  private MockView viewMock;
  private StringBuilder stringOutput;

  @Before
  public void setUp() {
    stringOutput = new StringBuilder();
    model = new CalendarModel();
    view = new CLIView(stringOutput);
  }

  @Test
  public void SingleDayEvent() {
    controller = new MockController("create event Standup from \"2025-11-11T11:00\" " +
        "to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Standup - 2025-11-11T11:00 to 2025-11-11T12:00 \n",
        stringOutput.toString());
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
        stringOutput.toString());
  }

  // Single event span Two days
  @Test
  public void SingleEventSpanTwoDays() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-11T11:00\" to \"2025-11-12T11:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-12T11:00 \n",
        stringOutput.toString());
  }

  @Test
  public void SingleEventSpanTwoDays1() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-11T11:00\" to \"2025-11-12T11:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-12T11:00 \n",
        stringOutput.toString());
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
        stringOutput.toString());
  }

  // Multiple events on Different time same day
  @Test
  public void MultipleEventsOnDifferentTime() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "create event \"Sprint Planning2\" from \"2025-11-11T12:00\" to \"2025-11-11T13:00\"\n" +
        "create event \"Sprint Planning3\" from \"2025-11-11T13:00\" to \"2025-11-11T14:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T12:00 \n" +
            "• Sprint Planning2 - 2025-11-11T12:00 to 2025-11-11T13:00 \n" +
            "• Sprint Planning3 - 2025-11-11T13:00 to 2025-11-11T14:00 \n",
        stringOutput.toString());
  }

  // Multiple events on Different time same day - with autodecline
  @Test
  public void MultipleEventsOnDifferentTime1() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "create event --autoDecline \"Sprint Planning2\" from \"2025-11-11T12:00\" to" +
        " \"2025-11-11T13:00\"\n" +
        "create event --autoDecline \"Sprint Planning3\" from \"2025-11-11T13:00\" to" +
        " \"2025-11-11T14:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T12:00 \n" +
            "• Sprint Planning2 - 2025-11-11T12:00 to 2025-11-11T13:00 \n" +
            "• Sprint Planning3 - 2025-11-11T13:00 to 2025-11-11T14:00 \n",
        stringOutput.toString());
  }

  // Multiple events on same time
  @Test(expected = EventConflictException.class)
  public void MultipleEventsOnSameTimeAutoDecline() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "create event --autoDecline \"Sprint Planning2\" from \"2025-11-11T11:00\" to" +
        " \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
  }

  // Multiple events on same time
  @Test(expected = EventConflictException.class)
  public void MultipleEventsOnSameTimeAutoDecline2() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "create event --autoDecline \"Sprint Planning2\" from \"2025-11-11T10:00\" to" +
        " \"2025-11-11T11:15\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
  }

  // Multiple events on same time
  @Test(expected = EventConflictException.class)
  public void MultipleEventsOnSameTimeAutoDecline3() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "create event --autoDecline \"Sprint Planning2\" from \"2025-11-11T11:30\" to" +
        " \"2025-11-11T12:30\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.go();
  }

  @Test
  public void AllDayEventTest() {
    controller = new MockController("create event abc on \"2025-12-22\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• abc - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        stringOutput.toString());
  }

  @Test
  public void AllDayEventTest2() {
    controller = new MockController("create event \"Sprint Planning\" on" +
        " \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        stringOutput.toString());
  }

  @Test
  public void AllDayEventTest3() {
    controller = new MockController("create event abc on \"2025-12-22T10:00\"\n" +
        "create event abc on \"2025-12-22T10:00\"\n" +
        "print events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• abc - 2025-12-22T00:00 to 2025-12-23T00:00 \n" +
            "• abc - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        stringOutput.toString());
  }

  @Test
  public void RecurringEventsForNTimes() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MTWRFSU\" for 3 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-20\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
        "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T10:30 \n" +
        "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
        "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T10:30 \n", stringOutput.toString());
  }

  // Recurring on all days for 6 times
  @Test
  public void RecurringEventsForNTimes1() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MTWRFSU\" for 6 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-20\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T10:30 \n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T10:30 \n" +
            "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n" +
            "• Recurring Event - 2025-11-15T10:00 to 2025-11-15T10:30 \n" +
            "• Recurring Event - 2025-11-16T10:00 to 2025-11-16T10:30 \n"
        , stringOutput.toString());
  }

  // Recurring on Monday, wednesday and friday
  @Test
  public void RecurringEventsForNTimes2() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MWF\" for 6 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n" +
            "• Recurring Event - 2025-11-17T10:00 to 2025-11-17T10:30 \n" +
            "• Recurring Event - 2025-11-19T10:00 to 2025-11-19T10:30 \n" +
            "• Recurring Event - 2025-11-21T10:00 to 2025-11-21T10:30 \n" +
            "• Recurring Event - 2025-11-24T10:00 to 2025-11-24T10:30 \n"
        , stringOutput.toString());
  }

  @Test
  public void RecurringEventsForNTimesWithExistingEvent() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-12T09:00\" to \"2025-11-12T10:00\"\n" +
        "create event \"Recurring Event\" from \"2025-11-11T10:00\" to \"2025-11-11T10:30\"" +
        " repeats \"MFW\" for 6 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-12T09:00 to 2025-11-12T10:00 \n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n" +
            "• Recurring Event - 2025-11-17T10:00 to 2025-11-17T10:30 \n" +
            "• Recurring Event - 2025-11-19T10:00 to 2025-11-19T10:30 \n" +
            "• Recurring Event - 2025-11-21T10:00 to 2025-11-21T10:30 \n" +
            "• Recurring Event - 2025-11-24T10:00 to 2025-11-24T10:30 \n"
        , stringOutput.toString());
  }

  @Test
  public void RecurringEventsForNTimesWithExistingEvent1() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-12T10:30\" to \"2025-11-12T11:00\"\n" +
        "create event \"Recurring Event\" from \"2025-11-11T10:00\" to \"2025-11-11T10:30\"" +
        " repeats \"MFW\" for 6 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-12T10:30 to 2025-11-12T11:00 \n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n" +
            "• Recurring Event - 2025-11-17T10:00 to 2025-11-17T10:30 \n" +
            "• Recurring Event - 2025-11-19T10:00 to 2025-11-19T10:30 \n" +
            "• Recurring Event - 2025-11-21T10:00 to 2025-11-21T10:30 \n" +
            "• Recurring Event - 2025-11-24T10:00 to 2025-11-24T10:30 \n"
        , stringOutput.toString());
  }

  @Test(expected = EventConflictException.class)
  public void RecurringEventsForNTimesConflict() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-12T10:15\" to \"2025-11-12T11:00\"\n" +
        "create event \"Recurring Event\" from \"2025-11-11T10:00\" to \"2025-11-11T10:30\"" +
        " repeats \"MFW\" for 6 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
  }

  @Test(expected = EventConflictException.class)
  public void RecurringEventsForNTimesConflict1() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-12T09:15\" to \"2025-11-12T10:15\"\n" +
        "create event \"Recurring Event\" from \"2025-11-11T10:00\" to \"2025-11-11T10:30\"" +
        " repeats \"MFW\" for 6 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
  }

  @Test
  public void RecurringEventsUntil() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MTWRFSU\" until \"2025-11-13\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-20\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
        "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T10:30 \n" +
        "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
        "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T10:30 \n", stringOutput.toString());
  }

  // Recurring on all days for 6 times
  @Test
  public void RecurringEventsUntil1() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MTWRFSU\" until " +
        "\"2025-11-16T20:00\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-20\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T10:30 \n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T10:30 \n" +
            "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n" +
            "• Recurring Event - 2025-11-15T10:00 to 2025-11-15T10:30 \n" +
            "• Recurring Event - 2025-11-16T10:00 to 2025-11-16T10:30 \n"
        , stringOutput.toString());
  }

  // Recurring on Monday, wednesday and friday
  @Test
  public void RecurringEventsUntil2() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MWF\" " +
        "until \"2025-11-24T20:00\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n" +
            "• Recurring Event - 2025-11-17T10:00 to 2025-11-17T10:30 \n" +
            "• Recurring Event - 2025-11-19T10:00 to 2025-11-19T10:30 \n" +
            "• Recurring Event - 2025-11-21T10:00 to 2025-11-21T10:30 \n" +
            "• Recurring Event - 2025-11-24T10:00 to 2025-11-24T10:30 \n"
        , stringOutput.toString());
  }

  @Test
  public void RecurringEventsUntilWithExistingEvent() {
    controller = new MockController("create event \"Sprint Planning\" from " +
        "\"2025-11-12T09:00\" to \"2025-11-12T10:00\"\n" +
        "create event \"Recurring Event\" from \"2025-11-11T10:00\" to \"2025-11-11T10:30\" " +
        "repeats \"MFW\" until \"2025-11-24T20:00\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-12T09:00 to 2025-11-12T10:00 \n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n" +
            "• Recurring Event - 2025-11-17T10:00 to 2025-11-17T10:30 \n" +
            "• Recurring Event - 2025-11-19T10:00 to 2025-11-19T10:30 \n" +
            "• Recurring Event - 2025-11-21T10:00 to 2025-11-21T10:30 \n" +
            "• Recurring Event - 2025-11-24T10:00 to 2025-11-24T10:30 \n"
        , stringOutput.toString());
  }

  @Test
  public void RecurringEventsUntilWithExistingEvent1() {
    controller = new MockController("create event \"Sprint Planning\" from " +
        "\"2025-11-12T10:30\" to \"2025-11-12T11:00\"\n" +
        "create event \"Recurring Event\" from \"2025-11-11T10:00\" to \"2025-11-11T10:30\" " +
        "repeats \"MFW\" until \"2025-11-24T20:00\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-12T10:30 to 2025-11-12T11:00 \n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n" +
            "• Recurring Event - 2025-11-17T10:00 to 2025-11-17T10:30 \n" +
            "• Recurring Event - 2025-11-19T10:00 to 2025-11-19T10:30 \n" +
            "• Recurring Event - 2025-11-21T10:00 to 2025-11-21T10:30 \n" +
            "• Recurring Event - 2025-11-24T10:00 to 2025-11-24T10:30 \n"
        , stringOutput.toString());
  }

  @Test(expected = EventConflictException.class)
  public void RecurringEventsUntilConflict() {
    controller = new MockController("create event \"Sprint Planning\" from " +
        "\"2025-11-12T10:15\" to \"2025-11-12T11:00\"\n" +
        "create event \"Recurring Event\" from \"2025-11-11T10:00\" to \"2025-11-11T10:30\" " +
        "repeats \"MFW\" until \"2025-11-24T20:00\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
  }

  @Test(expected = EventConflictException.class)
  public void RecurringEventsUntilConflict1() {
    controller = new MockController("create event \"Sprint Planning\" from " +
        "\"2025-11-12T09:15\" to \"2025-11-12T10:15\"\n" +
        "create event \"Recurring Event\" from \"2025-11-11T10:00\" to \"2025-11-11T10:30\" " +
        "repeats \"MFW\" until \"2025-11-24T20:00\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
  }

  @Test
  public void AllDayRecurringEventsFor() {
    controller = new MockController("create event \"All Day Recurring\" on \"2025-11-12\"" +
        " repeats \"MWRT\" for 5 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
        "• All Day Recurring - 2025-11-12T00:00 to 2025-11-13T00:00 \n" +
        "• All Day Recurring - 2025-11-13T00:00 to 2025-11-14T00:00 \n" +
        "• All Day Recurring - 2025-11-17T00:00 to 2025-11-18T00:00 \n" +
        "• All Day Recurring - 2025-11-18T00:00 to 2025-11-19T00:00 \n" +
        "• All Day Recurring - 2025-11-19T00:00 to 2025-11-20T00:00 \n", stringOutput.toString());
  }

  @Test(expected = EventConflictException.class)
  public void AllDayRecurringEventsFor1() {
    controller = new MockController("create event \"Sprint Planning\" from " +
        "\"2025-11-12T10:15\" to \"2025-11-12T11:00\"\n" +
        "create event \"All Day Recurring\" on \"2025-11-12\" repeats \"MWRU\" for 3 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
  }

  @Test
  public void AllDayRecurringEventsUntil() {
    controller = new MockController("create event \"All Day Recurring\" on \"2025-11-12\"" +
        " repeats \"MWRT\" until \"2025-11-19\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
        "• All Day Recurring - 2025-11-12T00:00 to 2025-11-13T00:00 \n" +
        "• All Day Recurring - 2025-11-13T00:00 to 2025-11-14T00:00 \n" +
        "• All Day Recurring - 2025-11-17T00:00 to 2025-11-18T00:00 \n" +
        "• All Day Recurring - 2025-11-18T00:00 to 2025-11-19T00:00 \n" +
        "• All Day Recurring - 2025-11-19T00:00 to 2025-11-20T00:00 \n", stringOutput.toString());
  }

  @Test(expected = EventConflictException.class)
  public void AllDayRecurringEventsUntil1() {
    controller = new MockController("create event \"Sprint Planning\" from " +
        "\"2025-11-12T10:15\" to \"2025-11-12T11:00\"\n" +
        "create event \"All Day Recurring\" on \"2025-11-12\" repeats \"MWRU\" " +
        "until \"2025-11-19\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.go();
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from " +
          "\"2025-11-12T10:15\"", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" from \"2025-11-12T10:15\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate1() {
    try {
      controller = new MockController("create event from " +
          "\"2025-11-12T10:15\" to \"2025-11-12T11:15\"", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event from \"2025-11-12T10:15\" to \"2025-11-12T11:15\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate2() {
    try {
      controller = new MockController("create event \"Sprint Planning\"", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate3() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from " +
          "\"2025-11-11T11:00\" to \"2025-11-11T12:00\" repeats \"MTW\" for", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" from \"2025-11-11T11:00\"" +
          " to \"2025-11-11T12:00\" repeats \"MTW\" for\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate4() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from " +
          "\"2025-11-11T11:00\" to \"2025-11-11T12:00\" repeats \"MTW\" for times", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" from \"2025-11-11T11:00\"" +
          " to \"2025-11-11T12:00\" repeats \"MTW\" for times\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate5() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from " +
          "\"2025-11-11T11:00\" to \"2025-11-11T12:00\" repeats for 5 times", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" from \"2025-11-11T11:00\"" +
          " to \"2025-11-11T12:00\" repeats for 5 times\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate6() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from " +
          "\"2025-11-11T11:00\" to \"2025-11-11T12:00\" repeats \"MTW\" until", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" from \"2025-11-11T11:00\"" +
          " to \"2025-11-11T12:00\" repeats \"MTW\" until\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate7() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from " +
          "\"2025-11-11T11:00\" to \"2025-11-11T12:00\" repeats until \"2025-11-11\"", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" from \"2025-11-11T11:00\"" +
          " to \"2025-11-11T12:00\" repeats until \"2025-11-11\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate8() {
    try {
      controller = new MockController("create event \"Sprint Planning\" on", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" on\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate9() {
    try {
      controller = new MockController("create event \"Sprint Planning\" on " +
          "\"2025-11-11T11:00\" repeats \"MTW\" for", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" on \"2025-11-11T11:00\" " +
          "repeats \"MTW\" for\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate10() {
    try {
      controller = new MockController("create event \"Sprint Planning\" on " +
          "\"2025-11-11T11:00\" repeats \"MTW\" for times", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" on \"2025-11-11T11:00\" " +
          "repeats \"MTW\" for times\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate11() {
    try {
      controller = new MockController("create event \"Sprint Planning\" " +
          "on \"2025-11-11T11:00\" repeats for 5 times", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" on \"2025-11-11T11:00\"" +
          " repeats for 5 times\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate12() {
    try {
      controller = new MockController("create event \"Sprint Planning\" " +
          "on \"2025-11-11T11:00\" repeats \"MTW\" until", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" on \"2025-11-11T11:00\" " +
          "repeats \"MTW\" until\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate13() {
    try {
      controller = new MockController("create event \"Sprint Planning\" " +
          "on \"2025-11-11T11:00\" repeats until \"2025-11-11\"", model, view);
      controller.go();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" " +
          "on \"2025-11-11T11:00\" repeats until \"2025-11-11\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }


  // TODO
  @Test
  public void testInteractive1() {
    controller = new MockController("CREATE EVENT abc ON \"2025-12-22T10:00\"" +
        "\nPRINT EVENTS ON \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• abc - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        stringOutput.toString());
  }

  @Test
  public void testInteractive2() {
    controller = new MockController("CREATE EVENT ABC ON \"2025-12-22T10:00\"" +
        "\nPRINT EVENTS ON \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• ABC - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        stringOutput.toString());
  }

  // Create
  @Test
  public void testInteractive3() {
    controller = new MockController("Create event \"Happy Event\" ON \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.go();
    assertEquals("Events:\n" +
            "• Happy Event - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        stringOutput.toString());
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
        stringOutput.toString());
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
