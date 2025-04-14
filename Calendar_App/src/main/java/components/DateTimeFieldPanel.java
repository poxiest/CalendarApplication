package components;

import java.time.LocalDateTime;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

/**
 * This implementation is a combination of formatted JFormattedTextFields
 * with a date and a time input. This class upholds getter and setter
 * methods so the values in these fields can be adjusted as needed.
 */
public class DateTimeFieldPanel extends JPanel implements IDateTimeFieldPanel {

  JFormattedTextField dateField;
  JFormattedTextField timeField;
  JLabel dateLabel;
  JLabel timeLabel;
  LocalDateTime currentDateTime;

  /**
   * A constructor for a date time field component
   * which accepts a label and a boolean indicating
   * whether or not to set default values for this
   * field.
   * @param label the label for the field.
   * @param setDefaults true if defaults are to be set, false otherwise.
   */
  public DateTimeFieldPanel(String label, Boolean setDefaults) {
    currentDateTime = LocalDateTime.now();
    dateField = new JFormattedTextField(createFormatter("####-##-##"));
    timeField = new JFormattedTextField(createFormatter("##:##"));
    dateField.setColumns(10);
    timeField.setColumns(5);
    dateLabel = new JLabel(label + " (yyyy-MM-dd)");
    timeLabel = new JLabel(" (hh:mm)");
    if (setDefaults) {
      this.setDefaults();
    }
    this.add(dateLabel);
    this.add(dateField);
    this.add(timeLabel);
    this.add(timeField);
    
  }

  @Override
  public String getTime() {
    return timeField.getText();
  }

  @Override
  public String getDate() {
    return dateField.getText();
  }

  @Override
  public void setLocalDateTime(LocalDateTime dateTime) {
    this.currentDateTime = dateTime;
    this.setDefaults();
  }

  @Override
  public void setDateVisible(boolean visible) {
    this.dateLabel.setVisible(visible);
    this.dateField.setVisible(visible);
  }

  @Override
  public void setTimeVisible(boolean visible) {
    this.timeLabel.setVisible(visible);
    this.timeField.setVisible(visible);
  }

  @Override
  public JFormattedTextField getDateField() {
    return this.dateField;
  }

  @Override
  public JFormattedTextField getTimeField() {
    return this.timeField;
  }

  @Override
  public void setDate(String dateString) {
    this.dateField.setText(dateString);
  }

  @Override
  public void setTime(String timeString) {
    this.timeField.setText(timeString);
  }

  private void setDefaults() {
    dateField.setText(currentDateTime.toLocalDate().toString());
    timeField.setText(currentDateTime.toLocalTime().toString());
  }

  private MaskFormatter createFormatter(String s) {
    MaskFormatter formatter = null;
    try {
      formatter = new MaskFormatter(s);
    } catch (java.text.ParseException ex) {
      System.exit(-1);
    }
    return formatter;
  }

}
