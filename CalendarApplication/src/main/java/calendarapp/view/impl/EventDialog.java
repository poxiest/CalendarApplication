package calendarapp.view.impl;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import calendarapp.model.EventVisibility;
import calendarapp.model.dto.EventsResponseDTO;

import static calendarapp.utils.Constants.EVENT_DESCRIPTION;
import static calendarapp.utils.Constants.EVENT_END_DATE;
import static calendarapp.utils.Constants.EVENT_LOCATION;
import static calendarapp.utils.Constants.EVENT_NAME;
import static calendarapp.utils.Constants.EVENT_RECURRING_COUNT;
import static calendarapp.utils.Constants.EVENT_RECURRING_DAYS;
import static calendarapp.utils.Constants.EVENT_RECURRING_END_DATE;
import static calendarapp.utils.Constants.EVENT_START_DATE;
import static calendarapp.utils.Constants.EVENT_VISIBILITY;

/**
 * A dialog for creating or editing a calendar event, supporting both single and recurring events.
 * Allows input for event details such as name, time, location, recurrence, description, and visibility.
 */
public class EventDialog extends JDialog {
  private final JFrame parent;
  private final Date selectedDate;
  private JTextField eventNameField;
  private JSpinner startTimeSpinner;
  private JSpinner endTimeSpinner;
  private JTextField locationField;
  private JCheckBox[] recurringDayCheckBoxes;
  private JSpinner occurrenceCountSpinner;
  private JSpinner recurrenceEndDateSpinner;
  private JTextField descriptionField;
  private JComboBox<String> visibilityComboBox;
  private JRadioButton singleEventButton;
  private JRadioButton countRadioButton;
  private JRadioButton dateRadioButton;
  private JPanel daysPanel;
  private Map<String, String> result = null;

