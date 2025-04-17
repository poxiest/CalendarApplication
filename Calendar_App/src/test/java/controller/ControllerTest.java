package controller;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import calendar.ConflictException;
import model.IMultipleCalendarModel;
import model.MultipleCalendarModel;

/**
 * This is a junit test class that evaluates the implementation
 * of the controller for the calendar app.
 */
public class ControllerTest {

  IMultipleCalendarModel calendarModel;
  MockController mockController;
  StringBuffer out;

  @Before
  public void setup() {
    this.out = new StringBuffer();
    this.calendarModel = new MultipleCalendarModel();
  }

  @Test
  public void testValidCopyRange() throws IOException {
    Reader in = new StringReader(
        "copy events between 2025-03-11 and 2025-03-12 --target test to 2025-03-13\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals(
        "2025-03-11, 2025-03-12, test, 2025-03-13",
        actual
    );
  }

  @Test
  public void testValidCopyOnDate() throws IOException {
    Reader in = new StringReader(
        "copy events on 2025-03-11 --target default to 2025-03-12\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals(
        "2025-03-11, default, 2025-03-12",
        actual
    );
  }

  @Test
  public void testValidCopySingle() throws IOException {
    Reader in = new StringReader(
        "copy event test0 on 2025-03-11T10:00"
        + " --target default to 2025-03-11T11:00\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals(
        "test0, 2025-03-11T10:00, default, 2025-03-11T11:00",
        actual
    );
  }

  @Test
  public void testValidUseCalendar() throws IOException {
    Reader in = new StringReader(
        "use calendar --name test\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals(
        "test",
        actual
    );
  }

  @Test
  public void testValidCreateSingle() throws IOException {
    Reader in = new StringReader(
        "create event test0 from 2025-03-11T10:00 to 2025-03-11T11:00\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals(
        "test0, 2025-03-11T10:00, 2025-03-11T11:00",
        actual
    );
  }

  @Test
  public void testValidCreateRecurringSequence() throws IOException {
    Reader in = new StringReader(
        "create event test0 from 2025-03-11T10:00"
        + " to 2025-03-11T11:00 repeats MWU for 5 times\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals(
        "test0, 2025-03-11T10:00, 2025-03-11T11:00, "
        + "[MONDAY, WEDNESDAY, SUNDAY], 5",
        actual
    );
  }

  @Test
  public void testValidCreateRecurringUntil() throws IOException {
    Reader in = new StringReader(
        "create event test0 from 2025-03-11T10:00 to "
        + "2025-03-11T11:00 repeats MWU until 2025-04-11T11:00\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals(
        "test0, 2025-03-11T10:00, 2025-03-11T11:00, "
        + "[MONDAY, WEDNESDAY, SUNDAY], 2025-04-11T11:00",
        actual
    );
  }

