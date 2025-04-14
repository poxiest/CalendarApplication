package components;

import java.time.YearMonth;

/**
 * Interface defining operations for the month panel component.
 * This allows the view to update the component.
 */
public interface IMonthPanel extends IComponent {

  /**
   * Sets the current month for this calendar. 
   * @param month the month to set.
   */
  public void setMonth(YearMonth month);

}
