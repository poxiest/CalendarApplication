package view;

/**
 * An abstract class which implements methods shared by ViewRecurringEvents.
 * These methods specifically include getters and setters for fields shared
 * by all RecurringEvents.
 */
public class AbstractViewRecurringEvent extends ViewEvent implements IViewRecurringEvent {
  String weekdaysString;
  String recurringSequenceId;

  @Override
  public String getWeekdaysString() {
    return this.weekdaysString;
  }

  @Override
  public String getRecurringSequenceIdString() {
    return this.recurringSequenceId;
  }

  @Override
  public void setRecurringSequenceIdString(String sequenceId) {
    this.recurringSequenceId = sequenceId;
  }

  @Override
  public void setWeekdaysString(String weekdaysString) {
    this.weekdaysString = weekdaysString;
  }
}
