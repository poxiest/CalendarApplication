package controller;

import view.IView;

/**
 * An interface for a controller with a view. This outlines
 * one method which sets the view instance for this controller.
 */
public interface IControllerWithView {

  /**
   * Sets the view instance to this controller.
   * @param view the view instance to set.
   */
  public void setView(IView view);
  
}
