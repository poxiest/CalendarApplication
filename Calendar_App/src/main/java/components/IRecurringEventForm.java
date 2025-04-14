package components;

import view.IViewRecurringEvent;

/**
 * Interface for a recurring event form which outlines setters
 * specific to a recurring event form.
 */
public interface IRecurringEventForm extends IEventForm<IViewRecurringEvent> {

  /**
   * Sets the values of the weekday checkbox field using
   * a string ("MTWRFSU") to set the corresponding weekday
   * checkboxes.
   * @param weekdaysString the string used to set the checkbox.
   */
  public void setWeekdaysCheckboxes(String weekdaysString);

  /**
   * Sets the on date field value of the form.
   * @param onDateString the on date string to set.
   */
  public void setOnDateString(String onDateString);

  /**
   * Sets the on time field value of the form.
   * @param onTimeString the on time string to set.
   */
  public void setOnTimeString(String onTimeString);

  /**
   * Sets the after field value of the form.
   * @param afterString the after string.
   */
  public void setAfterString(String afterString);

}
