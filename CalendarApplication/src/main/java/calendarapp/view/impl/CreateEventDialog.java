package calendarapp.view.impl;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import calendarapp.model.EventVisibility;

import static calendarapp.utils.Constants.EVENT_DESCRIPTION;
import static calendarapp.utils.Constants.EVENT_END_DATE;
import static calendarapp.utils.Constants.EVENT_LOCATION;
import static calendarapp.utils.Constants.EVENT_NAME;
import static calendarapp.utils.Constants.EVENT_RECURRING_COUNT;
import static calendarapp.utils.Constants.EVENT_RECURRING_DAYS;
import static calendarapp.utils.Constants.EVENT_RECURRING_END_DATE;
import static calendarapp.utils.Constants.EVENT_START_DATE;
import static calendarapp.utils.Constants.EVENT_VISIBILITY;

public class CreateEventDialog extends JDialog {
  private JTextField eventNameField;
  private JSpinner startTimeSpinner;
  private JSpinner endTimeSpinner;
  private JTextField locationField;
  private JCheckBox[] recurringDayCheckBoxes;
  private JSpinner occurrenceCountSpinner;
  private JSpinner recurrenceEndDateSpinner;
  private JTextField descriptionField;
  private JComboBox<String> visibilityComboBox;

  // Radio buttons to choose between recurrence types
  private JRadioButton singleEventButton;
  private JRadioButton countRadioButton;
  private JRadioButton dateRadioButton;


  private final Date selectedDate;
  private final JFrame parent;
  private Map<String, String> result = null;

  public CreateEventDialog(JFrame parent, LocalDate selectedDate) {
    super(parent, "Create Event", true);
    this.parent = parent;
    this.selectedDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    this.result = new HashMap<>();
    constructPane();
  }

