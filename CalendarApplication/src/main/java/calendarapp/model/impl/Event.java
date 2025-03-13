package calendarapp.model.impl;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.function.BiConsumer;

import calendarapp.model.EventVisibility;
import calendarapp.model.IEvent;
import calendarapp.utils.TimeUtil;

import static calendarapp.utils.TimeUtil.isEqual;
import static calendarapp.utils.TimeUtil.isFirstAfterSecond;
import static calendarapp.utils.TimeUtil.isFirstBeforeSecond;


/**
 * Implementation of the {@link IEvent} interface representing a calendar event.
 */
public class Event implements IEvent {

  /**
   * The name of the event.
   */
  private final String name;

  /**
   * The start time of the event.
   */
  private final Temporal startTime;

  /**
   * The end time of the event.
   */
  private final Temporal endTime;

  /**
   * The description of the event.
   */
  private final String description;

  /**
   * The location where the event takes place.
   */
  private final String location;

  /**
   * The visibility setting of the event.
   */
  private final EventVisibility visibility;

  /**
   * The days of the week on which a recurring event occurs (e.g., "MWF").
   */
  private final String recurringDays;

  /**
   * The number of occurrences for recurring events.
   */
  private final Integer occurrenceCount;

  /**
   * The end date for recurring events.
   */
  private final Temporal recurrenceEndDate;

  /**
   * Whether this event should auto-decline conflicting events.
   */
  private final boolean isAutoDecline;

  /**
   * Private constructor used by the Builder.
   *
   * @param builder the builder containing event parameters
   */
  private Event(Builder builder) {
    this.name = builder.name;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.description = builder.description;
    this.location = builder.location;
    this.visibility = builder.visibility;
    this.recurringDays = builder.recurringDays;
    this.occurrenceCount = builder.occurrenceCount;
    this.recurrenceEndDate = builder.recurrenceEndDate;
    this.isAutoDecline = builder.isAutoDecline;
  }

  /**
   * Checks if this event conflicts with another event.
   *
   * @param other the event to check for conflicts.
   * @return true if there is a conflict, false otherwise.
   */
  @Override
  public boolean conflictsWith(IEvent other) {
    if (!(other instanceof Event)) {
      return false;
    }
    Event otherEvent = (Event) other;
    return overlapsWith(otherEvent.startTime, otherEvent.endTime);
  }

  /**
   * Determines if the event is active at the specified date and time.
   *
   * @param dateTime the date and time to check.
   * @return true if the event is active at the specified time, false otherwise.
   */
  @Override
  public boolean isActiveAt(Temporal dateTime) {
    return isEqual(dateTime, this.startTime)
        || isEqual(dateTime, this.endTime)
        || (isFirstAfterSecond(dateTime, this.startTime)
        && isFirstBeforeSecond(dateTime, this.endTime));
  }

  /**
   * Checks if this event matches the specified name.
   *
   * @param eventName the name to match against, or null to match any event.
   * @return true if the event name matches or if eventName is null, false otherwise.
   */
  @Override
  public boolean matchesName(String eventName) {
    return eventName == null || this.name.equals(eventName);
  }

  /**
   * Determines if this event falls within the specified time range.
   *
   * @param startDateTime the start of the time range, or null for no start constraint.
   * @param endDateTime   the end of the time range, or null for no end constraint.
   * @return true if the event is within the specified time range, false otherwise.
   */
  @Override
  public boolean isWithinTimeRange(Temporal startDateTime, Temporal endDateTime) {
    boolean afterStart = startDateTime == null
        || isFirstAfterSecond(this.startTime, startDateTime)
        || isEqual(this.startTime, startDateTime);
    boolean beforeEnd = endDateTime == null
        || isFirstBeforeSecond(this.endTime, endDateTime)
        || isEqual(this.endTime, endDateTime);
    return afterStart && beforeEnd;
  }

  /**
   * Creates a new event with the specified property updated to the given value.
   * Supported properties are defined in {@link EventConstants.PropertyKeys}.
   *
   * @param property the property to update.
   * @param value    the new value for the property.
   * @return a new Event instance with the updated property.
   * @throws IllegalArgumentException if the property is not supported or the value is invalid.
   */
  @Override
  public IEvent updateProperty(String property, String value) {
    BiConsumer<Builder, String> updater = EventPropertyUpdater.getUpdater(property);
    if (updater == null) {
      throw new IllegalArgumentException("Cannot edit property: " + property + "\n");
    }

    Builder builder = Event.builder()
        .name(this.name)
        .startTime(this.startTime)
        .endTime(this.endTime)
        .description(this.description)
        .location(this.location)
        .visibility(this.visibility.getValue())
        .recurringDays(this.recurringDays)
        .occurrenceCount(this.occurrenceCount)
        .recurrenceEndDate(this.recurrenceEndDate)
        .isAutoDecline(this.isAutoDecline);

    updater.accept(builder, value);
    return builder.build();
  }

