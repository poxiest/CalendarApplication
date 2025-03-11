package calendarapp.model;

import java.time.temporal.Temporal;

public interface IEvent {

  boolean conflictsWith(IEvent other);

  boolean isActiveAt(Temporal dateTime);

  boolean matchesName(String eventName);

  boolean isWithinTimeRange(Temporal startDateTime, Temporal endDateTime);

  IEvent updateProperty(String property, String value);

  String formatForDisplay();

  String formatForExport();

  boolean shouldAutoDecline();
}