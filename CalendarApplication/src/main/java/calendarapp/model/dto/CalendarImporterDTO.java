package calendarapp.model.dto;

/**
 * DTO representing an event to be imported into the calendar.
 * Contains essential event details such as name, timing, description, location, and visibility.
 */
public class CalendarImporterDTO {
  private final String eventName;
  private final String startTime;
  private final String endTime;
  private final String description;
  private final String location;
  private final String visibility;

  private CalendarImporterDTO(CalendarImporterDTO.Builder builder) {
    this.eventName = builder.eventName;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.description = builder.description;
    this.location = builder.location;
    this.visibility = builder.visibility;
  }

  /**
   * Returns the name of the event.
   *
   * @return the event name
   */
  public String getEventName() {
    return eventName;
  }

  /**
   * Returns the start time of the event.
   *
   * @return the start time
   */
  public String getStartTime() {
    return startTime;
  }

  /**
   * Returns the end time of the event.
   *
   * @return the end time
   */
  public String getEndTime() {
    return endTime;
  }

  /**
   * Returns the description of the event.
   *
   * @return the event description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the location of the event.
   *
   * @return the event location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Returns the visibility of the event.
   *
   * @return the event visibility
   */
  public String getVisibility() {
    return visibility;
  }

  /**
   * Creates and returns a new Builder instance for CalendarImporterDTO.
   *
   * @return a new Builder
   */
  public static CalendarImporterDTO.Builder builder() {
    return new CalendarImporterDTO.Builder();
  }

  /**
   * Builder for constructing CalendarImporterDTO instances.
   */
  public static class Builder {
    private String eventName;
    private String startTime;
    private String endTime;
    private String description;
    private String location;
    private String visibility;

    /**
     * Sets the name of the event.
     *
     * @param eventName the event name
     * @return this Builder instance
     */
    public Builder eventName(String eventName) {
      this.eventName = eventName;
      return this;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime the start time
     * @return this Builder instance
     */
    public Builder startTime(String startTime) {
      this.startTime = startTime;
      return this;
    }

    /**
     * Sets the end time of the event.
     *
     * @param endTime the end time
     * @return this Builder instance
     */
    public Builder endTime(String endTime) {
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets the description of the event.
     *
     * @param description the event description
     * @return this Builder instance
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the location of the event.
     *
     * @param location the event location
     * @return this Builder instance
     */
    public Builder location(String location) {
      this.location = location;
      return this;
    }

    /**
     * Sets the visibility of the event.
     *
     * @param visibility the event visibility
     * @return this Builder instance
     */
    public Builder visibility(String visibility) {
      this.visibility = visibility;
      return this;
    }

    /**
     * Builds and returns the final CalendarImporterDTO instance.
     *
     * @return the constructed CalendarImporterDTO
     */
    public CalendarImporterDTO build() {
      return new CalendarImporterDTO(this);
    }
  }
}