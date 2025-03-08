package calendarapp.model.event;

import java.time.temporal.Temporal;

public interface IEvent {
  String getName();

  Temporal getStartDateTime();

  Temporal getEndDateTime();

  String getDescription();

  String getLocation();

  EventVisibility getVisibility();

  String getRecurringDays();

  Integer getOccurrenceCount();

  Temporal getRecurrenceEndDate();

  boolean isAutoDecline();

  boolean conflictsWith(IEvent other);
}
