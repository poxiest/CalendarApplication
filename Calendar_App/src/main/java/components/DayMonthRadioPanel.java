package components;

import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * This component includes a button group which allows users
 * to switch back and forth from the available views.
 */
public class DayMonthRadioPanel extends JPanel implements IDayMonthRadioPanel {

  JRadioButton[] viewRadioButtons;

  /**
   * A constructor for the view radio
   * panel component which accepts action
   * listeners that allow communication with
   * the controller. Feature events are events
   * that require the model, and display events
   * are simple display changes.
   * @param featureActionListener the action listener for feature events.
   * @param displayActionListener the action listener for display events.
   */
  public DayMonthRadioPanel(
      ActionListener featureActionListener,
      ActionListener displayActionListener
  ) {
    this.viewRadioButtons = new JRadioButton[3];
    ButtonGroup viewButtonGroup = new ButtonGroup();

    viewRadioButtons[0] = new JRadioButton("Day");
    viewRadioButtons[1] = new JRadioButton("Month");
    viewRadioButtons[2] = new JRadioButton("Analytics");

    viewRadioButtons[1].doClick(); // Default to Month view

    // Add all radio buttons to the button group
    for (JRadioButton button : viewRadioButtons) {
      viewButtonGroup.add(button);
      this.add(button);
    }

    // Set action commands
    viewRadioButtons[0].setActionCommand("day-radio");
    viewRadioButtons[1].setActionCommand("month-radio");
    viewRadioButtons[2].setActionCommand("analytics-radio"); // Command for analytics

    // Add action listeners
    viewRadioButtons[0].addActionListener(featureActionListener); // Day uses feature listener
    viewRadioButtons[1].addActionListener(displayActionListener); // Month uses display listener
    viewRadioButtons[2].addActionListener(featureActionListener); // Analytics uses feature listener
  }

  @Override
  public void selectDayRadio() {
    this.viewRadioButtons[0].doClick();
  }

  @Override
  public void selectAnalyticsRadio() {
    this.viewRadioButtons[2].doClick();
  }
}