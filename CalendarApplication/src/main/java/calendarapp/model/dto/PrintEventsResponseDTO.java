package calendarapp.model.dto;

import java.time.temporal.Temporal;

/**
 * DTO representing a response containing event details for printing.
 * Includes event name, time range, and optional location.
 */
public class PrintEventsResponseDTO {
  private final String eventName;
  private final Temporal startTime;
  private final Temporal endTime;
  private final String location;

  /**
   * Builder for constructing a PrintEventsResponseDTO object.
   */
  private PrintEventsResponseDTO(PrintEventsResponseDTO.Builder builder) {
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
   * Creates and returns a new Builder instance for PrintEventsResponseDTO.
   *
   * @return a new Builder instance.
   */
  public static PrintEventsResponseDTO.Builder builder() {
    return new PrintEventsResponseDTO.Builder();
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
    public PrintEventsResponseDTO.Builder eventName(String eventName) {
      this.eventName = eventName;
      return this;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime the start time.
     * @return this Builder instance.
     */
    public PrintEventsResponseDTO.Builder startTime(Temporal startTime) {
      this.startTime = startTime;
      return this;
    }

    /**
     * Sets the end time of the event.
     *
     * @param endTime the end time.
     * @return this Builder instance.
     */
    public PrintEventsResponseDTO.Builder endTime(Temporal endTime) {
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets the location of the event.
     *
     * @param location the event location.
     * @return this Builder instance.
     */
    public PrintEventsResponseDTO.Builder location(String location) {
      this.location = location;
      return this;
    }

    /**
     * Builds and returns the final PrintEventsResponseDTO instance.
     *
     * @return the constructed PrintEventsResponseDTO object.
     */
    public PrintEventsResponseDTO build() {
      return new PrintEventsResponseDTO(this);
    }
  }
}
