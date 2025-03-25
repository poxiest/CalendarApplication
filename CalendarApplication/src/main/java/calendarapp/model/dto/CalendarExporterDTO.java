package calendarapp.model.dto;

import java.time.temporal.Temporal;

public class CalendarExporterDTO {
  private final String subject;
  private final Temporal startDate;
  private final Temporal endDate;
  private final boolean isAllDayEvent;
  private final String description;
  private final String location;
  private final String visibility;

  private CalendarExporterDTO(CalendarExporterDTO.Builder builder) {
    this.subject = builder.subject;
    this.startDate = builder.startDate;
    this.endDate = builder.endDate;
    this.isAllDayEvent = builder.isAllDayEvent;
    this.description = builder.description;
    this.location = builder.location;
    this.visibility = builder.visibility;
  }

  public String getSubject() {
    return subject;
  }

  public Temporal getStartDate() {
    return startDate;
  }

  public Temporal getEndDate() {
    return endDate;
  }

  public boolean isAllDayEvent() {
    return isAllDayEvent;
  }

  public String getDescription() {
    return description;
  }

  public String getLocation() {
    return location;
  }

  public String getVisibility() {
    return visibility;
  }

  public static CalendarExporterDTO.Builder builder() {
    return new CalendarExporterDTO.Builder();
  }

  public static class Builder {
    private String subject;
    private Temporal startDate;
    private Temporal endDate;
    private boolean isAllDayEvent;
    private String description;
    private String location;
    private String visibility;

    public Builder subject(String subject) {
      this.subject = subject;
      return this;
    }

    public Builder startDate(Temporal startDate) {
      this.startDate = startDate;
      return this;
    }

    public Builder endDate(Temporal endDate) {
      this.endDate = endDate;
      return this;
    }

    public Builder isAllDayEvent(boolean isAllDayEvent) {
      this.isAllDayEvent = isAllDayEvent;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder location(String location) {
      this.location = location;
      return this;
    }

    public Builder visibility(String visibility) {
      this.visibility = visibility;
      return this;
    }

    public CalendarExporterDTO build() {
      return new CalendarExporterDTO(this);
    }
  }
}