  private void constructPane() {
    setSize(600, 700);
    setLocationRelativeTo(parent);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    formPanel.add(new JLabel("Event Name:"));
    eventNameField = new JTextField();
    formPanel.add(eventNameField);

    formPanel.add(new JLabel("Start Time:"));
    startTimeSpinner = createDateTimeSpinner(selectedDate);
    formPanel.add(startTimeSpinner);

    formPanel.add(new JLabel("End Time:"));
    endTimeSpinner = createDateTimeSpinner(selectedDate);
    formPanel.add(endTimeSpinner);

    formPanel.add(new JLabel("Location:"));
    locationField = new JTextField();
    formPanel.add(locationField);

    formPanel.add(new JLabel("Recurrence Setting:"));
    JPanel recurrenceOptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    singleEventButton = new JRadioButton("Single Event");
    countRadioButton = new JRadioButton("Occurrence Count");
    dateRadioButton = new JRadioButton("Recurrence End Date");
    ButtonGroup recurrenceGroup = new ButtonGroup();
    recurrenceGroup.add(singleEventButton);
    recurrenceGroup.add(countRadioButton);
    recurrenceGroup.add(dateRadioButton);
    singleEventButton.setSelected(true);
    recurrenceOptionPanel.add(singleEventButton);
    recurrenceOptionPanel.add(countRadioButton);
    recurrenceOptionPanel.add(dateRadioButton);
    formPanel.add(recurrenceOptionPanel);

    formPanel.add(new JLabel("Recurring Days:"));
    JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    recurringDayCheckBoxes = new JCheckBox[days.length];
    for (int i = 0; i < days.length; i++) {
      recurringDayCheckBoxes[i] = new JCheckBox(days[i]);
      daysPanel.add(recurringDayCheckBoxes[i]);
    }
    daysPanel.setVisible(false);
    formPanel.add(daysPanel);

    formPanel.add(new JLabel("Occurrence Count:"));
    occurrenceCountSpinner = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
    occurrenceCountSpinner.setEnabled(false);
    formPanel.add(occurrenceCountSpinner);

    formPanel.add(new JLabel("Recurrence End Date:"));
    recurrenceEndDateSpinner = new JSpinner(new SpinnerDateModel(selectedDate, null, null,
        java.util.Calendar.DAY_OF_MONTH));
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(recurrenceEndDateSpinner, "MM-dd"
        + "-yyyy");
    recurrenceEndDateSpinner.setEditor(dateEditor);
    recurrenceEndDateSpinner.setEnabled(false);
    formPanel.add(recurrenceEndDateSpinner);

    singleEventButton.addActionListener(e -> {
      singleEventButton.setEnabled(true);
      occurrenceCountSpinner.setEnabled(false);
      recurrenceEndDateSpinner.setEnabled(false);
      daysPanel.setVisible(false);
    });
    countRadioButton.addActionListener(e -> {
      occurrenceCountSpinner.setEnabled(true);
      recurrenceEndDateSpinner.setEnabled(false);
      daysPanel.setVisible(true);
    });
    dateRadioButton.addActionListener(e -> {
      occurrenceCountSpinner.setEnabled(false);
      recurrenceEndDateSpinner.setEnabled(true);
      daysPanel.setVisible(true);
    });

    formPanel.add(new JLabel("Description:"));
    descriptionField = new JTextField();
    formPanel.add(descriptionField);

    formPanel.add(new JLabel("Visibility:"));
    String[] visibilityOptions = new String[EventVisibility.getVisibilities().size()];
    for (int i = 0; i < EventVisibility.getVisibilities().size(); i++) {
      visibilityOptions[i] = EventVisibility.getVisibilities().get(i);
    }
    visibilityComboBox = new JComboBox<>(visibilityOptions);
    formPanel.add(visibilityComboBox);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton saveButton = new JButton("Save");
    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    cancelButton.addActionListener(e -> {
      result = null;
      dispose();
    });

    saveButton.addActionListener(e -> {
      Date startDate = (Date) startTimeSpinner.getValue();
      Date endDate = (Date) endTimeSpinner.getValue();
      Date recurrenceEndDate = (Date) recurrenceEndDateSpinner.getValue();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
      SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

      result.put(EVENT_NAME, eventNameField.getText().trim());
      result.put(EVENT_START_DATE, sdf.format(startDate));
      result.put(EVENT_END_DATE, sdf.format(endDate));
      result.put(EVENT_LOCATION, locationField.getText().trim());
      result.put(EVENT_RECURRING_DAYS, getDays());
      if (singleEventButton.isSelected()) {
        result.put(EVENT_RECURRING_COUNT, null);
        result.put(EVENT_RECURRING_END_DATE, null);
      }
      if (countRadioButton.isSelected()) {
        result.put(EVENT_RECURRING_COUNT, occurrenceCountSpinner.getValue().toString());
        result.put(EVENT_RECURRING_END_DATE, null);
      } else if (dateRadioButton.isSelected()) {
        result.put(EVENT_RECURRING_COUNT, null);
        result.put(EVENT_RECURRING_END_DATE, sdfDate.format(recurrenceEndDate));
      }

      result.put(EVENT_DESCRIPTION, descriptionField.getText().trim());
      result.put(EVENT_VISIBILITY, visibilityComboBox.getSelectedItem().toString());
      dispose();
    });

    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel);
  }

  private JSpinner createDateTimeSpinner(Date initialDate) {
    SpinnerDateModel model = new SpinnerDateModel(initialDate, null, null,
        java.util.Calendar.MINUTE);
    JSpinner spinner = new JSpinner(model);
    JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "MM-dd-yyyy HH:mm");
    spinner.setEditor(editor);
    return spinner;
  }

  private String getDays() {
    StringBuilder recurringDays = new StringBuilder();
    char[] dayCodes = {'U', 'M', 'T', 'W', 'R', 'F', 'S'};
    for (int i = 0; i < recurringDayCheckBoxes.length; i++) {
      if (recurringDayCheckBoxes[i].isSelected()) {
        recurringDays.append(dayCodes[i]);
      }
    }
    return recurringDays.toString().isEmpty() ? null : recurringDays.toString();
  }

  public Map<String, String> showDialog() {
    setVisible(true);
    return result;
  }
}
