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
  private final String description;
  private final String visibility;
  private final String recurringDays;
  private final Integer occurrenceCount;
  private final Temporal recurrenceEndDate;

  /**
   * Builder for constructing a EventsResponseDTO object.
   */
  private EventsResponseDTO(EventsResponseDTO.Builder builder) {
    this.eventName = builder.eventName;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.location = builder.location;
    this.description = builder.description;
    this.visibility = builder.visibility;
    this.recurringDays = builder.recurringDays;
    this.occurrenceCount = builder.occurrenceCount;
    this.recurrenceEndDate = builder.recurrenceEndDate;
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
   * Returns the visibility of the event.
   *
   * @return the event visibility.
   */
  public String getVisibility() {
    return visibility;
  }

  /**
   * Returns the description of the event.
   *
   * @return the event description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the recurring days of the event.
   *
   * @return the recurring days.
   */
  public String getRecurringDays() {
    return recurringDays;
  }

  /**
   * Returns the recurrence end date of the event.
   *
   * @return the recurrence end date.
   */
  public Temporal getRecurrenceEndDate() {
    return recurrenceEndDate;
  }

  /**
   * Returns the occurrence count of the event.
   *
   * @return the occurrence count.
   */
  public Integer getOccurrenceCount() {
    return occurrenceCount;
  }

  /**
   * Builder class for constructing a PrintEventsDTO object.
   */
  public static class Builder {
    private String eventName;
    private Temporal startTime;
    private Temporal endTime;
    private String location;
    private String description;
    private String visibility;
    private String recurringDays;
    private Integer occurrenceCount;
    private Temporal recurrenceEndDate;

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
     * Sets the description of the event.
     *
     * @param description the event location.
     * @return this Builder instance.
     */
    public EventsResponseDTO.Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the visibility of the event.
     *
     * @param visibility the event location.
     * @return this Builder instance.
     */
    public EventsResponseDTO.Builder visibility(String visibility) {
      this.visibility = visibility;
      return this;
    }

    /**
     * Sets the recurring days of the event.
     *
     * @param recurringDays the event location.
     * @return this Builder instance.
     */
    public EventsResponseDTO.Builder recurringDays(String recurringDays) {
      this.recurringDays = recurringDays;
      return this;
    }

    /**
     * Sets the occurrence count of the event.
     *
     * @param occurrenceCount the event location.
     * @return this Builder instance.
     */
    public EventsResponseDTO.Builder occurrenceCount(Integer occurrenceCount) {
      this.occurrenceCount = occurrenceCount;
      return this;
    }

    /**
     * Sets the recurring end date of the event.
     *
     * @param recurrenceEndDate the event location.
     * @return this Builder instance.
     */
    public EventsResponseDTO.Builder recurringEndDate(Temporal recurrenceEndDate) {
      this.recurrenceEndDate = recurrenceEndDate;
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
