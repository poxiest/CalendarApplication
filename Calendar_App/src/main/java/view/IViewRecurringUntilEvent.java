package view;

/**
 * An interface which outlines methods used by view specific representations
 * of RecurringUntilEvents. The methods specifically include getters and
 * setters for values specific to RecurringUntilEvents.
 */
public interface IViewRecurringUntilEvent extends IViewRecurringEvent {

  /**
   * Gets the onDateString, which is a string representation
   * of the onDate which this RecurringUntilEvent stops on.
   * @return the OnDateString.
   */
  public String getOnDateString();

  /**
   * Gets the onTimeString, which is a string representation
   * of the onTime which this RecurringUntilEvent stops on.
   * @return the OnTimeString.
   */
  public String getOnTimeString();

  /**
   * Sets the onDateString, which is a string representation
   * of the onDate which this RecurringUntilEvent stops on.
   * @param onDateString the onDateString to set.
   */
  public void setOnDateString(String onDateString);

  /**
   * Sets the onTimeString, which is a string representation
   * of the onTime which this RecurringUntilEvent stops on.
   * @param onTimeString the onDateString to set.
   */
  public void setOnTimeString(String onTimeString);

}
