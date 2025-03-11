package calendarapp.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public class TimeUtil {
  public static Temporal getLocalDateTimeFromString(String dateTime) {
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

  public static boolean isFirstBeforeSecond(Temporal temporal1, Temporal temporal2) {
    return ChronoUnit.SECONDS.between(temporal1, temporal2) > 0;
  }

  public static boolean isFirstAfterSecond(Temporal temporal1, Temporal temporal2) {
    return ChronoUnit.SECONDS.between(temporal1, temporal2) < 0;
  }

  public static boolean isEqual(Temporal temporal1, Temporal temporal2) {
    return ChronoUnit.SECONDS.between(temporal1, temporal2) == 0;
  }

  public static LocalDateTime getLocalDateTimeFromTemporal(Temporal temporal) {
    if (temporal instanceof LocalDateTime) {
      return ((LocalDateTime) temporal).atZone(ZoneId.systemDefault()).toLocalDateTime();
    } else {
      throw new UnsupportedOperationException("Cannot convert to LocalDateTime: " + temporal.getClass());
    }
  }

  public static String formatDate(Temporal temporal) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    if (temporal instanceof LocalDate) {
      return ((LocalDate) temporal).format(formatter);
    } else if (temporal instanceof LocalDateTime) {
      return ((LocalDateTime) temporal).format(formatter);
    }

    return LocalDate.from(temporal).format(formatter);
  }

  public static String formatTime(Temporal temporal) {
    if (temporal instanceof LocalDateTime) {
      return ((LocalDateTime) temporal).format(DateTimeFormatter.ofPattern("h:mm:ss a"));
    }
    return "";
  }

  public static boolean isAllDayEvent(Temporal startTime, Temporal endTime) {
    try {
      LocalDateTime start = getLocalDateTimeFromTemporal(startTime);
      LocalDateTime end = getLocalDateTimeFromTemporal(endTime);

      boolean startsAtMidnight = start.getHour() == 0 && start.getMinute() == 0 && start.getSecond() == 0;
      boolean endsAtEndOfDay = end.getHour() == 23 && end.getMinute() == 59 && end.getSecond() == 59;

      return startsAtMidnight && endsAtEndOfDay;
    } catch (UnsupportedOperationException e) {
      return false;
    }
  }
}
