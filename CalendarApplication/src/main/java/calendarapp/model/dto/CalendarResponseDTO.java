package calendarapp.model.dto;

import java.time.ZoneId;

/**
 * DTO representing a response containing calendar details.
 */
public class CalendarResponseDTO {
  private final String name;
  private final ZoneId zoneId;

  /**
   * Constructs a Calendar instance with the given name, time zone, and event repository.
   *
   * @param builder the Builder containing the configuration for this object.
   */
  CalendarResponseDTO(Builder builder) {
    this.name = builder.name;
    this.zoneId = builder.zoneId;
  }

  /**
   * Creates and returns a new Builder instance for constructing a Calendar.
   *
   * @return a new Builder
   */
  public static Builder builder() {
    return new Builder();
  }

  public String getName() {
    return name;
  }

  public ZoneId getZoneId() {
    return zoneId;
  }

  /**
   * Builder class for constructing Calendar instances.
   */
  public static class Builder {
    private String name;
    private ZoneId zoneId;

    /**
     * Sets the name of the calendar.
     *
     * @param name the calendar name
     * @return this Builder instance
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the time zone of the calendar using a ZoneId.
     *
     * @param zoneId the ZoneId object
     * @return this Builder instance
     */
    public Builder zoneId(ZoneId zoneId) {
      this.zoneId = zoneId;
      return this;
    }

    /**
     * Builds and returns a Calendar instance using the provided properties.
     *
     * @return the constructed Calendar object
     */
    public CalendarResponseDTO build() {
      return new CalendarResponseDTO(this);
    }
  }
}
