package calendarapp.model.impl;

import java.time.temporal.Temporal;

import calendarapp.model.EventVisibility;
import calendarapp.model.IEvent;
import calendarapp.utils.TimeUtil;

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
  public String getName() {
    return name;
  }

  @Override
  public Temporal getStartDateTime() {
    return startTime;
  }

  @Override
  public Temporal getEndDateTime() {
    return endTime;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getLocation() {
    return location;
  }

  @Override
  public EventVisibility getVisibility() {
    return visibility;
  }

  @Override
  public String getRecurringDays() {
    return recurringDays;
  }

  @Override
  public Integer getOccurrenceCount() {
    return occurrenceCount;
  }

  @Override
  public Temporal getRecurrenceEndDate() {
    return recurrenceEndDate;
  }

  @Override
  public boolean isAutoDecline() {
    return isAutoDecline;
  }

  @Override
  public boolean conflictsWith(IEvent other) {
    return isFirstBeforeSecond(this.getStartDateTime(), other.getEndDateTime())
        && isFirstAfterSecond(this.getEndDateTime(), other.getStartDateTime());
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
    private EventVisibility visibility = EventVisibility.PUBLIC;
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
            .toLocalDate().atTime(0, 0, 0));
        this.endTime = (TimeUtil.getLocalDateTimeFromTemporal(startTime)
            .toLocalDate().atTime(23, 59, 59));
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

    public Builder visibility(EventVisibility visibility) {
      this.visibility = visibility;
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

        if (recurrenceEndDate != null && isFirstBeforeSecond(recurrenceEndDate, startTime)) {
          throw new IllegalArgumentException("Occurrence count must be greater than 0");
        }
      }
    }
  }

  public String toString() {
    return "Name: " + name + " " +
        "Start Time: " + startTime + " " +
        "End Time: " + endTime + " " +
        "Description: " + description + " " +
        "Location: " + location + " " +
        "Visibility: " + visibility + " " +
        "Recurring Days: " + recurringDays + " " +
        "Occurrence Count: " + occurrenceCount + " " +
        "Recurrence End Date: " + recurrenceEndDate + " " +
        "Auto Decline: " + isAutoDecline + "\n";
  }
}