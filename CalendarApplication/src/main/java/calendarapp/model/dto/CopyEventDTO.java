package calendarapp.model.dto;

import java.time.temporal.Temporal;

/**
 * DTO class representing an event copy request.
 */
public class CopyEventDTO {
  private final String eventName;
  private final Temporal startTime;
  private final Temporal endTime;
  private final String copyCalendarName;
  private final Temporal copyToDate;

  private CopyEventDTO(Builder builder) {
    this.eventName = builder.eventName;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.copyCalendarName = builder.copyCalendarName;
    this.copyToDate = builder.toDate;
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

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder class for constructing a CopyEventRequest object.
   */
  public static class Builder {
    private String eventName;
    private Temporal startTime;
    private Temporal endTime;
    private String copyCalendarName;
    private Temporal toDate;

    public Builder eventName(String eventName) {
      this.eventName = eventName;
      return this;
    }

    public Builder startTime(Temporal startTime) {
      this.startTime = startTime;
      return this;
    }

    public Builder endTime(Temporal endTime) {
      this.endTime = endTime;
      return this;
    }

    public Builder copyCalendarName(String copyCalendarName) {
      this.copyCalendarName = copyCalendarName;
      return this;
    }

    public Builder copyStartDate(Temporal toDate) {
      this.toDate = toDate;
      return this;
    }

    public CopyEventDTO build() {
      return new CopyEventDTO(this);
    }
  }
}
