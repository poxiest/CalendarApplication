package components;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import javax.swing.JButton;

/**
 * Renders the month panel component for the application. This application
 * includes the days of the month as buttons which transfer to the day
 * view.
 */
public class MonthPanel extends AbstractComponent implements IMonthPanel {

  YearMonth currentMonth;
  ActionListener featureActionListener;

  /**
   * The constructor for the month panel which accepts the current
   * month to render and a feature action listener to send action
   * events to the constructor.
   * @param currentMonth the current month to render.
   * @param featureActionListener the action listener for this component.
   */
  public MonthPanel(
      YearMonth currentMonth, 
      ActionListener featureActionListener
  ) {
    this.featureActionListener = featureActionListener;
    this.currentMonth = currentMonth;
    this.render();
  }

  @Override
  protected void render() {
    this.setLayout(new GridLayout(0,7));
    for (Integer day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      JButton dayButton = new JButton(String.valueOf(day));
      dayButton.setActionCommand(day.toString());
      dayButton.addActionListener(featureActionListener);
      this.add(dayButton);
    }
  }

  @Override
  public void setMonth(YearMonth month) {
    this.currentMonth = month;
  }

}
