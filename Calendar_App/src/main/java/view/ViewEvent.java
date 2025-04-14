package view;

/**
 * This is a representation of an Event that is compromised of text fields.
 * This representation has all of the same fields as Event instances
 * used by the model. This class is only used by the view and is used to validate 
 * fields in the controller. This class supports getting and setting string field values.
 */
public class ViewEvent implements IViewEvent {
  private String subject;
  private String startDateString;
  private String startTimeString;
  private String endDateString;
  private String endTimeString;
  private Boolean isAllDayEvent;
  private String description;
  private String location;
  private Boolean isPrivate;
  private String id;

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public String getStartDateString() {
    return this.startDateString;
  }

  @Override
  public String getEndDateString() {
    return this.endDateString;
  }

  @Override
  public String getStartTimeString() {
    return this.startTimeString;
  }

  @Override
  public String getEndTimeString() {
    return this.endTimeString;
  }

  @Override
  public Boolean isAllDayEvent() {
    return this.isAllDayEvent;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getLocation() {
    return this.location;
  }

  @Override
  public Boolean isPrivate() {
    return this.isPrivate;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public void setSubject(String subject) {
    this.subject = subject;
  }

  @Override
  public void setStartDateString(String startDateString) {
    this.startDateString = startDateString;
  }

  @Override
  public void setEndDateString(String endDateString) {
    this.endDateString = endDateString;
  }

  @Override
  public void setStartTimeString(String startTimeString) {
    this.startTimeString = startTimeString;
  }

  @Override
  public void setEndTimeString(String endTimeString) {
    this.endTimeString = endTimeString;
  }

  @Override
  public void setIsAllDayEvent(Boolean isAllDayEvent) {
    this.isAllDayEvent = isAllDayEvent;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setLocation(String location) {
    this.location = location;
  }

  @Override
  public void setIsPrivate(Boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

}
