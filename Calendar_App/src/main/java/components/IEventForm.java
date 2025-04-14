package components;

/**
 * This interface outlines getter and setter methods for a generic event
 * form which is extended by the single and recurring event forms.
 * @param <T> the type of model IViewEvent | IViewRecurring event to associate
 *            with this form.
 */
public interface IEventForm<T> {

  /**
   * Gets the event model from the form.
   * @return the event model.
   */
  public T getEventModel();

  /**
   * Sets the subject in this form.
   * @param subject the subject to set.
   */
  public void setSubject(String subject);

  /**
   * Sets the description of this form.
   * @param description the description to set.
   */
  public void setDescription(String description);

  /**
   * Sets the location of this form.
   * @param location the location to set.
   */
  public void setLocation(String location);

  /**
   * Sets the startDate of this form.
   * @param startDateString the start date to set.
   */
  public void setStartDate(String startDateString);

  /**
   * Sets the startTime of this form.
   * @param startTimeString the start time to set.
   */
  public void setStartTime(String startTimeString);

  /**
   * Sets the endDate of this form.
   * @param endDateString the end date to set.
   */
  public void setEndDate(String endDateString);

  /**
   * Sets the endTime of this form.
   * @param endTimeString the end time to set.
   */
  public void setEndTime(String endTimeString);

  /**
   * Clicks the all day checkbox.
   */
  public void clickIsAllDayCheckbox();

  /**
   * Clicks th is private checkbox.
   */
  public void clickIsPrivateCheckbox();

}