  /**
   * Formats the event details for display in the user interface.
   *
   * @return a formatted string representation of the event suitable for display.
   */
  @Override
  public String formatForDisplay() {
    return String.format("• %s - %s to %s %s",
        name,
        startTime,
        endTime,
        location != null && !location.isEmpty() ? "- Location: " + location : "");
  }

  /**
   * Formats the event details for export to external formats.
   *
   * @return a string representation of the event suitable for export.
   */
  @Override
  public String formatForExport() {
    return CalendarExporter.formatEventAsCsvRow(
        this.name,
        this.startTime,
        this.endTime,
        this.description,
        this.location,
        this.visibility
    );
  }

  /**
   * Indicates whether this event should automatically decline conflicting events.
   *
   * @return true if this event should auto-decline conflicts, false otherwise.
   */
  @Override
  public boolean shouldAutoDecline() {
    return this.isAutoDecline;
  }

  /**
   * Creates and returns a new Builder instance for constructing Event objects.
   *
   * @return a new Builder instance.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder class for creating Event instances with proper validation.
   */
  public static class Builder {
    private String name;
    private Temporal startTime;
    private Temporal endTime;
    private String description;
    private String location;
    private EventVisibility visibility = EventVisibility.DEFAULT;
    private String recurringDays;
    private Integer occurrenceCount;
    private Temporal recurrenceEndDate;
    private boolean isAutoDecline = true;

    /**
     * Sets the name of the event.
     *
     * @param name the event name.
     * @return this Builder instance.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime the event start time.
     * @return this Builder instance.
     */
    public Builder startTime(Temporal startTime) {
      this.startTime = startTime;
      return this;
    }

    /**
     * Sets the end time of the event.
     * If endTime is null, the event will be set to last for one day starting
     * from the start time.
     *
     * @param endTime the event end time.
     * @return this Builder instance.
     * @throws IllegalStateException if startTime has not been set.
     */
    public Builder endTime(Temporal endTime) {
      if (startTime == null) {
        throw new IllegalStateException("Set startTime before setting endTime.\n");
      }
      if (endTime == null) {
        this.startTime = (TimeUtil.getLocalDateTimeFromTemporal(startTime)
            .toLocalDate().atStartOfDay());
        this.endTime = (TimeUtil.getLocalDateTimeFromTemporal(startTime)
            .toLocalDate().plusDays(1).atStartOfDay());
      } else {
        this.endTime = endTime;
      }
      return this;
    }

    /**
     * Sets the description of the event.
     *
     * @param description the event description.
     * @return this Builder instance.
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the location of the event.
     *
     * @param location the event location.
     * @return this Builder instance.
     */
    public Builder location(String location) {
      this.location = location;
      return this;
    }

    /**
     * Sets the visibility of the event.
     *
     * @param visibility the event visibility as a string.
     * @return this Builder instance.
     * @throws IllegalArgumentException if the visibility string is invalid.
     */
    public Builder visibility(String visibility) {
      this.visibility = visibility != null
          ? EventVisibility.getVisibility(visibility) : EventVisibility.DEFAULT;
      if (this.visibility == EventVisibility.UNKNOWN) {
        throw new IllegalArgumentException("Unknown Visibility input\n");
      }
      return this;
    }

    /**
     * Sets the recurring days for recurring events.
     * The format is a string of characters representing days of the week: M (Monday),
     * T (Tuesday), W (Wednesday), R (Thursday), F (Friday), S (Saturday), U (Sunday).
     * For example, "MWF" represents Monday, Wednesday, and Friday.
     *
     * @param recurringDays the string representing recurring days.
     * @return this Builder instance.
     */
    public Builder recurringDays(String recurringDays) {
      this.recurringDays = recurringDays;
      return this;
    }

    /**
     * Sets the occurrence count for recurring events.
     *
     * @param occurrenceCount the number of occurrences.
     * @return this Builder instance.
     */
    public Builder occurrenceCount(Integer occurrenceCount) {
      this.occurrenceCount = occurrenceCount;
      return this;
    }

    /**
     * Sets the end date for recurring events.
     *
     * @param recurrenceEndDate the date when recurrence ends.
     * @return this Builder instance.
     */
    public Builder recurrenceEndDate(Temporal recurrenceEndDate) {
      this.recurrenceEndDate = recurrenceEndDate;
      return this;
    }

