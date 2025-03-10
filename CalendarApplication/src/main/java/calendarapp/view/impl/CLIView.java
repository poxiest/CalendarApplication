package calendarapp.view.impl;

import java.io.IOException;
import java.util.List;

import calendarapp.model.IEvent;
import calendarapp.view.ICalendarView;

public class CLIView implements ICalendarView {

  private final Appendable out;

  public CLIView(Appendable out) {
    this.out = out;
  }

  @Override
  public void displayMessage(String message) {
    try {
      out.append(message);
    } catch (IOException e) {
      throw new RuntimeException("Append failed : ", e);
    }
  }

  @Override
  public void displayEvents(List<IEvent> events) {
    StringBuilder outputString = new StringBuilder();
    outputString.append(String.format("%-20s %-20s %-20s %-15s %-15s %-15s %-15s %-20s %-20s %-50s%n",
        "Event Name", "Start Time", "End Time", "Visibility", "Occurrences", "Auto Decline",
        "Recurring Days", "Recurrence End Date", "Location", "Description"));
    outputString.append("-------------------------------------------------------------------" +
        "------------------------------------------------------------------------------------" +
        "-------------------------------\n");
    for (IEvent event : events) {
      outputString.append(String.format("%-20s %-20s %-20s %-15s %-15d %-15b %-15s %-20s %-20s %-50s%n",
          event.getName(),
          event.getStartDateTime(),
          event.getEndDateTime(),
          event.getVisibility(),
          event.getOccurrenceCount(),
          event.isAutoDecline(),
          event.getRecurringDays(),
          event.getRecurrenceEndDate(),
          event.getLocation(),
          event.getDescription()));
    }
    try {
      out.append(outputString.toString()).append(String.valueOf('\n'));
    } catch (IOException e) {
      throw new RuntimeException("Append failed : ", e);
    }
  }
}
