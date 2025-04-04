package calendarapp.controller.impl;

import calendarapp.controller.Features;
import calendarapp.model.ICalendarModel;
import calendarapp.view.GUIView;

public class GUIController implements Features {

  /**
   * The calendar model that stores and manages calendar data.
   */
  private ICalendarModel model;

  /**
   * The view used for displaying information to the user.
   */
  private GUIView view;

  public GUIController(ICalendarModel model) {
    this.model = model;
  }

  @Override
  public void setView(GUIView view) {
    this.view = view;
    view.addFeatures(this);
  }
}
