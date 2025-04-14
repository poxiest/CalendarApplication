package components;

import java.time.LocalDateTime;

import javax.swing.JFormattedTextField;

/**
 * This interface represents the methods upheld by the date time
 * field panel which is a combination of formatted JFormattedTextFields
 * with a date and a time input.
 */
public interface IDateTimeFieldPanel {

  /**
   * Gets the time from this component.
   * @return the time.
   */
  public String getTime();

  /**
   * Gets the date from this component.
   * @return the date.
   */
  public String getDate();

  /**
   * Sets the visibility of the time field.
   * @param visible true if visible, false otherwise.
   */
  public void setTimeVisible(boolean visible);

  /**
   * Sets the visibility of the date field.
   * @param visible true if visible, false otherwise.
   */
  public void setDateVisible(boolean visible);
  
  /**
   * Sets the local time associated with this component
   * used for setting default values.
   * @param dateTime the datetime to set.
   */
  public void setLocalDateTime(LocalDateTime dateTime);

  /**
   * Gets a reference to the date field, used for implementing
   * listeners in forms.
   * @return the date field reference.
   */
  public JFormattedTextField getDateField();

  /**
   * Gets a reference to the time field, used for implementing
   * listeners in forms.
   * @return the time field reference.
   */
  public JFormattedTextField getTimeField();

  /**
   * Sets the text of the date field.
   * @param dateString the date to set.
   */
  public void setDate(String dateString);

  /**
   * Sets the text of the time field.
   * @param timeString the time to set.
   */
  public void setTime(String timeString);

}
