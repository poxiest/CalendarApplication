package view;

/**
 * This is a representation of a RecurringUntilEvent that is compromised of text fields.
 * This representation has all of the same fields as RecurringUntilEvent instances
 * used by the model. This class is only used by the view and is used to validate 
 * fields in the controller. This class supports getting and setting string field values.
 */
public class ViewRecurringUntilEvent extends AbstractViewRecurringEvent 
    implements IViewRecurringUntilEvent {
  private String onDateString;
  private String onTimeString;

  @Override
  public String getOnDateString() {
    return this.onDateString;
  }

  @Override
  public String getOnTimeString() {
    return this.onTimeString;
  }

  @Override
  public void setOnDateString(String onDateString) {
    this.onDateString = onDateString;
  }

  @Override
  public void setOnTimeString(String onTimeString) {
    this.onTimeString = onTimeString;
  }

}
