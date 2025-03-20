package calendarapp.model.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CalendarPropertyUpdater {
  private static final Map<String, BiConsumer<Calendar.Builder, String>> UPPDATERS;

  static {
    UPPDATERS = new HashMap<>();
    UPPDATERS.put("name", (builder, value) -> builder.name(value));
    UPPDATERS.put("timezone", (builder, value) -> builder.zoneId(value));
  }

  public static BiConsumer<Calendar.Builder, String> getUpdater(String property) {
    return UPPDATERS.get(property.toLowerCase());
  }
}
