package calendarapp.model.dto;

import java.time.temporal.Temporal;

/**
 * Represents a request to copy an event or set of events to another calendar.
 * Includes the event name, original time range, destination calendar name,
 * and target date.
 */
public class CopyEventRequestDTO {
  private final String eventName;
  private final Temporal startTime;
  private final Temporal endTime;
  private final String copyCalendarName;
  private final Temporal copyToDate;

  private CopyEventRequestDTO(Builder builder) {
    this.eventName = builder.eventName;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.copyCalendarName = builder.copyCalendarName;
    this.copyToDate = builder.toDate;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getEventName() {
    return eventName;
  }

  public Temporal getStartTime() {
    return startTime;
  }

  public Temporal getEndTime() {
    return endTime;
  }

  public String getCopyCalendarName() {
    return copyCalendarName;
  }

  public Temporal getCopyToDate() {
    return copyToDate;
  }

  /**
   * Builder for creating instances of CopyEventRequestDTO.
   * Allows setting each property step by step before building the final object.
   */
  public static class Builder {
    private String eventName;
    private Temporal startTime;
    private Temporal endTime;
    private String copyCalendarName;
    private Temporal toDate;

    /**
     * Sets the name of the event.
     *
     * @param eventName the event name to copy.
     * @return this Builder instance.
     */
    public Builder eventName(String eventName) {
      this.eventName = eventName;
      return this;
    }

    /**
     * Sets the start time of the event or event range.
     *
     * @param startTime the original start time.
     * @return this Builder instance.
     */
    public Builder startTime(Temporal startTime) {
      this.startTime = startTime;
      return this;
    }

    /**
     * Sets the end time of the event or event range.
     *
     * @param endTime the original end time.
     * @return this Builder instance.
     */
    public Builder endTime(Temporal endTime) {
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets the name of the calendar to copy the event(s) to.
     *
     * @param copyCalendarName the name of the target calendar.
     * @return this Builder instance.
     */
    public Builder copyCalendarName(String copyCalendarName) {
      this.copyCalendarName = copyCalendarName;
      return this;
    }

    /**
     * Sets the date to copy the event(s) to.
     *
     * @param toDate the destination date.
     * @return this Builder instance.
     */
    public Builder copyStartDate(Temporal toDate) {
      this.toDate = toDate;
      return this;
    }

    /**
     * Builds and returns the final CopyEventRequestDTO instance.
     *
     * @return the constructed CopyEventRequestDTO object.
     */
    public CopyEventRequestDTO build() {
      return new CopyEventRequestDTO(this);
    }
  }
}
