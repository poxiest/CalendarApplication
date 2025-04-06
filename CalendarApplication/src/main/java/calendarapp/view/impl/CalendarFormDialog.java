package calendarapp.view.impl;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static calendarapp.utils.Constants.CALENDAR_NAME;
import static calendarapp.utils.Constants.CALENDAR_TIME_ZONE;

public class CalendarFormDialog extends JDialog {
  private final JFrame parent;
  private Map<String, String> result;

  public CalendarFormDialog(JFrame parent) {
    super(parent, "Create Calendar", true);
    this.parent = parent;
    result = new HashMap<>();
    constructCalendarPanel();
  }

  private void constructCalendarPanel() {
    JTextField nameField;
    JTextField timezoneField;
    setSize(350, 200);
    setLocationRelativeTo(parent);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    formPanel.add(new JLabel("Calendar Name:"));
    nameField = new JTextField();
    formPanel.add(nameField);

    formPanel.add(new JLabel("Timezone:"));
    timezoneField = new JTextField("America/New_York");
    formPanel.add(timezoneField);

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
      result.put(CALENDAR_NAME, nameField.getText().trim());
      result.put(CALENDAR_TIME_ZONE, timezoneField.getText().trim());
      dispose();
    });

    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel);
  }

  public Map<String, String> showDialog() {
    setVisible(true);
    return result;
  }
}
