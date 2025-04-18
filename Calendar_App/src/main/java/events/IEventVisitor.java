package events;

/**
 * A visitor interface for visiting different types of calendar events.
 *
 * @param <R> the return type of the visitor operation
 */
public interface IEventVisitor<R> {

  /**
   * Visit a basic single event.
   *
   * @param e the event to visit
   * @return the result of the visit
   */
  R visitEvent(Event e);

  /**
   * Visit a recurring sequence event.
   *
   * @param e the recurring sequence event to visit
   * @return the result of the visit
   */
  R visitRecurringSequence(RecurringSequenceEvent e);

  /**
   * Visit a recurring event with an end date.
   *
   * @param e the recurring until event to visit
   * @return the result of the visit
   */
  R visitRecurringUntil(RecurringUntilEvent e);
}
