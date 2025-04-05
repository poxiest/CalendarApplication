package calendarapp.view.impl;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import calendarapp.controller.Features;

public class CalendarFormDialog extends JDialog {

  private final JTextField nameField;
  private final JTextField timezoneField;

  public CalendarFormDialog(JFrame parent, Features controller) {
    super(parent, "Create Calendar", true);
    setSize(350, 200);
    setLocationRelativeTo(parent);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    formPanel.add(new JLabel("Calendar Name:"));
    nameField = new JTextField();
    formPanel.add(nameField);

    formPanel.add(new JLabel("Timezone:"));
    timezoneField = new JTextField("GMT");
    formPanel.add(timezoneField);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton saveButton = new JButton("Save");
    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    cancelButton.addActionListener(e -> dispose());
    saveButton.addActionListener(e -> {
      String calendarName = nameField.getText().trim();
      String timezone = timezoneField.getText().trim();
      if (calendarName.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please enter a calendar name.",
            "Input Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      controller.createCalendar(calendarName, timezone);
      dispose();
    });

    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel);
  }
}
