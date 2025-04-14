package view;

/**
 * An interface which outlines methods used by view specific representations
 * of Events. The methods specifically include getters and
 * setters for values specific to RecurringUntilEvents. All events are either
 * String or Boolean corresponding to field values set by the user. This
 * interface is view specific.
 */
public interface IViewEvent {
  /**
   * Gets the unique id associated with this event.
   * @return the id.
   */
  public String getId();

  /**
   * Gets the subject of this event.
   * @return the subject of this event.
   */
  public String getSubject();

  /**
   * Gets the startDate string of this event.
   * @return the startDate of this event.
   */
  public String getStartDateString();

  /**
   * Gets the endDate string of this event.
   * @return the endDate of this event.
   */
  public String getEndDateString();

  /**
   * Gets the startTime of this event.
   * @return the startTime of this event.
   */
  public String getStartTimeString();

  /**
   * Gets the endTime of this event.
   * @return the endTime of this event.
   */
  public String getEndTimeString();

  /**
   * Gets the isAllDayEvent of this event.
   * @return the isAllDayEvent of this event.
   */
  public Boolean isAllDayEvent();

  /**
   * Gets the description of this event.
   * @return the description of this event.
   */
  public String getDescription();

  /**
   * Gets the location of this event.
   * @return the location of this event.
   */
  public String getLocation();

  /**
   * Gets the isPrivate of this event.
   * @return the isPrivate of this event.
   */
  public Boolean isPrivate();

  /**
   * Sets the id for this view event.
   * @param id the id to set.
   */
  public void setId(String id);

  /**
   * Sets the subject for this view event.
   * @param subject the subject to set.
   */
  public void setSubject(String subject);

  /**
   * Sets the startDateString for this view event.
   * @param startDateString the startDateString to set.
   */
  public void setStartDateString(String startDateString);

  /**
   * Sets the endDateString for this viewEvent.
   * @param endDateString the endDateString to set.
   */
  public void setEndDateString(String endDateString);

  /**
   * Sets the startTimeString for this view event.
   * @param startTimeString the startDateString to set.
   */
  public void setStartTimeString(String startTimeString);

  /**
   * Sets the endTimeString for this view event.
   * @param endTimeString the endTimeString to set.
   */
  public void setEndTimeString(String endTimeString);

  /**
   * Sets the isAllDayEvent field for this view event.
   * @param isAllDayEvent the isAllDayEvent value to set.
   */
  public void setIsAllDayEvent(Boolean isAllDayEvent);

  /**
   * Sets the description for this view event.
   * @param description the description to set.
   */
  public void setDescription(String description);

  /**
   * Sets the location for this view event.
   * @param location the location to set.
   */
  public void setLocation(String location);

  /**
   * Sets the isPrivate field for this view event.
   * @param isPrivate the isPrivate value to set.
   */
  public void setIsPrivate(Boolean isPrivate);

}