  /**
   * Constructs a dialog for creating a new event based on the selected date.
   *
   * @param parent the parent frame of this dialog
   * @param selectedDate the date selected on the calendar
   */
  public EventDialog(JFrame parent, LocalDate selectedDate) {
    super(parent, "Create Event", true);
    this.parent = parent;
    this.selectedDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    this.result = new HashMap<>();

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        result = null;
        dispose();
      }
    });

    constructPanel();
  }

  /**
   * Constructs a dialog for editing an existing event using the provided DTO.
   *
   * @param parent the parent frame of this dialog
   * @param selectedDate the fallback date if the DTO doesn't contain a start time
   * @param dto the event data to prefill the dialog
   */
  public EventDialog(JFrame parent, LocalDate selectedDate, EventsResponseDTO dto) {
    super(parent, "Edit Event", true);
    this.parent = parent;
    // If the DTO has a start time, use it; otherwise, fall back to the provided selectedDate.
    if (dto.getStartTime() != null) {
      LocalDateTime ldt = (LocalDateTime) dto.getStartTime();
      this.selectedDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    } else {
      this.selectedDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    this.result = new HashMap<>();
    constructPanel();
    populateFieldsFromDTO(dto);
  }

  /**
   * Builds the user interface of the dialog with all event input fields and buttons.
   */
  private void constructPanel() {
    setSize(600, 700);
    setLocationRelativeTo(parent);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    formPanel.add(new JLabel("Event Name:"));
    eventNameField = new JTextField();
    formPanel.add(eventNameField);

    formPanel.add(new JLabel("Start Time:"));
    startTimeSpinner = createDateTimeSpinner(selectedDate, "MM-dd-yyyy HH:mm", Calendar.MINUTE);
    formPanel.add(startTimeSpinner);

    formPanel.add(new JLabel("End Time:"));
    endTimeSpinner = createDateTimeSpinner(selectedDate, "MM-dd-yyyy HH:mm", Calendar.MINUTE);
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
    daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    recurringDayCheckBoxes = new JCheckBox[days.length];
    for (int i = 0; i < days.length; i++) {
      recurringDayCheckBoxes[i] = new JCheckBox(days[i]);
      daysPanel.add(recurringDayCheckBoxes[i]);
    }
    daysPanel.setVisible(false);
    formPanel.add(daysPanel);

    formPanel.add(new JLabel("Occurrence Count:"));
    occurrenceCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    occurrenceCountSpinner.setEnabled(false);
    formPanel.add(occurrenceCountSpinner);

    formPanel.add(new JLabel("Recurrence End Date:"));
    recurrenceEndDateSpinner = createDateTimeSpinner(selectedDate, "MM-dd-yyyy",
        Calendar.DAY_OF_MONTH);
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
      result.put(EVENT_LOCATION,
          (locationField.getText().isEmpty() || locationField.getText().isBlank()) ? null :
              locationField.getText().trim());
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
      result.put(EVENT_DESCRIPTION,
          (descriptionField.getText().isEmpty() || descriptionField.getText().isBlank()) ? null :
              descriptionField.getText().trim());
      result.put(EVENT_VISIBILITY, visibilityComboBox.getSelectedItem().toString());
      dispose();
    });

    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel);
  }

  /**
   * Creates a JSpinner for date and time selection using the given format and initial value.
   *
   * @param initialDate the initial date to display
   * @param format the date/time format for the spinner
   * @param calendarField the calendar field to increment (e.g., Calendar.MINUTE)
   * @return the configured JSpinner
   */
  private JSpinner createDateTimeSpinner(Date initialDate, String format, int calendarField) {
    SpinnerDateModel model = new SpinnerDateModel(initialDate, null, null, calendarField);
    JSpinner spinner = new JSpinner(model);
    JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, format);
    spinner.setEditor(editor);
    return spinner;
  }

  /**
   * Returns a string representing selected recurring weekdays in coded format (e.g., 'MTWR').
   *
   * @return a string of selected day codes or null if none selected
   */
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

  /**
   * Populates the dialog fields with event data from the provided DTO for editing.
   *
   * @param dto the event data to populate into the form
   */
  private void populateFieldsFromDTO(EventsResponseDTO dto) {
    // Populate basic fields.
    eventNameField.setText(dto.getEventName() != null ? dto.getEventName() : "");
    if (dto.getStartTime() != null) {
      LocalDateTime ldt = (LocalDateTime) dto.getStartTime();
      Date startDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
      startTimeSpinner.setValue(startDate);
    }
    if (dto.getEndTime() != null) {
      LocalDateTime ldt = (LocalDateTime) dto.getEndTime();
      Date endDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
      endTimeSpinner.setValue(endDate);
    }
    locationField.setText(dto.getLocation() != null ? dto.getLocation() : "");
    descriptionField.setText(dto.getDescription() != null ? dto.getDescription() : "");

    // Set visibility selection.
    if (dto.getVisibility() != null) {
      for (int i = 0; i < visibilityComboBox.getItemCount(); i++) {
        if (visibilityComboBox.getItemAt(i).equals(dto.getVisibility())) {
          visibilityComboBox.setSelectedIndex(i);
          break;
        }
      }
    }

    // Recurrence settings.
    String recurringDays = dto.getRecurringDays();
    boolean hasRecurringDays = recurringDays != null && !recurringDays.isEmpty();
    char[] dayCodes = {'U', 'M', 'T', 'W', 'R', 'F', 'S'};
    for (int i = 0; i < recurringDayCheckBoxes.length; i++) {
      recurringDayCheckBoxes[i].setSelected(hasRecurringDays && recurringDays.indexOf(dayCodes[i]) != -1);
    }

    // Choose radio buttons based on recurrence values.
    if (!hasRecurringDays) {
      singleEventButton.setSelected(true);
      occurrenceCountSpinner.setEnabled(false);
      recurrenceEndDateSpinner.setEnabled(false);
      daysPanel.setVisible(false);
    } else {
      if (dto.getOccurrenceCount() != null) {
        countRadioButton.setSelected(true);
        occurrenceCountSpinner.setValue(dto.getOccurrenceCount());
        occurrenceCountSpinner.setEnabled(true);
        recurrenceEndDateSpinner.setEnabled(false);
      } else if (dto.getRecurrenceEndDate() != null) {
        dateRadioButton.setSelected(true);
        LocalDateTime ldt = (LocalDateTime) dto.getRecurrenceEndDate();
        Date recurDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        recurrenceEndDateSpinner.setValue(recurDate);
        occurrenceCountSpinner.setEnabled(false);
        recurrenceEndDateSpinner.setEnabled(true);
      } else {
        singleEventButton.setSelected(true);
        occurrenceCountSpinner.setEnabled(false);
        recurrenceEndDateSpinner.setEnabled(false);
      }
      daysPanel.setVisible(true);
    }
  }

  /**
   * Displays the event dialog and returns the result when closed.
   *
   * @return a map of event properties or null if the dialog was cancelled
   */
  public Map<String, String> showDialog() {
    setVisible(true);
    return result;
  }
}
