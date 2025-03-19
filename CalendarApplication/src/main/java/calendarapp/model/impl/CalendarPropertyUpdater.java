package calendarapp.model.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CalendarPropertyUpdater {
  private static final Map<String, BiConsumer<Calendar, String>> UPPDATERS;

  static {
    UPPDATERS = new HashMap<>();
    UPPDATERS.put("name", (calendar, value) -> calendar.setName(value));
    UPPDATERS.put("timezone", (calendar, value) -> calendar.setZoneId(value));
  }

  public static BiConsumer<Calendar, String> getUpdater(String property) {
    return UPPDATERS.get(property.toLowerCase());
  }
}
