import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

import calendarapp.utils.TimeUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for Utility class {@link TimeUtil}.
 */
public class TimeUtilsTest {

    private LocalDateTime sampleDateTime;
    private LocalDate sampleDate;

    @Before
    public void setUp() {
      sampleDateTime = LocalDateTime.of(2025, 3, 13, 14, 30);
      sampleDate = LocalDate.of(2025, 3, 13);
    }

    @Test
    public void testGetTemporalFromString() {
      // Valid date and time
      Temporal result = TimeUtil.getTemporalFromString("2025-03-13T14:30");
      assertTrue(result instanceof LocalDateTime);
      assertEquals(sampleDateTime, result);

      // Valid date only
      result = TimeUtil.getTemporalFromString("2025-03-13");
      assertTrue(result instanceof LocalDateTime);
      assertEquals(sampleDate.atStartOfDay(), result);

      // Invalid date
      try {
        TimeUtil.getTemporalFromString("2025-13-13");
        fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        assertTrue(e.getMessage().contains("Invalid date format"));
      }
    }

    @Test
    public void testGetEndOfDayFromString() {
      // Date only (should return end of day)
      Temporal result = TimeUtil.getEndOfDayFromString("2025-03-13");
      assertTrue(result instanceof LocalDateTime);
      assertEquals(sampleDate.atStartOfDay().plusDays(1), result);

      // Date and time
      result = TimeUtil.getEndOfDayFromString("2025-03-13T14:30");
      assertTrue(result instanceof LocalDateTime);
      assertEquals(sampleDateTime, result);
    }

    @Test
    public void testIsFirstBeforeSecond() {
      assertTrue(TimeUtil.isFirstBeforeSecond(sampleDateTime.minusHours(1), sampleDateTime));
      assertFalse(TimeUtil.isFirstBeforeSecond(sampleDateTime, sampleDateTime));
      assertFalse(TimeUtil.isFirstBeforeSecond(sampleDateTime.plusHours(1), sampleDateTime));
    }

    @Test
    public void testIsFirstAfterSecond() {
      assertTrue(TimeUtil.isFirstAfterSecond(sampleDateTime.plusHours(1), sampleDateTime));
      assertFalse(TimeUtil.isFirstAfterSecond(sampleDateTime, sampleDateTime));
      assertFalse(TimeUtil.isFirstAfterSecond(sampleDateTime.minusHours(1), sampleDateTime));
    }

    @Test
    public void testDifference() {
      long differenceInSeconds = TimeUtil.difference(sampleDateTime.minusHours(1), sampleDateTime);
      assertEquals(3600, differenceInSeconds);

      differenceInSeconds = TimeUtil.difference(sampleDateTime, sampleDateTime);
      assertEquals(0, differenceInSeconds);
    }

    @Test
    public void testIsEqual() {
      assertTrue(TimeUtil.isEqual(sampleDateTime, sampleDateTime));
      assertFalse(TimeUtil.isEqual(sampleDateTime, sampleDateTime.plusHours(1)));
    }

    @Test
    public void testGetLocalDateTimeFromTemporal() {
      assertEquals(sampleDateTime, TimeUtil.getLocalDateTimeFromTemporal(sampleDateTime));
      try {
        TimeUtil.getLocalDateTimeFromTemporal(sampleDate);
        fail("Expected UnsupportedOperationException");
      } catch (UnsupportedOperationException e) {
        assertTrue(e.getMessage().contains("Cannot convert to LocalDateTime"));
      }
    }

    @Test
    public void testFormatDate() {
      String formattedDate = TimeUtil.formatDate(sampleDateTime);
      assertEquals("03/13/2025", formattedDate);

      formattedDate = TimeUtil.formatDate(sampleDate);
      assertEquals("03/13/2025", formattedDate);
    }

    @Test
    public void testFormatTime() {
      String formattedTime = TimeUtil.formatTime(sampleDateTime);
      assertEquals("2:30:00 PM", formattedTime);

      formattedTime = TimeUtil.formatTime(sampleDate);
      assertEquals("", formattedTime);
    }

    @Test
    public void testIsAllDayEvent() {
      // All-day event: midnight start and midnight next day end
      LocalDateTime startTime = LocalDateTime.of(2025, 3, 13, 0, 0);
      LocalDateTime endTime = LocalDateTime.of(2025, 3, 14, 0, 0);
      assertTrue(TimeUtil.isAllDayEvent(startTime, endTime));

      // Not an all-day event
      endTime = LocalDateTime.of(2025, 3, 13, 14, 30);
      assertFalse(TimeUtil.isAllDayEvent(startTime, endTime));
    }

    @Test
    public void testIsConflicting() {
      // Overlapping events
      LocalDateTime startTime1 = LocalDateTime.of(2025, 3, 13, 10, 0);
      LocalDateTime endTime1 = LocalDateTime.of(2025, 3, 13, 12, 0);
      LocalDateTime startTime2 = LocalDateTime.of(2025, 3, 13, 11, 0);
      LocalDateTime endTime2 = LocalDateTime.of(2025, 3, 13, 13, 0);
      assertTrue(TimeUtil.isConflicting(startTime1, endTime1, startTime2, endTime2));

      // No conflict
      startTime2 = LocalDateTime.of(2025, 3, 13, 14, 0);
      endTime2 = LocalDateTime.of(2025, 3, 13, 16, 0);
      assertFalse(TimeUtil.isConflicting(startTime1, endTime1, startTime2, endTime2));
    }

    @Test
    public void testIsWithinTimeRange() {
      LocalDateTime rangeStart = LocalDateTime.of(2025, 3, 13, 8, 0);
      LocalDateTime rangeEnd = LocalDateTime.of(2025, 3, 13, 18, 0);
      LocalDateTime eventStart = LocalDateTime.of(2025, 3, 13, 9, 0);
      LocalDateTime eventEnd = LocalDateTime.of(2025, 3, 13, 12, 0);

      assertTrue(TimeUtil.isWithinTimeRange(rangeStart, rangeEnd, eventStart, eventEnd));
      assertFalse(TimeUtil.isWithinTimeRange(rangeStart, rangeEnd, eventStart, eventEnd.plusHours(8)));
    }

    @Test
    public void testIsActiveAt() {
      // Event active at a specific time
      LocalDateTime checkTime = LocalDateTime.of(2025, 3, 13, 10, 0);
      assertTrue(TimeUtil.isActiveAt(checkTime, sampleDateTime.minusHours(6), sampleDateTime.plusHours(1)));

      // Event not active at a specific time
      checkTime = LocalDateTime.of(2025, 3, 13, 8, 0);
      assertFalse(TimeUtil.isActiveAt(checkTime, sampleDateTime.minusHours(1), sampleDateTime.plusHours(1)));
    }
  }