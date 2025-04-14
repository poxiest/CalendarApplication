package view;

/**
 * This interface outlines shared methods by RecurringEvent types.
 * Recurring event types include RecurringSequenceEvents, which
 * run for a set number of times, and RecurringUntilEvents, which
 * run until a specified date and time. This interface
 * is view specific.
 */
public interface IViewRecurringEvent extends IViewEvent {

  /**
   * Gets the days of the week that this RecurringEvent
   * repeats on in the form of a string. An example string
   * is "MTW" indicating that this event runs on Monday,
   * Tuesday, and Wednesday.
   * @return the weekdays relevant to this RecurringEvent.
   */
  public String getWeekdaysString();

  /**
   * Gets the unique id associated with the RecurringSequence
   * that this event is a part of. All RecurringEvents have
   * a corresponding recurringSequenceId and all events in 
   * the same series share the same recurringSequenceId.
   * @return the recurringSequenceId for this event.
   */
  public String getRecurringSequenceIdString();

  /**
   * Sets the days of the week that this RecurringEvent
   * repeats on in the form of a string. An example string
   * is "MTW" indicating that this event runs on Monday,
   * Tuesday, and Wednesday.
   * @param weekdays the weekday string to set.
   */
  public void setWeekdaysString(String weekdays);

  /**
   * Sets the unique id associated with the RecurringSequence
   * that this event is a part of. All RecurringEvents have
   * a corresponding recurringSequenceId and all events in 
   * the same series share the same recurringSequenceId.
   * @param sequenceId the recurringSequenceId to set for this event.
   */
  public void setRecurringSequenceIdString(String sequenceId);

}
