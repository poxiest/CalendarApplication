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
}
