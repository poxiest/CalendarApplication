package calendarapp.model.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import calendarapp.model.EventVisibility;
import calendarapp.utils.TimeUtil;

/**
 * The {@code EventPropertyUpdater} class provides a centralized mechanism for updating
 * properties of an {@link Event} object. It maintains a registry of property updaters,
 * where each updater is a {@link BiConsumer} that applies a new value to the corresponding
 * property of an {@link Event.Builder} instance.
 */
public class EventPropertyUpdater {

  /**
   * A map that associates property keys with their corresponding updater functions.
   */
  private static final Map<String, BiConsumer<Event.Builder, String>> UPDATERS = new HashMap<>();

  // Static initializer block to register all property updaters
  static {
    // Register all property updaters
    UPDATERS.put(EventConstants.PropertyKeys.NAME,
        Event.Builder::name);

    UPDATERS.put(EventConstants.PropertyKeys.START_TIME,
        (builder, value) ->
            builder.startTime(TimeUtil.getTemporalFromString(value)));

    UPDATERS.put(EventConstants.PropertyKeys.END_TIME,
        (builder, value) -> builder.endTime(TimeUtil.getTemporalFromString(value)));

    UPDATERS.put(EventConstants.PropertyKeys.DESCRIPTION,
        Event.Builder::description);

    UPDATERS.put(EventConstants.PropertyKeys.LOCATION,
        Event.Builder::location);

    UPDATERS.put(EventConstants.PropertyKeys.VISIBILITY,
        (builder, value) -> builder.visibility(EventVisibility.DEFAULT));

    UPDATERS.put(EventConstants.PropertyKeys.RECURRING_DAYS,
        Event.Builder::recurringDays);

    UPDATERS.put(EventConstants.PropertyKeys.OCCURRENCE_COUNT,
        (builder, value) -> builder.occurrenceCount(Integer.parseInt(value)));

    UPDATERS.put(EventConstants.PropertyKeys.RECURRENCE_END_DATE,
        (builder, value) -> builder.recurrenceEndDate(TimeUtil.getTemporalFromString(value)));
  }

  /**
   * Retrieves the updater function associated with the specified property key.
   *
   * @param property the property key for which to retrieve the updater.
   * @return the {@link BiConsumer} updater function associated with the property,
   *     or {@code null} if no updater is found for the given key.
   */
  public static BiConsumer<Event.Builder, String> getUpdater(String property) {
    return UPDATERS.get(property.toLowerCase());
  }
}