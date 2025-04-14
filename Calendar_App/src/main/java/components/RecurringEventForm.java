package components;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;
import view.IViewRecurringEvent;
import view.IViewRecurringSequenceEvent;
import view.IViewRecurringUntilEvent;
import view.ViewRecurringSequenceEvent;
import view.ViewRecurringUntilEvent;

/**
 * This class represents a form with fields pertaining to recurring events. This
 * class supports getting and setting fields in the form.
 */
public class RecurringEventForm extends AbstractEventForm<IViewRecurringEvent> 
    implements IRecurringEventForm {

  private JFormattedTextField afterField;
  private JCheckBox[] weekdays;
  private IDateTimeFieldPanel onDateTimePickerPanel;
  private Map<String, String> weekdaysMap;
  private JButton editRecurringAfterButton;
  private JButton editRecurringAllButton;
  private Boolean editView;
  private String initialOnDateState;
  private String initialOnTimeState;
  private String initialAfterState;
  private String initialWeekdayState;

  /**
   * A constructor for a recurring event form which is provided
   * with an actionCommand and title for the submit button,
   * as well as an optional header panel and the current
   * date for setting default values. This constructor also
   * takes a feature listener which sends an event to the controller
   * when this form is submitted. Additionally, this form takes
   * an edit view boolean which determines the inclusion of the
   * edit this and following and edit all buttons.
   * @param actionCommand the action command for the submit button.
   * @param submitButtonTitle the title for the submit button.
   * @param featureListener the feature listener to react to submit actions.
   * @param headerPanel an optional header JPanel to render above the form.
   * @param currentDate the current date.
   * @param editView whether to render this form in view mode.
   */
  public RecurringEventForm(
      String actionCommand, 
      String submitButtonTitle,
      ActionListener featureListener,
      JPanel headerPanel,
      LocalDate currentDate,
      Boolean editView
  ) {
    super(actionCommand, submitButtonTitle, featureListener, headerPanel, currentDate);
    this.editView = editView;
    this.onDateTimePickerPanel = new DateTimeFieldPanel("On", false);
    this.afterField = new JFormattedTextField(new NumberFormatter());
    this.afterField.setColumns(3);
    this.weekdays = new JCheckBox[7];

    this.weekdaysMap = new HashMap<>();
    this.weekdaysMap.put("MO", "M");
    this.weekdaysMap.put("TU", "T");
    this.weekdaysMap.put("WE", "W");
    this.weekdaysMap.put("TH", "R");
    this.weekdaysMap.put("FR", "F");
    this.weekdaysMap.put("SA", "S");
    this.weekdaysMap.put("SU", "U");

    this.editRecurringAfterButton = new JButton("Edit This and Following");
    this.editRecurringAllButton = new JButton("Edit All");
    this.editRecurringAfterButton.setActionCommand("edit-recurring-this-and-following");
    this.editRecurringAllButton.setActionCommand("edit-recurring-all");
    this.editRecurringAfterButton.addActionListener(featureListener);
    this.editRecurringAllButton.addActionListener(featureListener);

    this.render();
    this.initializeCaretListeners();
    this.initializeEventListeners();
  }

  private void initializeEventListeners() {
    for (JCheckBox checkBox : weekdays) {
      checkBox.addActionListener(e -> {
        String currentWeekdayString = getWeekdaysString();
        if (initialWeekdayState != null 
            && initialWeekdayState.compareTo(currentWeekdayString) != 0) {
          this.submitButton.setVisible(false);
        }
        else {
          this.submitButton.setVisible(true);
        }
      });
    }
  }

  private void initializeCaretListeners() {
    this.onDateTimePickerPanel.getDateField().addCaretListener(e -> {
      if (initialOnDateState != null 
          && onDateTimePickerPanel.getDate().compareTo(initialOnDateState) != 0) {
        this.submitButton.setVisible(false);
      }
      else {
        this.submitButton.setVisible(true);
      }
      if (this.onDateTimePickerPanel.getDate()
          .replace("-", "")
          .replace(" ", "").length() != 0) {
        this.afterField.setEnabled(false);
      }
      else {
        this.afterField.setEnabled(true);
      }
    });

    this.onDateTimePickerPanel.getTimeField().addCaretListener(e -> {
      if (initialOnTimeState != null 
          && onDateTimePickerPanel.getTime().compareTo(initialOnTimeState) != 0) {
        this.submitButton.setVisible(false);
      }
      else {
        this.submitButton.setVisible(true);
      }
      if (this.onDateTimePickerPanel.getTime()
          .replace(":", "")
          .replace(" ", "").length() != 0) {
        this.afterField.setEnabled(false);
      }
      else {
        this.afterField.setEnabled(true);
      }
    });

    this.afterField.addCaretListener(e -> {
      if (initialAfterState != null 
          && afterField.getText().compareTo(initialAfterState) != 0) {
        this.submitButton.setVisible(false);
      }
      else {
        this.submitButton.setVisible(true);
      }
      if (this.afterField.getText().length() != 0) {
        this.onDateTimePickerPanel.getDateField().setEnabled(false);
        this.onDateTimePickerPanel.getTimeField().setEnabled(false);
      }
      else {
        this.onDateTimePickerPanel.getDateField().setEnabled(true);
        this.onDateTimePickerPanel.getTimeField().setEnabled(true);
      }
    });
  }

  /**
   * Renders this form.
   */
  public void render() {
    renderCommonFields();
    JPanel weekdaysPanel = new JPanel();
    JPanel weekdaysLabelPanel = new JPanel();
    JLabel weekdaysLabel = new JLabel("Repeats: ");
    weekdaysLabelPanel.add(weekdaysLabel);
    for (int i = 0; i < 7; i++) {
      weekdays[i] = new JCheckBox(DayOfWeek.values()[i].toString().substring(0, 2));
      weekdaysPanel.add(weekdays[i]);
    }
    constraints.gridy += 1;
    this.add(weekdaysLabelPanel, constraints);

    constraints.gridy += 1;
    this.add(weekdaysPanel, constraints);

    JPanel endsLabelPanel = new JPanel();
    endsLabelPanel.add(new JLabel("Ends:"));

    constraints.gridy += 1;
    this.add(endsLabelPanel, constraints);

    constraints.gridy += 1;
    this.add((JPanel) onDateTimePickerPanel, constraints);

    JPanel afterPanel = new JPanel();
    JLabel afterLabel = new JLabel("After: ");
    JLabel occurrencesLabel = new JLabel(" occurrences");
    afterPanel.add(afterLabel);
    afterPanel.add(afterField);
    afterPanel.add(occurrencesLabel);

    constraints.gridy += 1;
    this.add(afterPanel, constraints);

    JPanel submitPanel = new JPanel();

    constraints.gridy += 1;
    constraints.anchor = GridBagConstraints.LINE_END;
    submitPanel.add(submitButton, constraints);

    if (editView) {
      submitPanel.add(editRecurringAfterButton);
      submitPanel.add(editRecurringAllButton);
    }

    this.add(submitPanel, constraints);

  }

  /**
   * Gets the event model associated with the fields in this form.
   * All fields correspond directly to an IViewRecurringEvent. Events
   * can either be IViewRecurringUntilEvents or IViewRecurringSequenceEvents.
   */
  public IViewRecurringEvent getEventModel() {
    if (afterField.getText().length() > 0) {
      IViewRecurringSequenceEvent casted = new ViewRecurringSequenceEvent();
      casted.setSequenceSizeString(afterField.getText());
      casted.setWeekdaysString(getWeekdaysString());
      casted.setSubject(subjectField.getText());
      casted.setIsAllDayEvent(allDayCheckbox.isSelected());
      casted.setStartDateString(startDateTimePanel.getDate());
      casted.setStartTimeString(startDateTimePanel.getTime());
      casted.setEndDateString(endDateTimePanel.getDate());
      casted.setEndTimeString(endDateTimePanel.getTime());
      casted.setDescription(subjectField.getText());
      casted.setLocation(locationField.getText());
      casted.setIsPrivate(this.isPrivateCheckbox.isSelected());
      return casted;
    }
    else {
      IViewRecurringUntilEvent casted = new ViewRecurringUntilEvent();
      casted.setOnDateString(onDateTimePickerPanel.getDate());
      casted.setWeekdaysString(getWeekdaysString());
      casted.setOnTimeString(onDateTimePickerPanel.getTime());
      casted.setSubject(subjectField.getText());
      casted.setIsAllDayEvent(allDayCheckbox.isSelected());
      casted.setStartDateString(startDateTimePanel.getDate());
      casted.setStartTimeString(startDateTimePanel.getTime());
      casted.setEndDateString(endDateTimePanel.getDate());
      casted.setEndTimeString(endDateTimePanel.getTime());
      casted.setDescription(subjectField.getText());
      casted.setLocation(locationField.getText());
      casted.setIsPrivate(this.isPrivateCheckbox.isSelected());
      return casted;
    }
  }

  private String getWeekdaysString() {
    String result = "";
    for (JCheckBox weekdayCheckbox: weekdays) {
      if (weekdayCheckbox.isSelected()) {
        result += this.weekdaysMap.get(weekdayCheckbox.getText());
      }
    }
    return result;
  }

  @Override
  public void setWeekdaysCheckboxes(String weekdaysString) {
    initialWeekdayState = weekdaysString;
    for (char c: weekdaysString.toCharArray()) {
      switch (c) {
        case 'M':
          weekdays[0].doClick();
          break;
        case 'T':
          weekdays[1].doClick();
          break;
        case 'W':
          weekdays[2].doClick();
          break;
        case 'R':
          weekdays[3].doClick();
          break;
        case 'F':
          weekdays[4].doClick();
          break;
        case 'S':
          weekdays[5].doClick();
          break;
        case 'U':
          weekdays[6].doClick();
          break;
        default:
          break;
      }
    }
  }

  public void setOnDateString(String onDateString) {
    this.onDateTimePickerPanel.setDate(onDateString);
    this.initialOnDateState = onDateString;
  }

  public void setOnTimeString(String onTimeString) {
    this.onDateTimePickerPanel.setTime(onTimeString);
    this.initialOnTimeState = onTimeString;
  }

  public void setAfterString(String afterString) {
    this.afterField.setText(afterString);
    this.initialAfterState = afterString;
  }

}
