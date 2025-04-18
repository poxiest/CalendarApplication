package events;

/**
 * A visitor that determines whether an event is online.
 * An event is considered online if its location is "online" (case-insensitive).
 */
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

  /**
   * Checks whether the provided location string indicates an online event.
   *
   * @param location the location string to check
   * @return true if the location is "online", false otherwise
   */
  private Boolean isOnline(String location) {
    if (location == null) {
      return false;
    }
    return location.equalsIgnoreCase("online");
  }
}
