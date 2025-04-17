package events;

public class OnlineEvent implements IEventVisitor<Boolean> {
  @Override
  public Boolean visitEvent(Event e) {
    return isOnline(e.getLocation());
  }

  @Override
  public Boolean visitRecurringSequence(RecurringSequenceEvent e) {
    return isOnline(e.getLocation());
  }

  @Override
  public Boolean visitRecurringUntil(RecurringUntilEvent e) {
    return isOnline(e.getLocation());
  }

  private Boolean isOnline(String location) {
    if (location == null) {
      return false;
    }
    return location.equalsIgnoreCase("online");
  }
}
