package calendarapp.model.event;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import calendarapp.model.IEvent;
import calendarapp.model.impl.CalendarExporter;
import calendarapp.model.impl.Event;
import calendarapp.model.impl.EventConstants;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link CalendarExporter}.
 */
public class CalendarExporterTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private File csvFile;
  private List<IEvent> events;

  @Before
  public void setUp() throws IOException {
    csvFile = tempFolder.newFile("test-calendar.csv");
    events = new ArrayList<>();
  }

  @After
  public void tearDown() {
    if (csvFile.exists()) {
      csvFile.delete();
    }
  }

  @Test
  public void testExportEmptyEventList() throws IOException {
    CalendarExporter.exportEventAsGoogleCalendarCsv(events, csvFile.getAbsolutePath());

    List<String> lines = Files.readAllLines(csvFile.toPath());

    assertEquals(1, lines.size());
    assertEquals(String.join(EventConstants.CsvFormat.DELIMITER,
        EventConstants.CsvHeaders.SUBJECT,
        EventConstants.CsvHeaders.START_DATE,
        EventConstants.CsvHeaders.START_TIME,
        EventConstants.CsvHeaders.END_DATE,
        EventConstants.CsvHeaders.END_TIME,
        EventConstants.CsvHeaders.ALL_DAY_EVENT,
        EventConstants.CsvHeaders.DESCRIPTION,
        EventConstants.CsvHeaders.LOCATION,
        EventConstants.CsvHeaders.PRIVATE), lines.get(0));
  }

  @Test
  public void testExportSingleEvent() throws IOException {
    IEvent event = Event.builder()
        .name("Test Meeting")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .description("Sample Description")
        .location("Sample Location")
        .build();
    events.add(event);

    CalendarExporter.exportEventAsGoogleCalendarCsv(events, csvFile.getAbsolutePath());

    List<String> lines = Files.readAllLines(csvFile.toPath());

    assertEquals(2, lines.size());

    String[] fields = lines.get(1).split(EventConstants.CsvFormat.DELIMITER, -1);
    assertEquals("\"Test Meeting\"", fields[0]);
    assertEquals("03/15/2025", fields[1]);
    assertEquals("10:00:00 AM", fields[2]);
    assertEquals("03/15/2025", fields[3]);
    assertEquals("11:00:00 AM", fields[4]);
    assertEquals(EventConstants.CsvFormat.FALSE_VALUE, fields[5]);
    assertEquals("\"Sample Description\"", fields[6]);
    assertEquals("\"Sample Location\"", fields[7]);
    assertEquals(EventConstants.CsvFormat.FALSE_VALUE, fields[8]);
  }

  @Test
  public void testExportAllDayEvent() throws IOException {
    IEvent allDayEvent = Event.builder()
        .name("All Day Event")
        .startTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .endTime(null)
        .build();
    events.add(allDayEvent);

    CalendarExporter.exportEventAsGoogleCalendarCsv(events, csvFile.getAbsolutePath());

    List<String> lines = Files.readAllLines(csvFile.toPath());

    String[] fields = lines.get(1).split(EventConstants.CsvFormat.DELIMITER, -1);
    assertEquals("\"All Day Event\"", fields[0]);
    assertEquals("03/10/2025", fields[1]);
    assertEquals("", fields[2]);
    assertEquals("03/11/2025", fields[3]);
    assertEquals("", fields[4]);
    assertEquals(EventConstants.CsvFormat.TRUE_VALUE, fields[5]);
  }

//  @Test
//  public void testExportPrivateEvent() throws IOException {
//    IEvent privateEvent = Event.builder()
//        .name("Private Event")
//        .startTime(LocalDateTime.of(2025, 4, 10, 14, 30))
//        .endTime(LocalDateTime.of(2025, 4, 10, 15, 30))
//        .visibility("private")
//        .build();
//    events.add(privateEvent);
//
//    CalendarExporter.exportEventAsGoogleCalendarCsv(events, csvFile.getAbsolutePath());
//
//    List<String> lines = Files.readAllLines(csvFile.toPath());
//
//    String[] fields = lines.get(1).split(EventConstants.CsvFormat.DELIMITER, -1);
//    assertEquals(EventConstants.CsvFormat.TRUE_VALUE, fields[8]);
//  }

//  @Test
//  public void testExportMultipleEvents() throws IOException {
//    IEvent event1 = Event.builder()
//        .name("Event 1")
//        .startTime(LocalDateTime.of(2025, 3, 10, 9, 0))
//        .endTime(LocalDateTime.of(2025, 3, 10, 10, 0))
//        .visibility("public")
//        .build();
//
//    IEvent event2 = Event.builder()
//        .name("Event 2")
//        .startTime(LocalDateTime.of(2025, 3, 10, 12, 0))
//        .endTime(LocalDateTime.of(2025, 3, 10, 13, 0))
//        .visibility("default")
//        .build();
//
//    IEvent event3 = Event.builder()
//        .name("Event 3")
//        .startTime(LocalDateTime.of(2025, 3, 10, 15, 0))
//        .endTime(LocalDateTime.of(2025, 3, 10, 15, 30))
//        .visibility("PRIVATE")
//        .build();
//
//    events.addAll(Arrays.asList(event1, event2, event3));
//
//    CalendarExporter.exportEventAsGoogleCalendarCsv(events, csvFile.getAbsolutePath());
//
//    List<String> lines = Files.readAllLines(csvFile.toPath());
//
//    assertEquals(4, lines.size());
//  }
//
//  @Test
//  public void testEscapeSpecialCharacters() throws IOException {
//    IEvent specialEvent = Event.builder()
//        .name("John's \"Important\" Meeting")
//        .startTime(LocalDateTime.of(2025, 3, 20, 15, 0))
//        .endTime(LocalDateTime.of(2025, 3, 20, 16, 0))
//        .description("Discuss \"Project X\"")
//        .location("Room \"42\"")
//        .visibility("default")
//        .build();
//    events.add(specialEvent);
//
//    CalendarExporter.exportEventAsGoogleCalendarCsv(events, csvFile.getAbsolutePath());
//
//    List<String> lines = Files.readAllLines(csvFile.toPath());
//
//    String[] fields = lines.get(1).split(EventConstants.CsvFormat.DELIMITER, -1);
//    // Double quotes should be escaped
//    assertEquals("\"John's \"\"Important\"\" Meeting\"", fields[0]);
//    // Double quotes should be escaped
//    assertEquals("\"Discuss \"\"Project X\"\"\"", fields[6]);
//    // Double quotes should be escaped
//    assertEquals("\"Room \"\"42\"\"\"", fields[7]);
//  }
}