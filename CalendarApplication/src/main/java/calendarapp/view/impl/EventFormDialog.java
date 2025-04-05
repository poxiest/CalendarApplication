package calendarapp.view.impl;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import calendarapp.controller.Features;
import calendarapp.model.dto.PrintEventsResponseDTO;

public class EventFormDialog extends JDialog {

  private final PrintEventsResponseDTO eventToEdit;
  private final JTextField eventNameField;
  private final JTextField startTimeField;
  private final JTextField endTimeField;
  private final JTextField locationField;
  private final JCheckBox autoDeclineCheckbox;

  public EventFormDialog(JFrame parent, Features controller, String title, PrintEventsResponseDTO event) {
    super(parent, title, true);
    this.eventToEdit = event;
    setSize(400, 450);
    setLocationRelativeTo(parent);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    formPanel.add(new JLabel("Event Name:"));
    eventNameField = new JTextField(event != null ? event.getEventName() : "");
    formPanel.add(eventNameField);

    formPanel.add(new JLabel("Start Time:"));
    startTimeField = new JTextField(event != null ? event.getStartTime().toString() : "");
    formPanel.add(startTimeField);

    formPanel.add(new JLabel("End Time:"));
    endTimeField = new JTextField(event != null ? event.getEndTime().toString() : "");
    formPanel.add(endTimeField);

    formPanel.add(new JLabel("Location:"));
    locationField = new JTextField(event != null ? event.getLocation() : "");
    formPanel.add(locationField);

    formPanel.add(new JLabel("Auto Decline Conflicts:"));
    autoDeclineCheckbox = new JCheckBox();
    formPanel.add(autoDeclineCheckbox);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton saveButton = new JButton("Save");
    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    cancelButton.addActionListener(e -> dispose());
    saveButton.addActionListener(e -> {
      if (event == null) {
        controller.createEvent(
            eventNameField.getText(),
            startTimeField.getText(),
            endTimeField.getText(),
            "", "", "", "",
            locationField.getText(),
            "",
            autoDeclineCheckbox.isSelected()
        );
      } else {
        controller.editEvent(
            event.getEventName(),
            event.getStartTime().toString(),
            event.getEndTime().toString(),
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
}
