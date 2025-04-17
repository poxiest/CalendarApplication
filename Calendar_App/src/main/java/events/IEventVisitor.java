package events;

public interface IEventVisitor<R> {
  R visitEvent(Event e);

  R visitRecurringSequence(RecurringSequenceEvent e);

  R visitRecurringUntil(RecurringUntilEvent e);
}
