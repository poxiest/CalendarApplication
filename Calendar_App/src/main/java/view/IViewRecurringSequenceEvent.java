package view;

/**
 * An interface which outlines methods used by view specific representations
 * of RecurringSequenceEvents. The methods specifically include getters and
 * setters for values specific to RecurringSequenceEvents. This interface
 * is view specific.
 */
public interface IViewRecurringSequenceEvent extends IViewRecurringEvent {

  /**
   * Gets the sequence size string, which is
   * the number of occurrences specified for
   * this recurring sequence.
   * @return the sequence size.
   */
  public String getSequenceSizeString();

  /**
   * Sets the sequence size string, which is
   * the number of occurrences specified for
   * this recurring sequence.
   * @param sequenceSizeString the sequence size string to set.
   */
  public void setSequenceSizeString(String sequenceSizeString);

}
