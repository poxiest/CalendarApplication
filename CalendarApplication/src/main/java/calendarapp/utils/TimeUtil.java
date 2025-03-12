package calendarapp.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

/**
 * Utility class for handling time and date operations in the calendar application.
 * Provides methods for parsing, formatting, and comparing temporal values.
 */
public class TimeUtil {

  /**
   * Converts a string representation of date/time to a Temporal object.
   * Accepts two formats: "yyyy-MM-dd'T'HH:mm" and
   * "yyyy-MM-dd" - will be converted to start of day.
   *
   * @param dateTime the string representation of date/time to parse.
   * @return a Temporal object (specifically a LocalDateTime) representing the parsed date/time,
   *     or null if the input is null.
   * @throws IllegalArgumentException if the date/time string cannot be parsed.
   */
  public static Temporal getTemporalFromString(String dateTime) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    if (dateTime == null) {
      return null;
    }

    try {
      return LocalDateTime.parse(dateTime, dateTimeFormatter);
    } catch (DateTimeParseException e1) {
      try {
        return LocalDate.parse(dateTime, dateFormatter).atStartOfDay();
      } catch (DateTimeParseException e2) {
        throw new IllegalArgumentException("Invalid date format: " + dateTime);
      }
    }
  }

  /**
   * Gets the end of day or exact time from a string representation.
   *
   * @param dateTime the string representation of date/time to parse.
   * @return a Temporal object representing either the exact time or the end of the day,
   *     or null if the input is null.
   * @throws IllegalArgumentException if the date/time string cannot be parsed.
   */
  public static Temporal getEndOfDayFromString(String dateTime) throws IllegalArgumentException {
    if (dateTime == null) {
      return null;
    }
    Temporal time = getTemporalFromString(dateTime);
    if (!dateTime.contains("T")) {
      time = time.plus(1, ChronoUnit.DAYS);
    }
    return time;
  }

  /**
   * Checks if the first temporal is chronologically before the second temporal.
   *
   * @param temporal1 the first temporal to compare.
   * @param temporal2 the second temporal to compare.
   * @return true if the first temporal is before the second, false otherwise.
   */
  public static boolean isFirstBeforeSecond(Temporal temporal1, Temporal temporal2) {
    return ChronoUnit.SECONDS.between(temporal1, temporal2) > 0;
  }

  /**
   * Checks if the first temporal is chronologically after the second temporal.
   *
   * @param temporal1 the first temporal to compare.
   * @param temporal2 the second temporal to compare.
   * @return true if the first temporal is after the second, false otherwise.
   */
  public static boolean isFirstAfterSecond(Temporal temporal1, Temporal temporal2) {
    return ChronoUnit.SECONDS.between(temporal1, temporal2) < 0;
  }

  /**
   * Checks if two temporal objects represent the same instant.
   *
   * @param temporal1 the first temporal to compare.
   * @param temporal2 the second temporal to compare.
   * @return true if both temporals represent the same time, false otherwise.
   */
  public static boolean isEqual(Temporal temporal1, Temporal temporal2) {
    return ChronoUnit.SECONDS.between(temporal1, temporal2) == 0;
  }

  /**
   * Converts a Temporal object to LocalDateTime.
   *
   * @param temporal the temporal object to convert.
   * @return the LocalDateTime representation of the temporal.
   * @throws UnsupportedOperationException if the temporal cannot be converted to LocalDateTime.
   */
  public static LocalDateTime getLocalDateTimeFromTemporal(Temporal temporal) {
    if (temporal instanceof LocalDateTime) {
      return ((LocalDateTime) temporal).atZone(ZoneId.systemDefault()).toLocalDateTime();
    } else {
      throw new UnsupportedOperationException("Cannot convert to LocalDateTime: " + temporal.getClass());
    }
  }

  /**
   * Formats a temporal object as a date string (MM/dd/yyyy).
   *
   * @param temporal the temporal object to format.
   * @return a string representation of the date.
   */
  public static String formatDate(Temporal temporal) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    if (temporal instanceof LocalDate) {
      return ((LocalDate) temporal).format(formatter);
    } else if (temporal instanceof LocalDateTime) {
      return ((LocalDateTime) temporal).format(formatter);
    }

    return LocalDate.from(temporal).format(formatter);
  }

  /**
   * Formats a temporal object as a time string (h:mm:ss a).
   *
   * @param temporal the temporal object to format.
   * @return a string representation of the time, or an empty string if not a LocalDateTime.
   */
  public static String formatTime(Temporal temporal) {
    if (temporal instanceof LocalDateTime) {
      return ((LocalDateTime) temporal).format(DateTimeFormatter.ofPattern("h:mm:ss a"));
    }
    return "";
  }

  /**
   * Determines if the given start and end times represent an all-day event.
   *
   * @param startTime the start time of the event.
   * @param endTime   the end time of the event.
   * @return true if the event is an all-day event, false otherwise.
   */
  public static boolean isAllDayEvent(Temporal startTime, Temporal endTime) {
    try {
      LocalDateTime start = getLocalDateTimeFromTemporal(startTime);
      LocalDateTime end = getLocalDateTimeFromTemporal(endTime);

//       boolean startsAtMidnight = start.getHour() == 0 && start.getMinute() ==.0 && start.getSecond() == 0;
//       boolean endsAtStartOfNextDay = end.getHour() == 0 && end.getMinute() == 0 && end.getSecond() == 0 &&
//           end.toLocalDate().isEqual(start.toLocalDate().plusDays(1));
      // TODO: bro check this
      boolean startsAtMidnight = start.getHour() == 0 && start.getMinute() == 0;
      boolean endsAtStartOfNextDay = end.getHour() == 0 && end.getMinute() == 0 &&
          (end.getDayOfMonth() == (start.getDayOfMonth() + 1));

      return startsAtMidnight && endsAtStartOfNextDay;
    } catch (UnsupportedOperationException e) {
      return false;
    }
  }
}