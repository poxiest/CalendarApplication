package calendarapp.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum of event visibility options available in the calendar application.
 */
public enum EventVisibility {
  /**
   * Events visible to all users.
   */
  PUBLIC("public"),

  /**
   * Events visible only to the owner.
   */
  PRIVATE("private"),

  /**
   * Default Visibility.
   */
  DEFAULT("default"),

  /**
   * Represents an unrecognized visibility setting.
   */
  UNKNOWN("unknown");

  /**
   * The string representation of the visibility setting.
   */
  private final String value;

  /**
   * Constructs an event visibility enum with the specified string representation.
   *
   * @param value the string representation of this visibility setting.
   */
  EventVisibility(String value) {
    this.value = value;
  }

  /**
   * Finds the event visibility corresponding to the given string representation.
   *
   * @param visibility the string visibility value to match.
   * @return the matching EventVisibility value, or UNKNOWN if no match is found.
   */
  public static EventVisibility getVisibility(String visibility) {
    for (EventVisibility prop : values()) {
      if (prop.value.equalsIgnoreCase(visibility)) {
        return prop;
      }
    }
    return UNKNOWN;
  }

  /**
   * Returns the list of active visibilities.
   *
   * @return List of visibilities as String.
   */
  public static List<String> getVisibilities() {
    return Arrays.stream(values())
        .filter(prop -> !prop.value.equalsIgnoreCase(EventVisibility.UNKNOWN.value))
        .map(EventVisibility::getValue).collect(Collectors.toList());
  }

  /**
   * Gets the string representation of this visibility setting.
   *
   * @return the string value of this visibility setting.
   */
  public String getValue() {
    return value;
  }
}