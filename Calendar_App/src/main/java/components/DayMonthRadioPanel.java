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

  JRadioButton[] dayMonthRadioButtons;

  /**
   * A constructor for the day month radio
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
    this.dayMonthRadioButtons = new JRadioButton[2];
    ButtonGroup dayMonthButtonGroup = new ButtonGroup();
    dayMonthRadioButtons[0] = new JRadioButton("Day");
    dayMonthRadioButtons[1] = new JRadioButton("Month");
    dayMonthRadioButtons[1].doClick();
    dayMonthButtonGroup.add(dayMonthRadioButtons[0]);
    dayMonthButtonGroup.add(dayMonthRadioButtons[1]);
    this.add(dayMonthRadioButtons[0]);
    this.add(dayMonthRadioButtons[1]);
    dayMonthRadioButtons[0].setActionCommand("day-radio");
    dayMonthRadioButtons[1].setActionCommand("month-radio");
    dayMonthRadioButtons[0].addActionListener(featureActionListener);
    dayMonthRadioButtons[1].addActionListener(displayActionListener);
  }

  public void selectDayRadio() {
    this.dayMonthRadioButtons[0].doClick();
  }

}
