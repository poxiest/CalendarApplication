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

import calendarapp.model.EventVisibility;
import calendarapp.model.ICalendarExporter;
import calendarapp.model.IEvent;
import calendarapp.model.impl.Constants;
import calendarapp.model.impl.CsvCalendarExporter;
import calendarapp.model.impl.Event;

import static org.junit.Assert.assertEquals;

/**
 * Test class for ICalendarExporter and its implementation.
 */
public class CalendarExporterTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private File csvFile;
  private List<IEvent> events;
  private ICalendarExporter exporter;

  @Before
  public void setUp() throws IOException {
    csvFile = tempFolder.newFile("test-calendar.csv");
    events = new ArrayList<>();
    exporter = new CsvCalendarExporter();
  }

  @After
  public void tearDown() {
    if (csvFile.exists()) {
      csvFile.delete();
    }
  }

  @Test
  public void testExportEmptyEventList() throws IOException {
    exporter.export(events, csvFile.getAbsolutePath());
    List<String> lines = Files.readAllLines(csvFile.toPath());

    assertEquals(1, lines.size());
    assertEquals(String.join(Constants.CsvFormat.DELIMITER,
        Constants.CsvHeaders.SUBJECT,
        Constants.CsvHeaders.START_DATE,
        Constants.CsvHeaders.START_TIME,
        Constants.CsvHeaders.END_DATE,
        Constants.CsvHeaders.END_TIME,
        Constants.CsvHeaders.ALL_DAY_EVENT,
        Constants.CsvHeaders.DESCRIPTION,
        Constants.CsvHeaders.LOCATION,
        Constants.CsvHeaders.PRIVATE), lines.get(0));
  }

  @Test
  public void testExportSingleEvent() throws IOException {
    IEvent event = Event.builder()
        .name("Test Meeting")
        .startTime(LocalDateTime.of(2025, 3, 15, 10, 0))
        .endTime(LocalDateTime.of(2025, 3, 15, 11, 0))
        .description("Sample Description")
        .location("Sample Location")
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build();
    events.add(event);

    exporter.export(events, csvFile.getAbsolutePath());
    List<String> lines = Files.readAllLines(csvFile.toPath());

    assertEquals(2, lines.size());

    String[] fields = lines.get(1).split(Constants.CsvFormat.DELIMITER, -1);
    assertEquals("\"Test Meeting\"", fields[0]);
    assertEquals("03/15/2025", fields[1]);
    assertEquals("10:00:00 AM", fields[2]);
    assertEquals("03/15/2025", fields[3]);
    assertEquals("11:00:00 AM", fields[4]);
    assertEquals(Constants.CsvFormat.FALSE_VALUE, fields[5]);
    assertEquals("\"Sample Description\"", fields[6]);
    assertEquals("\"Sample Location\"", fields[7]);
    assertEquals(Constants.CsvFormat.FALSE_VALUE, fields[8]);
  }

  @Test
  public void testExportAllDayEvent() throws IOException {
    IEvent allDayEvent = Event.builder()
        .name("All Day Event")
        .startTime(LocalDateTime.of(2025, 3, 10, 0, 0))
        .endTime(LocalDateTime.of(2025, 3, 11, 0, 0))
        .visibility(String.valueOf(EventVisibility.DEFAULT))
        .build();
    events.add(allDayEvent);

    exporter.export(events, csvFile.getAbsolutePath());
    List<String> lines = Files.readAllLines(csvFile.toPath());

    String[] fields = lines.get(1).split(Constants.CsvFormat.DELIMITER, -1);
    assertEquals("\"All Day Event\"", fields[0]);
    assertEquals("03/10/2025", fields[1]);
    assertEquals("", fields[2]);
    assertEquals("03/11/2025", fields[3]);
    assertEquals("", fields[4]);
    assertEquals(Constants.CsvFormat.TRUE_VALUE, fields[5]);
  }

  @Test
  public void testExportPrivateEvent() throws IOException {
    IEvent privateEvent = Event.builder()
        .name("Private Event")
        .startTime(LocalDateTime.of(2025, 4, 10, 14, 30))
        .endTime(LocalDateTime.of(2025, 4, 10, 15, 30))
        .visibility(String.valueOf(EventVisibility.PRIVATE))
        .build();
    events.add(privateEvent);

    exporter.export(events, csvFile.getAbsolutePath());
    List<String> lines = Files.readAllLines(csvFile.toPath());

    String[] fields = lines.get(1).split(Constants.CsvFormat.DELIMITER, -1);
    assertEquals(Constants.CsvFormat.TRUE_VALUE, fields[8]);
  }

  @Test
  public void testExportMultipleEvents() throws IOException {
    IEvent event1 = Event.builder()
        .name("Event 1")
        .startTime(LocalDateTime.of(2025, 3, 10, 9, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 10, 0))
        .visibility(String.valueOf(EventVisibility.PUBLIC))
        .build();

    IEvent event2 = Event.builder()
        .name("Event 2")
        .startTime(LocalDateTime.of(2025, 3, 10, 12, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 13, 0))
        .visibility(String.valueOf(EventVisibility.DEFAULT))
        .build();

    IEvent event3 = Event.builder()
        .name("Event 3")
        .startTime(LocalDateTime.of(2025, 3, 10, 15, 0))
        .endTime(LocalDateTime.of(2025, 3, 10, 15, 30))
        .visibility(String.valueOf(EventVisibility.PRIVATE))
        .build();

    events.addAll(Arrays.asList(event1, event2, event3));

    exporter.export(events, csvFile.getAbsolutePath());
    List<String> lines = Files.readAllLines(csvFile.toPath());

    assertEquals(4, lines.size()); // 1 header + 3 events
  }

  @Test
  public void testEscapeSpecialCharacters() throws IOException {
    IEvent specialEvent = Event.builder()
        .name("John's \"Important\" Meeting")
        .startTime(LocalDateTime.of(2025, 3, 20, 15, 0))
        .endTime(LocalDateTime.of(2025, 3, 20, 16, 0))
        .description("Discuss \"Project X\"")
        .location("Room \"42\"")
        .visibility(String.valueOf(EventVisibility.DEFAULT))
        .build();
    events.add(specialEvent);

    exporter.export(events, csvFile.getAbsolutePath());
    List<String> lines = Files.readAllLines(csvFile.toPath());

    String[] fields = lines.get(1).split(Constants.CsvFormat.DELIMITER, -1);

    // Check that quotes are escaped correctly
    assertEquals("\"John's \"\"Important\"\" Meeting\"", fields[0]);
    assertEquals("\"Discuss \"\"Project X\"\"\"", fields[6]);
    assertEquals("\"Room \"\"42\"\"\"", fields[7]);
  }

}
