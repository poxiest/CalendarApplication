package calendarapp.model.dto;

import java.time.temporal.Temporal;

/**
 * DTO representing a response containing event details for printing.
 * Includes event name, time range, and optional location.
 */
public class EventsResponseDTO {
  private final String eventName;
  private final Temporal startTime;
  private final Temporal endTime;
  private final String location;

  /**
   * Builder for constructing a EventsResponseDTO object.
   */
  private EventsResponseDTO(EventsResponseDTO.Builder builder) {
    this.eventName = builder.eventName;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.location = builder.location;
  }

  /**
   * Returns the name of the event.
   *
   * @return the event name.
   */
  public String getEventName() {
    return eventName;
  }

  /**
   * Returns the start time of the event.
   *
   * @return the start time.
   */
  public Temporal getStartTime() {
    return startTime;
  }

  /**
   * Returns the end time of the event.
   *
   * @return the end time.
   */
  public Temporal getEndTime() {
    return endTime;
  }

  /**
   * Returns the location of the event.
   *
   * @return the event location.
   */
  public String getLocation() {
    return location;
  }

  /**
   * Creates and returns a new Builder instance for EventsResponseDTO.
   *
   * @return a new Builder instance.
   */
  public static EventsResponseDTO.Builder builder() {
    return new EventsResponseDTO.Builder();
  }

  /**
   * Builder class for constructing a PrintEventsDTO object.
   */
  public static class Builder {
    private String eventName;
    private Temporal startTime;
    private Temporal endTime;
    private String location;

    /**
     * Sets the name of the event.
     *
     * @param eventName the event name.
     * @return this Builder instance.
     */
    public EventsResponseDTO.Builder eventName(String eventName) {
      this.eventName = eventName;
      return this;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime the start time.
     * @return this Builder instance.
     */
    public EventsResponseDTO.Builder startTime(Temporal startTime) {
      this.startTime = startTime;
      return this;
    }

    /**
     * Sets the end time of the event.
     *
     * @param endTime the end time.
     * @return this Builder instance.
     */
    public EventsResponseDTO.Builder endTime(Temporal endTime) {
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets the location of the event.
     *
     * @param location the event location.
     * @return this Builder instance.
     */
    public EventsResponseDTO.Builder location(String location) {
      this.location = location;
      return this;
    }

    /**
     * Builds and returns the final EventsResponseDTO instance.
     *
     * @return the constructed EventsResponseDTO object.
     */
    public EventsResponseDTO build() {
      return new EventsResponseDTO(this);
    }
  }
}
