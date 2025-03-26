package calendarapp.utils;

import java.time.Duration;
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
    } catch (DateTimeParseException e2) {
      try {
        return LocalDate.parse(dateTime, dateFormatter).atStartOfDay();
      } catch (DateTimeParseException e3) {
        throw new IllegalArgumentException("Invalid date format: " + dateTime);
      }
    }
  }

  public static Temporal getDateFromString(String dateTime) {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    if (dateTime == null) {
      return null;
    }
    try {
      return LocalDate.parse(dateTime, dateFormatter);
    } catch (DateTimeParseException e3) {
      throw new IllegalArgumentException("Invalid date format: " + dateTime);
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
    return getLocalDateTimeFromTemporal(time).plusDays(1).toLocalDate().atStartOfDay();
  }

  /**
   * Returns the start of the day (00:00) for the given date string.
   *
   * @param dateTime the date string to parse
   * @return the Temporal representing the start of the day
   * @throws IllegalArgumentException if the input cannot be parsed
   */
  public static Temporal getStartOfDayFromString(String dateTime) throws IllegalArgumentException {
    if (dateTime == null) {
      return null;
    }
    Temporal time = getTemporalFromString(dateTime);
    return getLocalDateTimeFromTemporal(time).toLocalDate().atStartOfDay();
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
   * Find the difference between two Temproral values.
   *
   * @param temporal1 the first temporal to compare.
   * @param temporal2 the second temporal to compare.
   * @return Difference between both in Long.
   */
  public static Long difference(Temporal temporal1, Temporal temporal2) {
    return ChronoUnit.SECONDS.between(temporal1, temporal2);
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
      return ((LocalDateTime) temporal);
    } else {
      throw new UnsupportedOperationException("Cannot convert to LocalDateTime: "
          + temporal.getClass());
    }
  }

  /**
   * Returns the start of the next day (00:00) for the given temporal value.
   *
   * @param temporal the input temporal
   * @return the start of the next day
   */
  public static Temporal GetStartOfNextDay(Temporal temporal) {
    return getLocalDateTimeFromTemporal(temporal).toLocalDate().plusDays(1).atStartOfDay();
  }

  /**
   * Returns the start of the same day (00:00) for the given temporal value.
   *
   * @param temporal the input temporal
   * @return the start of the day
   */
  public static Temporal GetStartOfDay(Temporal temporal) {
    return getLocalDateTimeFromTemporal(temporal).toLocalDate().atStartOfDay();
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
    }
    return ((LocalDateTime) temporal).format(formatter);
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
    LocalDateTime start = getLocalDateTimeFromTemporal(startTime);
    LocalDateTime end = getLocalDateTimeFromTemporal(endTime);
    boolean startsAtMidnight = start.getHour() == 0 && start.getMinute() == .0
        && start.getSecond() == 0;
    boolean endsAtStartOfNextDay = end.getHour() == 0 && end.getMinute() == 0
        && end.getSecond() == 0 &&
        end.toLocalDate().isEqual(start.toLocalDate().plusDays(1));
    return startsAtMidnight && endsAtStartOfNextDay;
  }

  /**
   * Determines if two events have conflicting time ranges.
   * An event is considered conflicting if its time range overlaps with another event's time range.
   *
   * @param startTime1 the start time of the first event.
   * @param endTime1   the end time of the first event.
   * @param startTime2 the start time of the second event.
   * @param endTime2   the end time of the second event.
   * @return true if the two events conflict (i.e., their time ranges overlap), false otherwise.
   */
  public static boolean isConflicting(Temporal startTime1, Temporal endTime1,
                                      Temporal startTime2, Temporal endTime2) {
    return isFirstBeforeSecond(startTime1, endTime2)
        && isFirstAfterSecond(endTime1, startTime2);
  }

  /**
   * Converts a temporal value from one time zone to another.
   *
   * @param temporal   the temporal value to convert
   * @param fromZoneId the original time zone
   * @param toZoneId   the target time zone
   * @return the temporal value in the target time zone
   */
  public static Temporal ChangeZone(Temporal temporal, ZoneId fromZoneId, ZoneId toZoneId) {
    return getLocalDateTimeFromTemporal(temporal).atZone(fromZoneId)
        .withZoneSameInstant(toZoneId).toLocalDateTime();
  }

  /**
   * Adds a duration to the given temporal value.
   *
   * @param temporal the original temporal value
   * @param duration the duration to add
   * @return the updated temporal value
   */
  public static Temporal AddDuration(Temporal temporal, Duration duration) {
    return temporal.plus(duration);
  }

  /**
   * Calculates the duration between two temporal values.
   * Supports comparisons between LocalDateTime and LocalDate.
   *
   * @param start the start temporal value
   * @param end   the end temporal value
   * @return the duration between the two values
   * @throws UnsupportedOperationException if the input types are not supported
   */
  public static Duration getDurationDifference(Temporal start, Temporal end) {
    if (start instanceof LocalDateTime && end instanceof LocalDateTime) {
      return Duration.between(start, end);

    } else if (start instanceof LocalDateTime && end instanceof LocalDate) {
      LocalDateTime endDateTime = ((LocalDate) end).atTime(((LocalDateTime) start).getHour(),
          ((LocalDateTime) start).getMinute());
      return Duration.between(start, endDateTime);

    } else {
      throw new UnsupportedOperationException("Unsupported Temporal types: "
          + start.getClass() + " and " + end.getClass());
    }
  }
}