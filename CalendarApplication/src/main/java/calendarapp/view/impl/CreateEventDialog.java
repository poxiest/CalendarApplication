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

import static calendarapp.utils.Constants.EVENT_END_DATE;
import static calendarapp.utils.Constants.EVENT_LOCATION;
import static calendarapp.utils.Constants.EVENT_NAME;
import static calendarapp.utils.Constants.EVENT_START_DATE;

public class CreateEventDialog extends JDialog {
  private JTextField eventNameField;
  private JSpinner startTimeSpinner;
  private JSpinner endTimeSpinner;
  private JTextField locationField;
  private final Date selectedDate;
  private final JFrame parent;
  private Map<String, String> result = null;

  public CreateEventDialog(JFrame parent, LocalDate selectedDate) {
    super(parent, "Create Event", true);
    this.parent = parent;
    this.selectedDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    constructPane();
  }

  private void constructPane() {
    setSize(400, 450);
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
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
      result = new HashMap<>();
      result.put(EVENT_NAME, eventNameField.getText().trim());
      result.put(EVENT_START_DATE, sdf.format(startDate));
      result.put(EVENT_END_DATE, sdf.format(endDate));
      result.put(EVENT_LOCATION, locationField.getText().trim());
      dispose();
    });

    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel);
  }

  private JSpinner createDateTimeSpinner(Date initialDate) {
    SpinnerDateModel model = new SpinnerDateModel(initialDate, null, null, java.util.Calendar.MINUTE);
    JSpinner spinner = new JSpinner(model);
    JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "MM-dd-yyyy HH:mm");
    spinner.setEditor(editor);
    return spinner;
  }

  public Map<String, String> showDialog() {
    setVisible(true);
    return result;
  }
}
