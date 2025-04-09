package calendarapp.view.impl;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static calendarapp.utils.Constants.CALENDAR_NAME;
import static calendarapp.utils.Constants.CALENDAR_TIME_ZONE;

/**
 * A dialog window for creating a new calendar by entering its name and timezone.
 * It collects user input and returns the data as a map when the dialog is submitted.
 */
public class CalendarFormDialog extends JDialog {
  private final JFrame parent;
  private Map<String, String> result;

  /**
   * Constructs a modal dialog for creating a new calendar with name and timezone input fields.
   */
  public CalendarFormDialog(JFrame parent) {
    super(parent, "Create Calendar", true);
    this.parent = parent;
    result = new HashMap<>();

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        result = null;
        dispose();
      }
    });

    constructCalendarPanel();
  }

  /**
   * Constructs the user interface components for the calendar creation form.
   */
  private void constructCalendarPanel() {
    JTextField nameField;
    JTextField timezoneField;
    setSize(350, 300);
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

  /**
   * Displays the dialog and returns the user input result when the dialog is closed.
   *
   * @return a map containing calendar name and timezone, or null if cancelled.
   */
  public Map<String, String> showDialog() {
    setVisible(true);
    return result;
  }
}
