package calendarapp.view.impl;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import calendarapp.controller.Features;
import calendarapp.model.dto.PrintEventsResponseDTO;

public class EventFormDialog extends JDialog {

  private final PrintEventsResponseDTO eventToEdit;
  private JTextField eventNameField;
  private JSpinner startTimeSpinner;
  private JSpinner endTimeSpinner;
  private JTextField locationField;
  private final Date selectedDate;
  private final JFrame parent;
  private final Features controller;

  public EventFormDialog(JFrame parent, Features controller, String title,
                         PrintEventsResponseDTO event, LocalDate selectedDate) {
    super(parent, title, true);
    this.parent = parent;
    this.controller = controller;
    this.selectedDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    this.eventToEdit = event;
    constructPane();
  }

  private void constructPane() {
    setSize(400, 450);
    setLocationRelativeTo(parent);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

    // Event Name
    formPanel.add(new JLabel("Event Name:"));
    eventNameField = new JTextField(eventToEdit != null ? eventToEdit.getEventName() : "");
    formPanel.add(eventNameField);

    // Start Time using JSpinner
    formPanel.add(new JLabel("Start Time:"));
    startTimeSpinner = createDateTimeSpinner(eventToEdit != null ?
        (Date) eventToEdit.getStartTime() : selectedDate);
    formPanel.add(startTimeSpinner);

    // End Time using JSpinner
    formPanel.add(new JLabel("End Time:"));
    endTimeSpinner = createDateTimeSpinner(eventToEdit != null ? (Date) eventToEdit.getEndTime()
        : selectedDate);
    formPanel.add(endTimeSpinner);

    // Location
    formPanel.add(new JLabel("Location:"));
    locationField = new JTextField(eventToEdit != null ? eventToEdit.getLocation() : "");
    formPanel.add(locationField);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton saveButton = new JButton("Save");
    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    cancelButton.addActionListener(e -> dispose());
    saveButton.addActionListener(e -> {
      // Retrieve the selected dates from the spinners
      Date startDate = (Date) startTimeSpinner.getValue();
      Date endDate = (Date) endTimeSpinner.getValue();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
      if (eventToEdit == null) {
        controller.createEvent(
            eventNameField.getText(),
            sdf.format(startDate),
            sdf.format(endDate),
            null, null, null, null,
            locationField.getText(),
            null, true);
      } else {
        controller.editEvent(
            eventToEdit.getEventName(),
            sdf.format(startDate),
            sdf.format(endDate),
            "name",
            eventNameField.getText()
        );
      }
      dispose();
    });

    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel);
  }

  // Helper method to create a JSpinner for date/time selection.
  private JSpinner createDateTimeSpinner(Date initialDate) {
    SpinnerDateModel model = new SpinnerDateModel(initialDate, null, null,
        java.util.Calendar.MINUTE);
    JSpinner spinner = new JSpinner(model);
    // Set a date editor with a custom date/time format.
    JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "MM-dd-yyyy HH:mm");
    spinner.setEditor(editor);
    return spinner;
  }
}
