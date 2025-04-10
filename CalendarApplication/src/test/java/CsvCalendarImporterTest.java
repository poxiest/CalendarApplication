import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import calendarapp.controller.importer.CsvCalendarImporter;
import calendarapp.model.dto.CalendarImporterDTO;

/**
 * Test case for CSV Calendar Importer
 */
public class CsvCalendarImporterTest {

  private CsvCalendarImporter importer;
  private String testFilePath;

  @Before
  public void setUp() {
    importer = new CsvCalendarImporter();
    testFilePath = "test_calendar.csv";
  }

  @After
  public void tearDown() {
    File testFile = new File(testFilePath);
    if (testFile.exists()) {
      testFile.delete();
    }
  }

  @Test
  public void testQuoteAtEndOfLine() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Event,05/24/2023,10:00:00 AM,05/24/2023,11:00:00 AM,FALSE,\"Text ending with quote character: \",Location1,FALSE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);
    assertEquals("Event", event.getEventName());
    assertEquals("Text ending with quote character: ", event.getDescription());
  }

  @Test
  public void testImportSingleEvent() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Event1,05/15/2023,09:00:00 AM,05/15/2023,10:00:00 AM,FALSE,Some Description,Location1,FALSE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);

    assertEquals("Event1", event.getEventName());
    assertEquals("2023-05-15T09:00", event.getStartTime());
    assertEquals("2023-05-15T10:00", event.getEndTime());
    assertEquals("Some Description", event.getDescription());
    assertEquals("Location1", event.getLocation());
    assertEquals("public", event.getVisibility());
  }

  @Test
  public void testImportMultipleEvents() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Event1,05/15/2023,09:00:00 AM,05/15/2023,10:00:00 AM,FALSE,Description1,Location1,FALSE\n" +
            "Event2,05/15/2023,12:00:00 PM,05/15/2023,01:00:00 PM,FALSE,Description2,Location2,TRUE\n" +
            "Event3,05/16/2023,02:00:00 PM,05/16/2023,03:30:00 PM,FALSE,Description3,Location3,TRUE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(3, events.size());
  }

  @Test
  public void testPrivateEvent() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "PrivateEvent,05/20/2023,02:00:00 PM,05/20/2023,03:00:00 PM,FALSE,Private Description,Location1,TRUE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);
    assertEquals("private", event.getVisibility());
  }

  @Test
  public void testPublicEvent() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "PublicEvent,05/25/2023,09:00:00 AM,05/25/2023,05:00:00 PM,FALSE,Public Description,Location1,FALSE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);
    assertEquals("public", event.getVisibility());
  }

  @Test
  public void testEmptyDescriptionAndLocation() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "EmptyEvent,05/18/2023,03:00:00 PM,05/18/2023,03:15:00 PM,FALSE,,,FALSE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);
    assertNull(event.getDescription());
    assertNull(event.getLocation());
  }

  @Test
  public void testQuotedFields() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "\"Event, With Comma\",05/22/2023,10:00:00 AM,05/22/2023,11:30:00 AM,FALSE,\"Description, with comma\",\"Location, with comma\",FALSE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);
    assertEquals("Event, With Comma", event.getEventName());
    assertEquals("Description, with comma", event.getDescription());
    assertEquals("Location, with comma", event.getLocation());
  }

  @Test(expected = Exception.class)
  public void testEmptyFile() throws Exception {
    createTestFile("");

    importer.importEvents(testFilePath);
  }

  @Test
  public void testHeaderOnly() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(0, events.size());
  }

  @Test(expected = Exception.class)
  public void testInsufficientFields() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "IncompleteEvent,05/15/2023,09:00:00 AM,05/15/2023";

    createTestFile(csvContent);

    importer.importEvents(testFilePath);
  }

  @Test(expected = Exception.class)
  public void testNonexistentFile() throws Exception {
    importer.importEvents("nonexistent_file.csv");
  }

  @Test
  public void testEmptyLines() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Event1,05/15/2023,09:00:00 AM,05/15/2023,10:00:00 AM,FALSE,Description1,Location1,FALSE\n" +
            "\n" +
            "Event2,05/15/2023,12:00:00 PM,05/15/2023,01:00:00 PM,FALSE,Description2,Location2,TRUE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(2, events.size());
  }

  private void createTestFile(String content) throws IOException {
    try (FileWriter writer = new FileWriter(testFilePath)) {
      writer.write(content);
    }
  }
}