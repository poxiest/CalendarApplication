package view;

/**
 * This is a representation of a RecurringSequenceEvent that is compromised of text fields.
 * This representation has all of the same fields as RecurringSequenceEvent instances
 * used by the model. This class is only used by the view and is used to validate 
 * fields in the controller. This class supports getting and setting string field values.
 */
public class ViewRecurringSequenceEvent extends AbstractViewRecurringEvent 
    implements IViewRecurringSequenceEvent {
  private String sequenceSizeString;

  @Override
  public String getSequenceSizeString() {
    return this.sequenceSizeString;
  }

  @Override
  public void setSequenceSizeString(String sequenceSizeString) {
    this.sequenceSizeString = sequenceSizeString;
  }

}
