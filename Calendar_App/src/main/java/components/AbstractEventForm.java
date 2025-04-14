package components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Abstract event form class which implements common methods between event forms
 * and renders fields common between forms.
 */
public abstract class AbstractEventForm<T> extends JPanel implements IEventForm<T> {

  protected T eventModel;
  protected JTextField descriptionField;
  protected JTextField locationField;
  protected JTextField subjectField;
  protected IDateTimeFieldPanel startDateTimePanel;
  protected IDateTimeFieldPanel endDateTimePanel;
  protected JCheckBox allDayCheckbox;
  protected JCheckBox isPrivateCheckbox;
  protected JButton submitButton;
  protected GridBagConstraints constraints;
  protected JPanel headerPanel;
  protected LocalDate currentDate;

  protected AbstractEventForm(
      String actionCommand, 
      String submitButtonTitle,
      ActionListener featureListener,
      JPanel headerPanel,
      LocalDate currentDate
  ) {
    this.setLayout(new GridBagLayout());
    this.constraints = new GridBagConstraints();
    this.constraints.gridx = 0;
    this.constraints.gridy = 0;
    this.constraints.anchor = GridBagConstraints.LINE_START;

    this.headerPanel = headerPanel;
    this.startDateTimePanel = new DateTimeFieldPanel("Start", true);
    this.endDateTimePanel = new DateTimeFieldPanel("End", true);
    this.descriptionField = new JTextField(20);
    this.locationField = new JTextField(20);
    this.subjectField = new JTextField(17);
    this.allDayCheckbox = new JCheckBox("All Day Event");
    this.isPrivateCheckbox = new JCheckBox("Private");
    this.submitButton = new JButton(submitButtonTitle);
    this.submitButton.setActionCommand(actionCommand);
    this.submitButton.addActionListener(featureListener);
    this.allDayCheckbox.addActionListener(e -> {
      this.startDateTimePanel.setTimeVisible(! allDayCheckbox.isSelected());
      this.endDateTimePanel.setDateVisible(! allDayCheckbox.isSelected());
      this.endDateTimePanel.setTimeVisible(! allDayCheckbox.isSelected());
    });
    if (currentDate != null) {
      this.startDateTimePanel.setLocalDateTime(LocalDateTime.of(currentDate, LocalTime.now()));
      this.endDateTimePanel.setLocalDateTime(
          LocalDateTime.of(currentDate, LocalTime.now().plusHours(1)));
    }
  }

  protected void renderCommonFields() {
    if (headerPanel != null) {
      this.add(this.headerPanel, constraints);
      this.constraints.gridy += 1;
    }

    JPanel checkboxPanel = new JPanel();
    checkboxPanel.add(allDayCheckbox);
    checkboxPanel.add(isPrivateCheckbox);
    this.add(checkboxPanel, constraints);

    constraints.gridy += 1;
    JLabel subjectLabel = new JLabel("Subject");
    JPanel subjectPanel = new JPanel();
    subjectPanel.add(subjectLabel);
    subjectPanel.add(subjectField);
    this.add(subjectPanel, constraints);

    constraints.gridy += 1;
    this.add((JPanel) startDateTimePanel, constraints);
    
    constraints.gridy += 1;
    this.add((JPanel) endDateTimePanel, constraints);

    constraints.gridy += 1;
    JLabel descriptionLabel = new JLabel("Description");
    JPanel descriptionPanel = new JPanel();
    descriptionPanel.add(descriptionLabel);
    descriptionPanel.add(descriptionField);
    this.add(descriptionPanel, constraints);
    
    constraints.gridy += 1;
    JLabel locationLabel = new JLabel("Location     ");
    JPanel locationPanel = new JPanel();
    locationPanel.add(locationLabel);
    locationPanel.add(locationField); 
    this.add(locationPanel, constraints);
  }

  @Override
  public void setSubject(String subject) {
    this.subjectField.setText(subject);
  }

  @Override
  public void setDescription(String description) {
    this.descriptionField.setText(description);
  }

  @Override
  public void setLocation(String location) {
    this.locationField.setText(location);
  }

  @Override
  public void setStartDate(String startDateString) {
    this.startDateTimePanel.setDate(startDateString);
  }

  @Override
  public void setStartTime(String startTimeString) {
    this.startDateTimePanel.setTime(startTimeString);
  }

  @Override
  public void setEndDate(String endDateString) {
    this.endDateTimePanel.setDate(endDateString);
  }

  @Override
  public void setEndTime(String endTimeString) {
    this.endDateTimePanel.setTime(endTimeString);
  }

  @Override
  public void clickIsAllDayCheckbox() {
    this.allDayCheckbox.doClick();
  }

  @Override
  public void clickIsPrivateCheckbox() {
    this.isPrivateCheckbox.doClick();
  }

  @Override
  public abstract T getEventModel();

}