  @Test
  public void testValidCreateSingleAllDay() throws IOException {
    Reader in = new StringReader(
        "create event test0 on 2025-03-11T11:00\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("test0, 2025-03-11", actual);
  }

  @Test
  public void testValidCreateRecurringSequenceAllDat() throws IOException {
    Reader in = new StringReader(
        "create event test0 on 2025-03-11 repeats MWU for 5 times\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("test0, 2025-03-11, [MONDAY, WEDNESDAY, SUNDAY], 5", actual);
  }


  @Test
  public void testValidCreateRecurringUntilAllDay() throws IOException {
    Reader in = new StringReader(
        "create event test0 on 2025-03-11 repeats MWU until 2025-04-11\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("test0, 2025-03-11, [MONDAY, WEDNESDAY, SUNDAY], 2025-04-11", actual);
  }

  @Test()
  public void testValidCreateCalendar() throws IOException {
    Reader in = new StringReader(
        "create calendar --name calendarName --timezone America/New_York\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("calendarName, America/New_York", actual);
  }

  @Test()
  public void testValidEditSingle() throws IOException {
    Reader in = new StringReader(
        "edit event subject test0 from 2025-03-11T10:00 to 2025-03-11T11:00 with test1\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("SUBJECT, test0, 2025-03-11T10:00, 2025-03-11T11:00, test1", actual);
  }

  @Test()
  public void testValidEditFrom() throws IOException {
    Reader in = new StringReader(
        "edit events subject test0 from 2025-03-11T10:00 with test1\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("SUBJECT, test0, 2025-03-11T10:00, test1", actual);
  }

  @Test()
  public void testValidEditAll() throws IOException {
    Reader in = new StringReader(
        "edit events subject test0 test1\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("SUBJECT, test0, test1",
        actual);
  }

  @Test
  public void testValidEditCalendar() throws IOException {
    Reader in = new StringReader(
        "edit calendar --name default --property name test\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("default, NAME, test",
        actual);
  }

  @Test(expected = IOException.class)
  public void testExceptionInvalidOrderAtPrintDate() throws IOException {
    Reader in = new StringReader("print events to from");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
  }

  @Test(expected = IOException.class)
  public void testExceptionNoDatePrintRange() throws IOException {
    Reader in = new StringReader("print events from to");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
  }

  @Test(expected = IOException.class)
  public void testExceptionNoPrintDate() throws IOException {
    Reader in = new StringReader("print events on");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
  }

  @Test(expected = IOException.class)
  public void testInvalidOrderAtPrintRange() throws IOException {
    Reader in = new StringReader(
        "print events to 2025-03-10T09:00 from 2025-03-11T10:00\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
  }

  @Test
  public void testValidInputAtPrintRange() throws IOException {
    Reader in = new StringReader(
        "print events from 2025-03-10T09:00 to 2025-03-11T10:00\nexit"
    );
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("2025-03-10T09:00, 2025-03-11T10:00",
        actual);
  }

  @Test(expected = IOException.class)
  public void testInvalidOrderAtPrintDate() throws IOException {
    Reader in = new StringReader("print on events 2025-03-10\nexit");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
  }

  @Test
  public void testValidInputAtPrintDate() throws IOException {
    Reader in = new StringReader("print events on 2025-03-10\nexit");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("2025-03-10", actual);
  }

  @Test(expected = IOException.class)
  public void testExceptionInvalidInputAtExport() throws IOException {
    Reader in = new StringReader("export fileName.csv\nexit");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
  }

  @Test(expected = IOException.class)
  public void testExceptionNoSuffixAtExport() throws IOException {
    Reader in = new StringReader("export cal fileName\nexit");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
  }

  @Test
  public void testCorrectInputAtExport() throws IOException {
    Reader in = new StringReader("export cal fileName.csv\nexit");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("fileName.csv", actual);
  }

  @Test(expected = IOException.class)
  public void testExceptionInvalidOrderShow() throws IOException {
    Reader in = new StringReader("show on status 2025-03-10T09:00\nexit");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
  }

  @Test(expected = IOException.class)
  public void testExceptionInvalidArgsShow() throws IOException {
    Reader in = new StringReader("show status on 2025-03-10T09:00 extra\nexit");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
  }

  @Test()
  public void testCorrectInputAtShow() throws IOException {
    Reader in = new StringReader("show status on 2025-03-10T09:00\nexit");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("2025-03-10T09:00", actual);
  }

  @Test(expected = IOException.class)
  public void testExceptionWithoutArgs() throws IOException {
    Reader in = new StringReader("exit");
    String[] args = {};
    new MockController(calendarModel, in,out, args);
  }

  @Test(expected = IOException.class)
  public void testExceptionInteractiveWithoutMode() throws IOException {
    Reader in = new StringReader("exit");
    String[] args = { "interactive" };
    new MockController(calendarModel, in, out, args);
  }

  @Test(expected = IOException.class)
  public void testExceptionHeadlessWithoutMode() throws IOException {
    Reader in = new StringReader("exit");
    String[] args = { "headless", "simpleCommands.txt" };
    new MockController(calendarModel, in, out, args);
  }

  @Test(expected = IOException.class)
  public void testExceptionHeadlessWithMissingFile() throws IOException {
    Reader in = new StringReader("exit");
    String[] args = { "--mode", "headless", "missing.txt" };
    new MockController(calendarModel, in, out, args);
  }

  @Test(expected = IOException.class)
  public void testExceptionHeadlessWithoutFile() throws IOException {
    Reader in = new StringReader("exit");
    String[] args = { "--mode", "headless" };
    new MockController(calendarModel, in, out, args);
  }

  @Test
  public void testValidInteractiveArgs() throws IOException {
    Reader in = new StringReader("exit");
    String[] args = { "--mode", "interactive" };
    new MockController(calendarModel, in, out, args);
    assertEquals("Now running calendar in interactive mode.\n", out.toString());
  }

  @Test
  public void testValidHeadlessArgs() throws IOException {
    Reader in = new StringReader("exit");
    String[] args = { "--mode", "headless", "res/validCommands.txt" };
    new MockController(calendarModel, in, out, args);
    assertEquals("Now running calendar in headless mode.\n", out.toString());
  }

  @Test
  public void testExport() throws ConflictException, IOException {
    Reader in = new StringReader("export cal fileName.csv\nexit");
    mockController = new MockController(
      calendarModel, 
      in,
      out,
      new String[] {"--mode", "interactive"}
    );
    LocalDateTime startDateTime = LocalDateTime.parse("2025-03-11T10:00");
    LocalDateTime endDateTime = LocalDateTime.parse("2025-03-11T11:00");
    calendarModel.getActiveCalendar().createSingleEvent(
        "test", 
        startDateTime, 
        endDateTime, 
        true
    );
    calendarModel.getActiveCalendar().createSingleEvent(
        "test1", 
        startDateTime.plusDays(1), 
        endDateTime.plusDays(1), 
        true
    );
    calendarModel.getActiveCalendar().createSingleEvent(
        "test2", 
        startDateTime.plusDays(2), 
        endDateTime.plusDays(2), 
        true
    );
    mockController.execute();
    String header = "Subject,Start Date,Start Time,End Date,";
    header += "End Time,All Day Event,Description,Location,Private";
    String line0 = "test,2025-03-11,10:00,2025-03-11,11:00,,,,";
    String line1 = "test1,2025-03-12,10:00,2025-03-12,11:00,,,,";
    String line2 = "test2,2025-03-13,10:00,2025-03-13,11:00,,,,";
    String fileName = out.toString().split("\n")[1];
    assertEquals("fileName.csv", fileName);
    String fileLocation = out.toString().split("\n")[2];
    FileReader reader = new FileReader(new File(fileLocation));
    Scanner scan = new Scanner(reader);
    assertEquals(header, scan.nextLine());
    assertEquals(line0, scan.nextLine());
    assertEquals(line1, scan.nextLine());
    assertEquals(line2, scan.nextLine());
    scan.close();
  }

  @Test
  public void testCorrectInputAtShowCalendar() throws IOException {
    Reader in = new StringReader("show calendar dashboard from 2025-03-10 to 2025-03-20\nexit");
    mockController = new MockController(
        calendarModel,
        in,
        out,
        new String[] {"--mode", "interactive"}
    );
    mockController.execute();
    String actual = out.toString().split("\n")[1];
    assertEquals("2025-03-10\t2025-03-20", actual);
  }

  @Test(expected = IOException.class)
  public void testCorrectInputAtShowCalendarInvalid() throws IOException {
    Reader in = new StringReader("show calendar dashboard from 2025-03-10 to\nexit");
    mockController = new MockController(
        calendarModel,
        in,
        out,
        new String[] {"--mode", "interactive"}
    );
    mockController.execute();
  }
  
}