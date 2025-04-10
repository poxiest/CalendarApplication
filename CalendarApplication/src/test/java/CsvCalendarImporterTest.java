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

  /**
   * Test importing a valid CSV file with a single event.
   */
  @Test
  public void testImportSingleEvent() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Meeting,05/15/2023,09:00:00 AM,05/15/2023,10:00:00 AM,FALSE,Team discussion,Conference Room A,FALSE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);

    assertEquals("Meeting", event.getEventName());
    assertEquals("2023-05-15T09:00", event.getStartTime());
    assertEquals("2023-05-15T10:00", event.getEndTime());
    assertEquals("Team discussion", event.getDescription());
    assertEquals("Conference Room A", event.getLocation());
    assertEquals("public", event.getVisibility());
  }

  /**
   * Test importing a CSV file with multiple events.
   */
  @Test
  public void testImportMultipleEvents() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Meeting,05/15/2023,09:00:00 AM,05/15/2023,10:00:00 AM,FALSE,Team discussion,Conference Room A,FALSE\n" +
            "Lunch,05/15/2023,12:00:00 PM,05/15/2023,01:00:00 PM,FALSE,Lunch with clients,Restaurant,TRUE\n" +
            "Interview,05/16/2023,02:00:00 PM,05/16/2023,03:30:00 PM,FALSE,Candidate interview,Room B,TRUE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(3, events.size());
  }

  /**
   * Test handling of private events.
   */
  @Test
  public void testPrivateEvent() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Doctor Appointment,05/20/2023,02:00:00 PM,05/20/2023,03:00:00 PM,FALSE,Annual checkup,Medical Center,TRUE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);
    assertEquals("private", event.getVisibility());
  }

  /**
   * Test handling of public events.
   */
  @Test
  public void testPublicEvent() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Team Building,05/25/2023,09:00:00 AM,05/25/2023,05:00:00 PM,FALSE,Annual team building,Park,FALSE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);
    assertEquals("public", event.getVisibility());
  }

  /**
   * Test handling of empty description and location.
   */
  @Test
  public void testEmptyDescriptionAndLocation() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Quick Call,05/18/2023,03:00:00 PM,05/18/2023,03:15:00 PM,FALSE,,,FALSE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);
    assertNull(event.getDescription());
    assertNull(event.getLocation());
  }

  /**
   * Test handling of quoted fields in CSV.
   */
  @Test
  public void testQuotedFields() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "\"Project, Kickoff\",05/22/2023,10:00:00 AM,05/22/2023,11:30:00 AM,FALSE,\"Initial meeting, project X\",\"Building A, Room 101\",FALSE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);
    assertEquals("Project, Kickoff", event.getEventName());
    assertEquals("Initial meeting, project X", event.getDescription());
    assertEquals("Building A, Room 101", event.getLocation());
  }

  /**
   * Test handling of escaped quotes in quoted fields.
   */
  @Test
  public void testEscapedQuotes() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Meeting,05/23/2023,01:00:00 PM,05/23/2023,02:00:00 PM,FALSE,\"Discussion about \"\"Project X\"\"\",Conference Room,FALSE";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(1, events.size());
    CalendarImporterDTO event = events.get(0);
    assertEquals("Discussion about \"Project X\"", event.getDescription());
  }

  /**
   * Test handling of empty CSV file.
   */
  @Test(expected = Exception.class)
  public void testEmptyFile() throws Exception {
    createTestFile("");

    importer.importEvents(testFilePath);
  }

  /**
   * Test handling of file with header only.
   */
  @Test
  public void testHeaderOnly() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private";

    createTestFile(csvContent);

    List<CalendarImporterDTO> events = importer.importEvents(testFilePath);

    assertEquals(0, events.size());
  }

  /**
   * Test handling of CSV line with insufficient fields.
   */
  @Test(expected = Exception.class)
  public void testInsufficientFields() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Meeting,05/15/2023,09:00:00 AM,05/15/2023";

    createTestFile(csvContent);

    importer.importEvents(testFilePath);
  }

  /**
   * Test handling of nonexistent file.
   */
  @Test(expected = Exception.class)
  public void testNonexistentFile() throws Exception {
    importer.importEvents("nonexistent_file.csv");
  }

  /**
   * Test handling of empty lines in CSV.
   */
  @Test
  public void testEmptyLines() throws Exception {
    String csvContent =
        "Subject,Start Date,Start Time,End Date,End Time,All Day,Description,Location,Private\n" +
            "Meeting,05/15/2023,09:00:00 AM,05/15/2023,10:00:00 AM,FALSE,Team discussion,Conference Room A,FALSE\n" +
            "\n" +
            "Lunch,05/15/2023,12:00:00 PM,05/15/2023,01:00:00 PM,FALSE,Lunch with clients,Restaurant,TRUE";

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