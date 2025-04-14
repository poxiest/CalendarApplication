package components;

import java.time.LocalDate;

import view.IViewEvent;
import view.IViewRecurringEvent;

/**
 * This is an interface which represents the create events form panel
 * component, which is a container for single event and recurring
 * event forms that you can switch between using radio buttons.
 */
public interface ICreateEventsFormPanel extends IComponent {

  /**
   * Sets the current date of this component.
   * Used for default values.
   * @param currentDate the current date to set.
   */
  public void setLocalDate(LocalDate currentDate);

  /**
   * Gets the IViewEvent from the single event form.
   * @return the IViewEvent.
   */
  public IViewEvent getSingleEventModel();

  /**
   * Gets the IViewRecurringEvent from the recurring event
   * form.
   * @return the IViewRecurringEvent.
   */
  public IViewRecurringEvent getRecurringEventModel();

}
