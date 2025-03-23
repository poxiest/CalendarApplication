package calendarapp.model.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CalendarPropertyUpdater {
  private static final Map<String, BiConsumer<Calendar.Builder, String>> UPDATERS;

  static {
    UPDATERS = new HashMap<>();
    UPDATERS.put(Constants.Calendar.CALENDAR_NAME, (builder, value) -> builder.name(value));
    UPDATERS.put(Constants.Calendar.CALENDAR_TIME_ZONE, (builder, value) -> builder.zoneId(value));
  }

  public static BiConsumer<Calendar.Builder, String> getUpdater(String property) {
    return UPDATERS.get(property.toLowerCase());
  }
}
