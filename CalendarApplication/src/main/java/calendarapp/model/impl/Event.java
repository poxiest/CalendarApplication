package calendarapp.model.impl;

import java.time.temporal.Temporal;

import calendarapp.model.EventVisibility;
import calendarapp.model.IEvent;
import calendarapp.utils.TimeUtil;

import static calendarapp.utils.TimeUtil.isEqual;
import static calendarapp.utils.TimeUtil.isFirstAfterSecond;
import static calendarapp.utils.TimeUtil.isFirstBeforeSecond;

public class Event implements IEvent {
  private final String name;
  private final Temporal startTime;
  private final Temporal endTime;
  private final String description;
  private final String location;
  private final EventVisibility visibility;
  private final String recurringDays;
  private final Integer occurrenceCount;
  private final Temporal recurrenceEndDate;
  private final boolean isAutoDecline;

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

  @Override
  public boolean conflictsWith(IEvent other) {
    if (!(other instanceof Event)) {
      return false;
    }
    Event otherEvent = (Event) other;
    return overlapsWith(otherEvent.startTime, otherEvent.endTime);
  }

  @Override
  public boolean isActiveAt(Temporal dateTime) {
    return isEqual(dateTime, this.startTime) ||
        isEqual(dateTime, this.endTime) ||
        (isFirstAfterSecond(dateTime, this.startTime) &&
            isFirstBeforeSecond(dateTime, this.endTime));
  }

  @Override
  public boolean matchesName(String eventName) {
    return eventName == null || this.name.equals(eventName);
  }

  @Override
  public boolean isWithinTimeRange(Temporal startDateTime, Temporal endDateTime) {
    boolean afterStart = startDateTime == null ||
        isFirstAfterSecond(this.startTime, startDateTime) ||
        isEqual(this.startTime, startDateTime);
    boolean beforeEnd = endDateTime == null ||
        isFirstBeforeSecond(this.endTime, endDateTime) ||
        isEqual(this.endTime, endDateTime);
    return afterStart && beforeEnd;
  }

  @Override
  public IEvent updateProperty(String property, String value) {
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

    switch (property.toLowerCase()) {
      case EventConstants.PropertyKeys.NAME:
        builder.name(value);
        break;

      case EventConstants.PropertyKeys.START_TIME:
        builder.startTime(TimeUtil.getLocalDateTimeFromString(value));
        break;

      case EventConstants.PropertyKeys.END_TIME:
        builder.endTime(TimeUtil.getLocalDateTimeFromString(value));
        break;

      case EventConstants.PropertyKeys.DESCRIPTION:
        builder.description(value);
        break;

      case EventConstants.PropertyKeys.LOCATION:
        builder.location(value);
        break;

      case EventConstants.PropertyKeys.VISIBILITY:
        builder.visibility(value);
        break;

      // TODO: Add logic for changing recurring properties
      default:
        throw new IllegalArgumentException("Cannot edit property: " + property + "\n");
    }

    return builder.build();
  }

  @Override
  public String formatForDisplay() {
    return String.format("%s - %s to %s %s",
        name,
        startTime,
        endTime,
        location != null && !location.isEmpty() ? "- Location: " + location : "");
  }

  @Override
  public String formatForExport() {
    // Use CalendarExporter's helper method to format for CSV export
    return CalendarExporter.formatEventAsCsvRow(
        this.name,
        this.startTime,
        this.endTime,
        this.description,
        this.location,
        this.visibility
    );
  }

  @Override
  public boolean shouldAutoDecline() {
    return this.isAutoDecline;
  }

  public static Builder builder() {
    return new Builder();
  }

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

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder startTime(Temporal startTime) {
      this.startTime = startTime;
      return this;
    }

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

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder location(String location) {
      this.location = location;
      return this;
    }

    public Builder visibility(String visibility) {
      this.visibility = visibility != null ?
          EventVisibility.getVisibility(visibility) : EventVisibility.DEFAULT;
      if (this.visibility == EventVisibility.UNKNOWN) {
        throw new IllegalArgumentException("Unknown Visibility input\n");
      }
      return this;
    }

    public Builder recurringDays(String recurringDays) {
      this.recurringDays = recurringDays;
      return this;
    }

    public Builder occurrenceCount(Integer occurrenceCount) {
      this.occurrenceCount = occurrenceCount;
      return this;
    }

    public Builder recurrenceEndDate(Temporal recurrenceEndDate) {
      this.recurrenceEndDate = recurrenceEndDate;
      return this;
    }

    public Builder isAutoDecline(boolean isAutoDecline) {
      this.isAutoDecline = isAutoDecline;
      return this;
    }

    public Event build() {
      validateEventParameters();
      return new Event(this);
    }

    private void validateEventParameters() {
      if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException("Event name cannot be empty");
      }

      if (startTime == null || (endTime != null && isFirstBeforeSecond(endTime, startTime))) {
        throw new IllegalArgumentException("Event start time cannot be before end time");
      }

      if (recurringDays != null && !recurringDays.isEmpty()) {
        if (!recurringDays.matches("^[MTWRFSU]+$")) {
          throw new IllegalArgumentException("Invalid recurring days format. Use M,T,W,R,F,S,U for days of week");
        }

        if (occurrenceCount == null && recurrenceEndDate == null) {
          throw new IllegalArgumentException("Recurring events require either occurrence count or end date");
        }

        if (occurrenceCount != null && occurrenceCount <= 0) {
          throw new IllegalArgumentException("Occurrence count must be greater than 0");
        }

        if (recurrenceEndDate != null && isFirstBeforeSecond(recurrenceEndDate, endTime)) {
          throw new IllegalArgumentException("Recurrence end date must be after end date");
        }

        // TODO: Add a validation to check that recurring events should not cross 1 day
      } else {
        if (occurrenceCount != null || recurrenceEndDate != null) {
          throw new IllegalArgumentException("Recurring events require recurring days of week");
        }
      }
    }
  }

  @Override
  public String toString() {
    return "Name: " + name + " " + "Start Time: " + startTime + " " + "End Time: " + endTime + " " + "Description: " + description + " " + "Location: " + location + " " + "Visibility: " + visibility + " " + "Recurring Days: " + recurringDays + " " + "Occurrence Count: " + occurrenceCount + " " + "Recurrence End Date: " + recurrenceEndDate + " " + "Auto Decline: " + isAutoDecline + "\n";
  }

  @Override
  public boolean hasIntersectionWith(Temporal startTime, Temporal endTime) {
    return overlapsWith(startTime, endTime);
  }

  private boolean overlapsWith(Temporal otherStartTime, Temporal otherEndTime) {
    return isFirstBeforeSecond(this.startTime, otherEndTime) && isFirstAfterSecond(this.endTime, otherStartTime);
  }

  // TODO: Override Equals() and Hashcode() functions
}