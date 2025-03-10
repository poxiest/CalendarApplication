package calendarapp.model;

public enum EventVisibility {
  PUBLIC("public"),
  PRIVATE("private"),
  DEFAULT("default");

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
    return DEFAULT;
  }
}
