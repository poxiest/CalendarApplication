package calendarapp.model.dto;

import java.time.temporal.Temporal;

/**
 * DTO class representing events print request.
 */
public class PrintEventsResponseDTO {
  private final String eventName;
  private final Temporal startTime;
  private final Temporal endTime;
  private final String location;

  private PrintEventsResponseDTO(PrintEventsResponseDTO.Builder builder) {
    this.eventName = builder.eventName;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.location = builder.location;
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

  public String getLocation() {
    return location;
  }

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

    public PrintEventsResponseDTO.Builder eventName(String eventName) {
      this.eventName = eventName;
      return this;
    }

    public PrintEventsResponseDTO.Builder startTime(Temporal startTime) {
      this.startTime = startTime;
      return this;
    }

    public PrintEventsResponseDTO.Builder endTime(Temporal endTime) {
      this.endTime = endTime;
      return this;
    }

    public PrintEventsResponseDTO.Builder location(String location) {
      this.location = location;
      return this;
    }

    public PrintEventsResponseDTO build() {
      return new PrintEventsResponseDTO(this);
    }
  }
}
