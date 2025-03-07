package calendarapp.model.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IEvent {
  String getName();

  LocalDateTime getStartDateTime();

  LocalDateTime getEndDateTime();

  String getDescription();

  String getLocation();

  EventVisibility getVisibility();

  String getRecurringDays();

  Integer getOccurrenceCount();

  LocalDate getRecurrenceEndDate();

  boolean isAutoDecline();

  boolean conflictsWith(IEvent other);
}
