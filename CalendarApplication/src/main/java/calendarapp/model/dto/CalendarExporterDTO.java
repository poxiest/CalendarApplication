package calendarapp.model.dto;

import java.time.temporal.Temporal;

/**
 * DTO representing an event to be exported from the calendar.
 * Contains event details such as timing, description, and visibility.
 */
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

  /**
   * Creates and returns a new Builder instance for CalendarExporterDTO.
   *
   * @return a new Builder
   */
  public static CalendarExporterDTO.Builder builder() {
    return new CalendarExporterDTO.Builder();
  }

  /**
   * Returns the subject of the event.
   *
   * @return the event subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Returns the start date of the event.
   *
   * @return the start date
   */
  public Temporal getStartDate() {
    return startDate;
  }

  /**
   * Returns the end date of the event.
   *
   * @return the end date
   */
  public Temporal getEndDate() {
    return endDate;
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
   * Builder for constructing CalendarExporterDTO instances.
   */
  public static class Builder {
    private String subject;
    private Temporal startDate;
    private Temporal endDate;
    private boolean isAllDayEvent;
    private String description;
    private String location;
    private String visibility;

    /**
     * Sets the subject of the event.
     *
     * @param subject the event subject
     * @return this Builder instance
     */
    public Builder subject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Sets the start date of the event.
     *
     * @param startDate the start date
     * @return this Builder instance
     */
    public Builder startDate(Temporal startDate) {
      this.startDate = startDate;
      return this;
    }

    /**
     * Sets the end date of the event.
     *
     * @param endDate the end date
     * @return this Builder instance
     */
    public Builder endDate(Temporal endDate) {
      this.endDate = endDate;
      return this;
    }

    /**
     * Sets whether the event is an all-day event.
     *
     * @param isAllDayEvent true if all-day, false otherwise
     * @return this Builder instance
     */
    public Builder isAllDayEvent(boolean isAllDayEvent) {
      this.isAllDayEvent = isAllDayEvent;
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
     * Builds and returns the final CalendarExporterDTO instance.
     *
     * @return the constructed CalendarExporterDTO
     */
    public CalendarExporterDTO build() {
      return new CalendarExporterDTO(this);
    }
  }
}
