package events;

/**
 * An interface for recurring sequence events which have a sequence
 * size and an index in the recurring sequence.
 */
public interface IRecurringSequenceEvent extends IRecurringEvent<IRecurringSequenceEvent> {

  /**
   * Gets the total number of events in this
   * recurring event sequence.
   * @return the number of events in the sequence.
   */
  public int getSequenceSize();

}
