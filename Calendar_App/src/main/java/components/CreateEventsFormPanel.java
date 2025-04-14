package components;

import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import view.IViewEvent;
import view.IViewRecurringEvent;

/**
 * This is an implementation of a create events form panel component
 * which is a container for single and recurring event forms that the
 * user can navigate between.
 */
public class CreateEventsFormPanel extends AbstractComponent implements ICreateEventsFormPanel {

  private JRadioButton[] eventTypeRadioButtons;
  private ActionListener featureActionListener;
  private IEventForm<IViewEvent> singleEventForm;
  private IEventForm<IViewRecurringEvent> recurringEventForm;
  private JPanel eventTypeButtonsPanel;
  private LocalDate currentDate;

  /**
   * This is a constructor for a create
   * events form panel implementation
   * that accepts a feature action listener
   * for communicating with the controller
   * and a current date for default values.
   * @param featureActionListener the feature action listener.
   * @param currentDate the current date.
   */
  public CreateEventsFormPanel(
      ActionListener featureActionListener,
      LocalDate currentDate
  ) {
    this.featureActionListener = featureActionListener;
    this.currentDate = currentDate;
    this.eventTypeRadioButtons = new JRadioButton[2];
    ButtonGroup eventTypeRadioButtonGroup = new ButtonGroup();
    this.eventTypeRadioButtons[0] = new JRadioButton("Single");
    this.eventTypeRadioButtons[1] = new JRadioButton("Recurring");
    eventTypeRadioButtonGroup.add(eventTypeRadioButtons[0]);
    eventTypeRadioButtonGroup.add(eventTypeRadioButtons[1]);
    this.eventTypeRadioButtons[0].doClick();
    this.eventTypeButtonsPanel = new JPanel();
    eventTypeButtonsPanel.add(new JLabel("Create Event: "));
    eventTypeButtonsPanel.add(eventTypeRadioButtons[0]);
    eventTypeButtonsPanel.add(eventTypeRadioButtons[1]);
    this.initializeActionListeners();
    this.render();
  }

  @Override
  protected void render() {
    this.setLayout(new GridBagLayout());
    if (this.eventTypeRadioButtons[0].isSelected()) {
      singleEventForm = new SingleEventForm(
          "create-single", 
          "Create", 
          featureActionListener, 
          eventTypeButtonsPanel, 
          currentDate);
      this.add((JPanel) singleEventForm);
    }
    else {
      recurringEventForm = new RecurringEventForm(
          "create-recurring", 
          "Create", 
          featureActionListener, 
          eventTypeButtonsPanel, 
          currentDate, 
          false);
      this.add((JPanel) recurringEventForm);
    }
  }
  
  @Override
  public void setLocalDate(LocalDate currentDate) {
    this.currentDate = currentDate;
  }

  @Override
  public IViewEvent getSingleEventModel() {
    return singleEventForm.getEventModel();
  }

  @Override
  public IViewRecurringEvent getRecurringEventModel() {
    return recurringEventForm.getEventModel();
  }

  private void initializeActionListeners() {
    eventTypeRadioButtons[0].addActionListener(e -> {
      this.rerender();
    });
    eventTypeRadioButtons[1].addActionListener(e -> {
      this.rerender();
    });
  }

}
