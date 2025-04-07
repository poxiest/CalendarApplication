package calendarapp.model.impl;

import java.util.HashMap;
import java.util.Map;

import calendarapp.model.CalendarUpdateOperation;
import calendarapp.model.ICalendar;

/**
 * Provides property update operations for Calendar.Builder using a map of supported properties.
 */
public class CalendarPropertyUpdater {
  private static final Map<String, CalendarUpdateOperation> UPDATERS;

  static {
    UPDATERS = new HashMap<>();
    UPDATERS.put(Constants.Calendar.CALENDAR_NAME, (oldCal, newValue) -> Calendar.builder()
        .name(newValue)
        .zoneId(oldCal.getZoneId())
        .eventRepository(oldCal.getEventRepository())
        .build());
    UPDATERS.put(Constants.Calendar.CALENDAR_TIME_ZONE, (oldCal, newValue) -> {
      ICalendar updated = Calendar.builder()
          .name(oldCal.getName())
          .zoneId(newValue)
          .eventRepository(oldCal.getEventRepository())
          .build();
      updated.getEventRepository().changeTimeZone(oldCal.getZoneId(), updated.getZoneId());
      return updated;
    });
  }

  /**
   * Returns the updater function for the given calendar property.
   *
   * @param property the name of the calendar property
   * @return a BiConsumer that updates the corresponding property on the Calendar.Builder
   */
  public static CalendarUpdateOperation getUpdater(String property) {
    return UPDATERS.get(property.toLowerCase());
  }
}