    /**
     * Sets whether this event should auto-decline conflicting events.
     *
     * @param isAutoDecline true to auto-decline conflicts, false otherwise.
     * @return this Builder instance.
     */
    public Builder isAutoDecline(boolean isAutoDecline) {
      this.isAutoDecline = isAutoDecline;
      return this;
    }

    /**
     * Builds and returns a new Event instance with the current builder parameters.
     *
     * @return a new Event instance.
     * @throws IllegalArgumentException if any validation checks fail.
     */
    public Event build() {
      validateEventParameters();
      return new Event(this);
    }

    /**
     * Validates all event parameters before building the Event object.
     *
     * @throws IllegalArgumentException if any validation checks fail.
     */
    private void validateEventParameters() {
      if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException("Event name cannot be empty");
      }

      if (startTime == null || (endTime != null && isFirstBeforeSecond(endTime, startTime))) {
        throw new IllegalArgumentException("Event end time cannot be before start time");
      }

      if (recurringDays != null && !recurringDays.isEmpty()) {
        if (!recurringDays.matches("^[MTWRFSU]+$")) {
          throw new IllegalArgumentException("Invalid recurring days format. "
              + "Use M,T,W,R,F,S,U for days of week");
        }

        if (occurrenceCount == null && recurrenceEndDate == null) {
          throw new IllegalArgumentException("Recurring events require either occurrence "
              + "count or end date");
        }

        if (occurrenceCount != null && occurrenceCount <= 0) {
          throw new IllegalArgumentException("Occurrence count must be greater than 0");
        }

        if (recurrenceEndDate != null && isFirstBeforeSecond(recurrenceEndDate, endTime)) {
          throw new IllegalArgumentException("Recurrence end date must be after end date");
        }

        LocalDateTime startDateTime = TimeUtil.getLocalDateTimeFromTemporal(startTime);
        LocalDateTime endDateTime = TimeUtil.getLocalDateTimeFromTemporal(endTime);
        LocalDateTime nextDayStart = startDateTime.toLocalDate().plusDays(1)
            .atStartOfDay();

        if (endDateTime.isAfter(nextDayStart)) {
          throw new IllegalArgumentException("Recurring events cannot span more than one day");
        }
      } else {
        if (occurrenceCount != null || recurrenceEndDate != null) {
          throw new IllegalArgumentException("Recurring events require recurring days of week");
        }
      }
    }
  }

  /**
   * Returns a string representation of this event.
   *
   * @return a string representation of all event properties.
   */
  @Override
  public String toString() {
    return "Name: " + name + " " + "Start Time: " + startTime + " " + "End Time: " + endTime + " "
        + "Description: " + description + " " + "Location: " + location + " " + "Visibility: "
        + visibility + " " + "Recurring Days: " + recurringDays + " " + "Occurrence Count: "
        + occurrenceCount + " " + "Recurrence End Date: " + recurrenceEndDate + " "
        + "Auto Decline: " + isAutoDecline + "\n";
  }

  // TODO: Write this better

  /**
   * Compares this event with another object for equality.
   *
   * @param obj the object to compare with.
   * @return true if the events are equal, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Event)) {
      return false;
    }
    Event other = (Event) obj;
    return name.equals(other.name)
        && startTime.equals(other.startTime)
        && endTime.equals(other.endTime);
  }

  /**
   * Returns a hash code value for this event.
   *
   * @return a hash code based on name, start time, and end time.
   */
  @Override
  public int hashCode() {
    return name.hashCode() + startTime.hashCode() + endTime.hashCode();
  }

  /**
   * Determines if this event has any time intersection with the specified time range.
   *
   * @param startTime the start of the time range to check.
   * @param endTime   the end of the time range to check.
   * @return true if there is any intersection, false otherwise.
   */
  @Override
  public boolean hasIntersectionWith(Temporal startTime, Temporal endTime) {
    return overlapsWith(startTime, endTime);
  }

  /**
   * Helper method to check if this event's time period overlaps with the specified time period.
   *
   * @param otherStartTime the start of the time period to check.
   * @param otherEndTime   the end of the time period to check.
   * @return true if there is an overlap, false otherwise.
   */
  private boolean overlapsWith(Temporal otherStartTime, Temporal otherEndTime) {
    return isFirstBeforeSecond(this.startTime, otherEndTime)
        && isFirstAfterSecond(this.endTime, otherStartTime);
  }

  /**
   * Find the difference between start date times.
   *
   * @param event Other event to check with.
   * @return Difference between two times in int.
   */
  public int getDifference(IEvent event) {
    if (!(event instanceof Event)) {
      return 0;
    }
    event = (Event) event;
    return Math.toIntExact(TimeUtil.difference(this.startTime, ((Event) event).startTime));
  }
}