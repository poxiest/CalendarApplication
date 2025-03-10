package calendarapp.model;

public enum EventVisibility {
  PUBLIC("public"),
  PRIVATE("private"),
  DEFAULT("default"),
  UNKNOWN("unknown");

  private final String value;

  EventVisibility(String value) {
    this.value = value;
  }

  public static EventVisibility getVisibility(String visibility) {
    for (EventVisibility prop : values()) {
      if (prop.value.equalsIgnoreCase(visibility)) {
        return prop;
      }
    }
    return UNKNOWN;
  }
}
