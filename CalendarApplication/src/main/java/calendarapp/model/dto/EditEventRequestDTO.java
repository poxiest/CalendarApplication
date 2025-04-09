package calendarapp.model.dto;

/**
 * Represents a request to edit an event or a set of events.
 */
public class EditEventRequestDTO {
  private final String eventName;
  private final String startTime;
  private final String endTime;
  private final String propertyName;
  private final String propertyValue;
  private final boolean isMultiple;

  /**
   * Private constructor to be used by the Builder.
   *
   * @param builder the Builder containing the configuration for this object.
   */
  private EditEventRequestDTO(Builder builder) {
    this.eventName = builder.eventName;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.propertyName = builder.propertyName;
    this.propertyValue = builder.propertyValue;
    this.isMultiple = builder.isMultiple;
  }

  /**
   * Returns a new Builder instance for constructing an EditEventRequestDTO.
   *
   * @return a new Builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  public String getEventName() {
    return eventName;
  }

  public String getStartTime() {
    return startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public String getPropertyValue() {
    return propertyValue;
  }

  public boolean isMultiple() {
    return isMultiple;
  }

  /**
   * Builder for creating instances of EditEventRequestDTO.
   * Allows setting each property step by step before building the final object.
   */
  public static class Builder {
    private String eventName;
    private String startTime;
    private String endTime;
    private String propertyName;
    private String propertyValue;
    private boolean isMultiple;

    /**
     * Sets the event name.
     *
     * @param eventName the name of the event to be edited.
     * @return this Builder instance.
     */
    public Builder eventName(String eventName) {
      this.eventName = eventName;
      return this;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime the original start time of the event.
     * @return this Builder instance.
     */
    public Builder startTime(String startTime) {
      this.startTime = startTime;
      return this;
    }

    /**
     * Sets the end time of the event.
     *
     * @param endTime the original end time of the event.
     * @return this Builder instance.
     */
    public Builder endTime(String endTime) {
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets the name of the property to be updated.
     *
     * @param propertyName the name of the property that will be updated.
     * @return this Builder instance.
     */
    public Builder propertyName(String propertyName) {
      this.propertyName = propertyName;
      return this;
    }

    /**
     * Sets the new value for the specified property.
     *
     * @param propertyValue the new value to assign to the property.
     * @return this Builder instance.
     */
    public Builder propertyValue(String propertyValue) {
      this.propertyValue = propertyValue;
      return this;
    }

    /**
     * Sets whether the event is recurring.
     *
     * @param isRecurring true if the update applies to a recurring event or series; false otherwise.
     * @return this Builder instance.
     */
    public Builder isRecurring(boolean isRecurring) {
      this.isMultiple = isRecurring;
      return this;
    }

    /**
     * Builds and returns the final EditEventRequestDTO instance.
     *
     * @return the constructed EditEventRequestDTO object.
     */
    public EditEventRequestDTO build() {
      return new EditEventRequestDTO(this);
    }
  }
}
