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
import calendarapp.model.impl.CalendarModel;
import calendarapp.view.ICalendarView;
import calendarapp.view.impl.CLIView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * End 2 end test for the calendar application.
 */
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
    controller.start();
    assertEquals("Events:\n" +
            "• Standup - 2025-11-11T11:00 to 2025-11-11T12:00 \n",
        stringOutput.toString());
    setUp();
    controller = new MockController("create event Standup from 2025-11-11T11:00 " +
        "to 2025-11-11T12:00\n" +
        "print events on 2025-11-11", model, view);
    controller.start();
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
    controller.start();
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
    controller.start();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-12T11:00 \n",
        stringOutput.toString());
  }

  @Test
  public void SingleEventSpanTwoDays1() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-11T11:00\" to \"2025-11-12T11:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.start();
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
    controller.start();
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
    controller.start();
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
    controller.start();
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
    controller.start();
  }

  // Multiple events on same time
  @Test(expected = EventConflictException.class)
  public void MultipleEventsOnSameTimeAutoDecline2() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "create event --autoDecline \"Sprint Planning2\" from \"2025-11-11T10:00\" to" +
        " \"2025-11-11T11:15\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.start();
  }

  // Multiple events on same time
  @Test(expected = EventConflictException.class)
  public void MultipleEventsOnSameTimeAutoDecline3() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\"\n" +
        "create event --autoDecline \"Sprint Planning2\" from \"2025-11-11T11:30\" to" +
        " \"2025-11-11T12:30\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.start();
  }

  @Test
  public void AllDayEventTest() {
    controller = new MockController("create event abc on \"2025-12-22\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• abc - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        stringOutput.toString());
  }

  @Test
  public void AllDayEventTest2() {
    controller = new MockController("create event \"Sprint Planning\" on" +
        " \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        stringOutput.toString());
  }

  @Test
  public void AllDayEventTest3() {
    controller = new MockController("create event abc on \"2025-12-22\"\n" +
        "create event abc on \"2025-12-22T10:00\"\n" +
        "print events on \"2025-12-22\"", model, view);
    controller.start();
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
    controller.start();
    assertEquals("Events:\n" +
        "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T10:30 \n" +
        "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
        "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T10:30 \n", stringOutput.toString());
  }

  @Test
  public void RecurringEventsForNTimesEdit() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MTWRFSU\" for 3 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-20\"\n"
        + "edit events recurring_days \"Recurring Event\" \"MW\"\n"
        + "print events from \"2025-11-10\" to \"2025-11-20\""
        , model, view);
    controller.start();
    assertEquals("Events:\n"
        + "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T10:30 \n"
        + "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n"
        + "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T10:30 \n"
        + "Events:\n"
        + "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n"
        + "• Recurring Event - 2025-11-17T10:00 to 2025-11-17T10:30 \n"
        + "• Recurring Event - 2025-11-19T10:00 to 2025-11-19T10:30 \n", stringOutput.toString());
  }

  @Test
  public void RecurringEventsForNTimes_1() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats MTWRFSU for 3 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-20\"", model, view);
    controller.start();
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
    controller.start();
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
    controller.start();
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
    controller.start();
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
    controller.start();
    assertEquals("Events:\n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Sprint Planning - 2025-11-12T10:30 to 2025-11-12T11:00 \n" +
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
    controller.start();
  }

  @Test(expected = EventConflictException.class)
  public void RecurringEventsForNTimesConflict1() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-12T09:15\" to \"2025-11-12T10:15\"\n" +
        "create event \"Recurring Event\" from \"2025-11-11T10:00\" to \"2025-11-11T10:30\"" +
        " repeats \"MFW\" for 6 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.start();
  }

  @Test
  public void RecurringEventsUntil() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MTWRFSU\" until \"2025-11-13\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-20\"", model, view);
    controller.start();
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
    controller.start();
    assertEquals("Events:\n" +
            "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T10:30 \n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T10:30 \n" +
            "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n" +
            "• Recurring Event - 2025-11-15T10:00 to 2025-11-15T10:30 \n" +
            "• Recurring Event - 2025-11-16T10:00 to 2025-11-16T10:30 \n"
        , stringOutput.toString());
  }

  // Recurring on all days for 6 times
  @Test
  public void RecurringEventsUntil1EditUntil() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MTWRFSU\" until " +
        "\"2025-11-16T20:00\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"\n"
        + "edit events recurrence_end_date \"Recurring Event\" \"2025-11-20T20:00\"\n"
        + "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.start();
    assertEquals("Events:\n"
            + "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T10:30 \n"
            + "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n"
            + "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T10:30 \n"
            + "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n"
            + "• Recurring Event - 2025-11-15T10:00 to 2025-11-15T10:30 \n"
            + "• Recurring Event - 2025-11-16T10:00 to 2025-11-16T10:30 \n"
            + "Events:\n"
            + "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T10:30 \n"
            + "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n"
            + "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T10:30 \n"
            + "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n"
            + "• Recurring Event - 2025-11-15T10:00 to 2025-11-15T10:30 \n"
            + "• Recurring Event - 2025-11-16T10:00 to 2025-11-16T10:30 \n"
            + "• Recurring Event - 2025-11-17T10:00 to 2025-11-17T10:30 \n"
            + "• Recurring Event - 2025-11-18T10:00 to 2025-11-18T10:30 \n"
            + "• Recurring Event - 2025-11-19T10:00 to 2025-11-19T10:30 \n"
            + "• Recurring Event - 2025-11-20T10:00 to 2025-11-20T10:30 \n"
        , stringOutput.toString());
  }

  // Recurring on all days for 6 times
  @Test
  public void RecurringEventsUntil1EditFrom() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MTWRFSU\" until " +
        "\"2025-11-16T20:00\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"\n"
        + "edit events to \"Recurring Event\" \"2025-11-11T11:00\"\n"
        + "print events from \"2025-11-10\" to \"2025-11-30\"\n"
        + "edit events from \"Recurring Event\" \"2025-11-11T10:15\"\n"
        + "print events from \"2025-11-10\" to \"2025-11-30\""
        , model, view);
    controller.start();
    assertEquals("Events:\n"
            + "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T10:30 \n"
            + "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n"
            + "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T10:30 \n"
            + "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T10:30 \n"
            + "• Recurring Event - 2025-11-15T10:00 to 2025-11-15T10:30 \n"
            + "• Recurring Event - 2025-11-16T10:00 to 2025-11-16T10:30 \n"
            + "Events:\n"
            + "• Recurring Event - 2025-11-11T10:00 to 2025-11-11T11:00 \n"
            + "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T11:00 \n"
            + "• Recurring Event - 2025-11-13T10:00 to 2025-11-13T11:00 \n"
            + "• Recurring Event - 2025-11-14T10:00 to 2025-11-14T11:00 \n"
            + "• Recurring Event - 2025-11-15T10:00 to 2025-11-15T11:00 \n"
            + "• Recurring Event - 2025-11-16T10:00 to 2025-11-16T11:00 \n"
            + "Events:\n"
            + "• Recurring Event - 2025-11-11T10:15 to 2025-11-11T11:00 \n"
            + "• Recurring Event - 2025-11-12T10:15 to 2025-11-12T11:00 \n"
            + "• Recurring Event - 2025-11-13T10:15 to 2025-11-13T11:00 \n"
            + "• Recurring Event - 2025-11-14T10:15 to 2025-11-14T11:00 \n"
            + "• Recurring Event - 2025-11-15T10:15 to 2025-11-15T11:00 \n"
            + "• Recurring Event - 2025-11-16T10:15 to 2025-11-16T11:00 \n"
        , stringOutput.toString());
  }

  // Recurring on Monday, wednesday and friday
  @Test
  public void RecurringEventsUntil2() {
    controller = new MockController("create event \"Recurring Event\" from " +
        "\"2025-11-11T10:00\" to \"2025-11-11T10:30\" repeats \"MWF\" " +
        "until \"2025-11-24T20:00\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.start();
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
    controller.start();
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
    controller.start();
    assertEquals("Events:\n" +
            "• Recurring Event - 2025-11-12T10:00 to 2025-11-12T10:30 \n" +
            "• Sprint Planning - 2025-11-12T10:30 to 2025-11-12T11:00 \n" +
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
    controller.start();
  }

  @Test(expected = EventConflictException.class)
  public void RecurringEventsUntilConflict1() {
    controller = new MockController("create event \"Sprint Planning\" from " +
        "\"2025-11-12T09:15\" to \"2025-11-12T10:15\"\n" +
        "create event \"Recurring Event\" from \"2025-11-11T10:00\" to \"2025-11-11T10:30\" " +
        "repeats \"MFW\" until \"2025-11-24T20:00\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.start();
  }

  @Test
  public void AllDayRecurringEventsFor() {
    controller = new MockController("create event \"All Day Recurring\" on \"2025-11-12\"" +
        " repeats \"MWRT\" for 5 times\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.start();
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
    controller.start();
  }

  @Test
  public void AllDayRecurringEventsUntil() {
    controller = new MockController("create event \"All Day Recurring\" on \"2025-11-12\"" +
        " repeats \"MWRT\" until \"2025-11-19\"\n" +
        "print events from \"2025-11-10\" to \"2025-11-30\"", model, view);
    controller.start();
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
    controller.start();
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from " +
          "\"2025-11-12T10:15\"", model, view);
      controller.start();
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
      controller.start();
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
      controller.start();
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
      controller.start();
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
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" from \"2025-11-11T11:00\"" +
          " to \"2025-11-11T12:00\" repeats \"MTW\" for times\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate4_1() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from " +
          "\"2025-11-11T11:00\" to \"2025-11-11T12:00\" repeats \"MTW\" for d times", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" from \"2025-11-11T11:00\"" +
          " to \"2025-11-11T12:00\" repeats \"MTW\" for d times\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void RequiredFieldsMissingCreate5() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from " +
          "\"2025-11-11T11:00\" to \"2025-11-11T12:00\" repeats for 5 times", model, view);
      controller.start();
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
      controller.start();
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
          "\"2025-11-11T11:00\" to \"2025-11-11T12:00\" repeats until \"2025-11-11\""
          , model, view);
      controller.start();
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
      controller.start();
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
      controller.start();
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
      controller.start();
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
      controller.start();
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
      controller.start();
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
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" " +
          "on \"2025-11-11T11:00\" repeats until \"2025-11-11\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EndDateLessThanStartDate() {
    try {
      controller = new MockController("create event \"Sprint Planning\" " +
          "from \"2025-11-11T11:00\" to \"2025-11-11T10:00\"", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("create event \"Sprint Planning\" from \"2025-11-11T11:00\" to " +
          "\"2025-11-11T10:00\"\n" +
          "Reason : Event end time cannot be before start time", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test
  public void SameStartTimeAndEndTime() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T11:00\"\n" +
        "create event \"Sprint Planning2\" from \"2025-11-11T12:00\" to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T11:00 \n" +
            "• Sprint Planning2 - 2025-11-11T12:00 to 2025-11-11T12:00 \n",
        stringOutput.toString());
  }

  @Test
  public void CreateCommandWithLocation() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T11:00\" location \"Shillman Hall\"\n" +
        "create event \"Sprint Planning2\" from \"2025-11-11T12:00\" to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T11:00 - Location: Shillman Hall\n"
            + "• Sprint Planning2 - 2025-11-11T12:00 to 2025-11-11T12:00 \n",
        stringOutput.toString());
  }

  @Test
  public void CreateCommandWithLocationDescriptionVisibility() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T11:00\" location \"Shillman Hall\" description "
        + "\"Longer Desc\" visibility private\n" +
        "create event \"Sprint Planning2\" from \"2025-11-11T12:00\" to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"", model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T11:00 - Location: Shillman Hall\n" +
            "• Sprint Planning2 - 2025-11-11T12:00 to 2025-11-11T12:00 \n",
        stringOutput.toString());
  }

  @Test
  public void ShowCommand() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from \"2025-11-11T11:00\" to \"2025-11-11T12:00\" location \"Shillman Hall\"\n" +
        "create event \"Sprint Planning2\" from \"2025-11-11T12:00\" to \"2025-11-11T13:00\"\n" +
        "print events on \"2025-11-11\"\n" +
        "show status on \"2025-11-11T11:30\"\n" +
        "show status on \"2025-11-12T11:30\"\n" +
        "show status on \"2025-11-11T12:30\""
        , model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T12:00 - Location: Shillman Hall\n"
            + "• Sprint Planning2 - 2025-11-11T12:00 to 2025-11-11T13:00 \n" +
            "Status: Busy\n" +
            "Status: Available\n" +
            "Status: Busy\n",
        stringOutput.toString());
  }

  @Test
  public void ShowCommand1() {
    controller = new MockController("create event \"Sprint Planning\" " +
        "from 2025-11-11T11:00 to 2025-11-11T12:00 location \"Shillman Hall\"\n" +
        "create event \"Sprint Planning2\" from 2025-11-11T12:00 to 2025-11-11T13:00\n" +
        "print events on 2025-11-11\n" +
        "show status on 2025-11-11T11:30\n" +
        "show status on 2025-11-12T11:30\n" +
        "show status on 2025-11-11T12:30"
        , model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T12:00 - Location: Shillman Hall\n"
            + "• Sprint Planning2 - 2025-11-11T12:00 to 2025-11-11T13:00 \n" +
            "Status: Busy\n" +
            "Status: Available\n" +
            "Status: Busy\n",
        stringOutput.toString());
  }

  @Test(expected = InvalidCommandException.class)
  public void ShowCommandInvalid() {
    try {
      controller = new MockController("show status 2025-11-11T11:00", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("show status 2025-11-11T11:00\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void ShowCommandInvalid1() {
    try {
      controller = new MockController("show status on", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("show status on\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  // Edit just one event at a time
  @Test
  public void EditCommandWithLocation() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-11T11:00\" to \"2025-11-11T11:00\" location \"Shillman Hall\"\n" +
        "create event Sprint from \"2025-11-11T12:00\" to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"\n" +
        "edit event location \"Sprint Planning\" from \"2025-11-11T11:00\" to" +
        " \"2025-11-11T11:00\" with \"Richard Hall\" \n" +
        "edit event location Sprint from 2025-11-11T12:00 to" +
        " 2025-11-11T13:00 with \"Shillman Hall\" \n" +
        "print events on \"2025-11-11\"\n", model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T11:00 - Location: Shillman Hall\n"
            + "• Sprint - 2025-11-11T12:00 to 2025-11-11T12:00 \n" +
            "Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T11:00 - Location: Richard Hall\n" +
            "• Sprint - 2025-11-11T12:00 to 2025-11-11T12:00 - Location: Shillman Hall\n",
        stringOutput.toString());
  }

  // Edit multiple events property
  @Test
  public void EditCommandWithLocation1() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-11T11:00\" to \"2025-11-11T11:00\" location \"Shillman Hall\"\n" +
        "create event \"Sprint Planning2\" from \"2025-11-11T12:00\" to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"\n" +
        "edit event location \"Sprint Planning\" from \"2025-11-11T10:00\" to \"2025-11-11T11:00\""
        + " with \"Richard Hall\"\n" +
        "edit event location \"Sprint Planning2\" from \"2025-11-11T12:00\" to \"2025-11-11T12:00\""
        + " with \"Richard Hall\"\n" +
        "print events on \"2025-11-11\"\n", model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T11:00 - Location: Shillman Hall\n" +
            "• Sprint Planning2 - 2025-11-11T12:00 to 2025-11-11T12:00 \n" +
            "Events:\n" +
            "• Sprint Planning - 2025-11-11T11:00 to 2025-11-11T11:00 - Location: Richard Hall\n" +
            "• Sprint Planning2 - 2025-11-11T12:00 to 2025-11-11T12:00 - Location: Richard Hall\n",
        stringOutput.toString());
  }

  // Edit multiple events property
  @Test
  public void EditCommandWithLocation1_1() {
    controller = new MockController("create event Sprint from" +
        " \"2025-11-11T11:00\" to \"2025-11-11T11:00\" location Shillman\n" +
        "create event Sprint from \"2025-11-11T12:00\" to \"2025-11-11T12:00\"\n" +
        "print events on \"2025-11-11\"\n" +
        "edit event location Sprint from 2025-11-11T10:00 to 2025-11-11T11:00 with" +
        " Richard\n" +
        "print events on \"2025-11-11\"\n", model, view);
    controller.start();
    assertEquals("Events:\n"
            + "• Sprint - 2025-11-11T11:00 to 2025-11-11T11:00 - Location: Shillman\n"
            + "• Sprint - 2025-11-11T12:00 to 2025-11-11T12:00 \n"
            + "Events:\n"
            + "• Sprint - 2025-11-11T11:00 to 2025-11-11T11:00 - Location: Richard\n"
            + "• Sprint - 2025-11-11T12:00 to 2025-11-11T12:00 \n",
        stringOutput.toString());
  }

  @Test(expected = InvalidCommandException.class)
  public void EditCommandInvalid() {
    try {
      controller = new MockController("edit event \"Sprint Meeting\"", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("edit event \"Sprint Meeting\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EditCommandInvalid1() {
    try {
      controller = new MockController("edit event location \"Sprint Meeting\"", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("edit event location \"Sprint Meeting\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EditCommandInvalid2() {
    try {
      controller = new MockController("edit event location \"Sprint Meeting\" from " +
          "\"2025-11-11T10:00\" to with Shillman", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("edit event location \"Sprint Meeting\" from " +
          "\"2025-11-11T10:00\" to with Shillman\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EditCommandInvalid3() {
    try {
      controller = new MockController("edit event location \"Sprint Meeting\" from " +
          "\"2025-11-11T10:00\" to \"2025-11-11T11:00\" with", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("edit event location \"Sprint Meeting\" from " +
          "\"2025-11-11T10:00\" to \"2025-11-11T11:00\" with\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EditCommandInvalid4() {
    try {
      controller = new MockController("edit event location \"Sprint Meeting\" from " +
          "\"2025-11-11T10:00\" with Shillam", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("edit event location \"Sprint Meeting\" from " +
          "\"2025-11-11T10:00\" with Shillam\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EditCommandInvalid5() {
    try {
      controller = new MockController("edit events \"Sprint Meeting\" from " +
          "\"2025-11-11T10:00\" with Shillam", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("edit events \"Sprint Meeting\" from " +
          "\"2025-11-11T10:00\" with Shillam\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EditCommandInvalid6() {
    try {
      controller = new MockController("edit events location \"Sprint Meeting\" from " +
          "\"2025-11-11T10:00\" with", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("edit events location \"Sprint Meeting\" from " +
          "\"2025-11-11T10:00\" with\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EditCommandInvalid7() {
    try {
      controller = new MockController("edit events \"Sprint Meeting\" Shillam", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("edit events \"Sprint Meeting\" Shillam\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EditCommandInvalid8() {
    try {
      controller = new MockController("edit events location \"Sprint Meeting\"", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("edit events location \"Sprint Meeting\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void EditCommandInvalid9() {
    try {
      controller = new MockController("create event Sprint from" +
          " \"2025-11-11T11:00\" to \"2025-11-11T12:00\" location \"Shillman Hall\"\n" +
          "edit event to Sprint from 2025-11-11T11:00 to 2025-11-11T12:00 with 2025-11-11T10:00\n"
          , model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("edit event to Sprint from 2025-11-11T11:00 to " +
          "2025-11-11T12:00 with 2025-11-11T10:00\n" +
          "Reason : Event end time cannot be before start time", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void PrintCommandInvalid() {
    try {
      controller = new MockController("print events on", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("print events on\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void PrintCommandInvalid1() {
    try {
      controller = new MockController("print on 2025-11-11T11:00", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("print on 2025-11-11T11:00\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void PrintCommandInvalid2() {
    try {
      controller = new MockController("print events from \"2025-11-11\" to", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("print events from \"2025-11-11\" to\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void PrintCommandInvalid3() {
    try {
      controller = new MockController("print events \"2025-11-11\" to \"2025-11-12\"",
          model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("print events \"2025-11-11\" to \"2025-11-12\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void PrintCommandInvalid4() {
    try {
      controller = new MockController("print events from to \"2025-11-12\"", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("print events from to \"2025-11-12\"\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void ExportCommandInvalid4() {
    try {
      controller = new MockController("export cal", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("export cal\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void ExportCommandInvalid5() {
    try {
      controller = new MockController("export filename.csv", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("export filename.csv\n" +
          "Reason : Required fields are missing.\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test
  public void NoEventsPrint() {
    controller = new MockController("print events on \"2025-12-22\"", model, view);
    controller.start();
    assertEquals("No events found.\n", stringOutput.toString());
  }

  @Test
  public void exportCommand() {
    controller = new MockController("create event \"Sprint Planning\" from" +
        " \"2025-11-11T11:00\" to \"2025-11-11T11:00\" location \"Shillman Hall\"\n" +
        "create event \"Sprint Planning\" from \"2025-11-11T12:00\" to \"2025-11-11T12:00\"\n" +
        "export cal exportTest.csv", model, view);
    controller.start();
    assertTrue(stringOutput.toString().contains("/exportTest.csv"));
  }

  @Test(expected = InvalidCommandException.class)
  public void exportCommandInvalidFile() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from" +
          " \"2025-11-11T11:00\" to \"2025-11-11T11:00\" location \"Shillman Hall\"\n" +
          "create event \"Sprint Planning\" from \"2025-11-11T12:00\" to \"2025-11-11T12:00\"\n" +
          "export cal exportTest.xlsx", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("export cal exportTest.xlsx\n"
          + "Reason : Only CSV files are supported.", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void exportCommandInvalidFile1() {
    try {
      controller = new MockController("create event \"Sprint Planning\" from" +
          " \"2025-11-11T11:00\" to \"2025-11-11T11:00\" location \"Shillman Hall\"\n" +
          "create event \"Sprint Planning\" from \"2025-11-11T12:00\" to \"2025-11-11T12:00\"\n" +
          "export cal exportTest", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("export cal exportTest\n"
          + "Reason : Only CSV files are supported.", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void InvalidCommandName() {
    try {
      controller = new MockController("exrt filename.csv", model, view);
      controller.start();
    } catch (InvalidCommandException e) {
      assertEquals("Unknown command: exrt filename.csv\n", e.getMessage());
      throw new InvalidCommandException(e.getMessage());
    }
  }

  @Test
  public void testInteractive1() {
    controller = new MockController("CREATE EVENT abc ON \"2025-12-22T10:00\"" +
        "\nPRINT EVENTS ON \"2025-12-22\"", model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• abc - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        stringOutput.toString());
  }

  @Test
  public void testInteractive2() {
    controller = new MockController("CREATE EVENT ABC ON \"2025-12-22T10:00\"" +
        "\nPRINT EVENTS ON \"2025-12-22\"", model, view);
    controller.start();
    assertEquals("Events:\n" +
            "• ABC - 2025-12-22T00:00 to 2025-12-23T00:00 \n",
        stringOutput.toString());
  }

  // Create
  @Test
  public void testInteractive3() {
    controller = new MockController("Create event \"Happy Event\" ON \"2025-12-22T10:00\"" +
        "\nprint events on \"2025-12-22\"", model, view);
    controller.start();
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
    controller.start();
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
    public void displayEvents(List<String> events) {
      for (String event : events) {
        resultBuilder.append(event);
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
    public void start() {
      Scanner scanner = new Scanner(in);
      while (scanner.hasNextLine()) {
        processCommand(scanner.nextLine());
      }
    }
  }
}
