package components;

import java.util.List;
import view.IViewEvent;
import view.IViewRecurringEvent;

/**
 * This interface outlines methods specific to the events list
 * panel component, which displays a list of events on a given
 * day.
 */
public interface IEventsListPanel extends IComponent {

  /**
   * Sets the event in the list. This component
   * uses view events, which are view specific representations
   * of events.
   * @param events the view events to set.
   */
  public void setEvents(List<IViewEvent> events);

  /**
   * Gets the viewEvent from the single event
   * form within this component.
   * @return the view event.
   */
  public IViewEvent getEditSingleEvent();

  /**
   * Gets the id of the currently selected event
   * in the list.
   * @return the id of the event.
   */
  public String getSelectedEventId();

  /**
   * Gets the ViewRecurringEvent from the recurring
   * event form within this component.
   * @return the view recurring event.
   */
  public IViewRecurringEvent getEditRecurringEvent();

}
