package components;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import javax.swing.JPanel;
import view.IViewEvent;
import view.ViewEvent;

/**
 * This class represents a form with fields pertaining to single events. This
 * class supports getting and setting fields in the form.
 */
public class SingleEventForm extends AbstractEventForm<IViewEvent> {

  /**
   * A constructor for a single event form which is provided
   * with an actionCommand and title for the submit button,
   * as well as an optional header panel and the current
   * date for setting default values. This constructor also
   * takes a feature listener which sends an event to the controller
   * when this form is submitted.
   * @param actionCommand the action command for the submit button.
   * @param submitButtonTitle the title for the submit button.
   * @param featureListener the feature listener to react to submit actions.
   * @param headerPanel an optional header JPanel to render above the form.
   * @param currentDate the current date.
   */
  public SingleEventForm(
      String actionCommand, 
      String submitButtonTitle,
      ActionListener featureListener,
      JPanel headerPanel,
      LocalDate currentDate
  ) {
    super(actionCommand, submitButtonTitle, featureListener, headerPanel, currentDate);
    this.render();
  }

  /**
   * Renders this form.
   */
  public void render() {
    renderCommonFields();
    constraints.gridy += 1;
    constraints.gridx = 0;
    constraints.anchor = GridBagConstraints.LINE_END;
    this.add(submitButton, constraints);
  }

  /**
   * Gets the event model associated with the fields in this form.
   * All fields correspond directly to an IViewEvent.
   */
  public IViewEvent getEventModel() {
    this.eventModel = new ViewEvent();
    eventModel.setSubject(subjectField.getText());
    eventModel.setIsAllDayEvent(allDayCheckbox.isSelected());
    eventModel.setStartDateString(startDateTimePanel.getDate());
    eventModel.setStartTimeString(startDateTimePanel.getTime());
    eventModel.setEndDateString(endDateTimePanel.getDate());
    eventModel.setEndTimeString(endDateTimePanel.getTime());
    eventModel.setDescription(subjectField.getText());
    eventModel.setLocation(locationField.getText());
    eventModel.setIsPrivate(this.isPrivateCheckbox.isSelected());
    return eventModel;
  }

}
