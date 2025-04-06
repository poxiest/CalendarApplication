package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.function.BiConsumer;

import calendarapp.model.EventVisibility;
import calendarapp.model.IEvent;
import calendarapp.utils.TimeUtil;

import static calendarapp.utils.TimeUtil.getStartOfNextDay;
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
   * Creates and returns a new Builder instance for constructing Event objects.
   *
   * @return a new Builder instance.
   */
  public static Builder builder() {
    return new Builder();
  }

  public String getName() {
    return name;
  }

  public Temporal getStartTime() {
    return startTime;
  }

  public Temporal getEndTime() {
    return endTime;
  }

  public String getDescription() {
    return description;
  }

  public String getLocation() {
    return location;
  }

  public EventVisibility getVisibility() {
    return visibility;
  }

  public String getRecurringDays() {
    return recurringDays;
  }

  public Integer getOccurrenceCount() {
    return occurrenceCount;
  }

  public Temporal getRecurrenceEndDate() {
    return recurrenceEndDate;
  }

  /**
   * Creates a new event with the specified property updated to the given value.
   * Supported properties are defined in {@link Constants.PropertyKeys}.
   *
   * @param property the property to update.
   * @param value    the new value for the property.
   * @return a new Event instance with the updated property.
   * @throws IllegalArgumentException if the property is not supported or the value is invalid.
   */
  public Event updateProperty(String property, String value) {
    BiConsumer<Builder, String> updater = EventPropertyUpdater.getUpdater(property);
    if (updater == null) {
      throw new IllegalArgumentException("Cannot edit property: " + property + "\n");
    }
    Builder builder = new Builder(this);
    updater.accept(builder, value);
    return builder.build();
  }

  @Override
  public IEvent deepCopyEvent() {
    return new Builder(this).build();
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, startTime, endTime, description, location, visibility,
        recurringDays, occurrenceCount, recurrenceEndDate, isAutoDecline);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Event)) {
      return false;
    }

    Event other = (Event) obj;
    if (!name.equals(other.name)) {
      return false;
    }
    if (!startTime.equals(other.startTime)) {
      return false;
    }
    if (!endTime.equals(other.endTime)) {
      return false;
    }
    if (!Objects.equals(description, other.description)) {
      return false;
    }
    if (!Objects.equals(location, other.location)) {
      return false;
    }
    if (!Objects.equals(visibility, other.visibility)) {
      return false;
    }
    if (!Objects.equals(recurringDays, other.recurringDays)) {
      return false;
    }
    if (!Objects.equals(occurrenceCount, other.occurrenceCount)) {
      return false;
    }
    if (!Objects.equals(recurrenceEndDate, other.recurrenceEndDate)) {
      return false;
    }
    return isAutoDecline == other.isAutoDecline;
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
     * Default constructor for the Builder class. This constructor does not perform
     * any initialization and is required to allow for the instantiation of the
     * Builder object without needing to provide an initial Event object.
     * The builder pattern allows properties to be set incrementally.
     */
    public Builder() {
      // No initialization is performed here.
    }

    /**
     * Constructs a new Builder object initialized with the values from the given Event object.
     *
     * @param event The Event object whose properties are copied into the Builder.
     */
    public Builder(Event event) {
      this.name = event.name;
      this.startTime = event.startTime;
      this.endTime = event.endTime;
      this.description = event.description;
      this.location = event.location;
      this.visibility = event.visibility;
      this.recurringDays = event.recurringDays;
      this.occurrenceCount = event.occurrenceCount;
      this.recurrenceEndDate = event.recurrenceEndDate;
      this.isAutoDecline = event.isAutoDecline;
    }

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
        this.startTime = TimeUtil.getStartOfDay(startTime);
        this.endTime = TimeUtil.getStartOfNextDay(startTime);
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

        if (isFirstAfterSecond(endTime, getStartOfNextDay(startTime))) {
          throw new IllegalArgumentException("Recurring events cannot span more than one day");
        }
      } else {
        if (occurrenceCount != null || recurrenceEndDate != null) {
          throw new IllegalArgumentException("Recurring events require recurring days of week");
        }
      }
    }
  }
}