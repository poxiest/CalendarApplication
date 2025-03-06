package calendarapp.model;

public class Event implements IEvent {

  private String eventName;
  private String from;
  private String to;
  private String on;
  private String repeats;
  private String times;
  private String until;
  private String description;
  private String location;
  private String scope;
  private boolean autoDecline;

  private Event(Builder builder) {

  }

  @Override
  public String getEventName() {
    return this.eventName;
  }

  public static class Builder {
    private String eventName;
    private String from;
    private String to;
    private String on;
    private String repeats;
    private String times;
    private String until;
    private String description;
    private String location;
    private String scope;
    private boolean autoDecline;

    public Builder eventName() {}

    public Builder from(String from) {
      this.from = from;
      return this;
    }

    public IEvent build() {
      return new Event(this);
    }
  }
}
