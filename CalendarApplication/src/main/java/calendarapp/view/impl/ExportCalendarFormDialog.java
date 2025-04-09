package calendarapp.view.impl;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import calendarapp.controller.Constants;

import static calendarapp.utils.Constants.EXPORT_FILE_EXTENSION;
import static calendarapp.utils.Constants.EXPORT_FILE_NAME;

/**
 * A dialog for exporting a calendar by entering the desired file name and selecting a file format.
 * Returns the export details as a map when submitted.
 */
public class ExportCalendarFormDialog extends JDialog {
  private final JFrame parent;
  private Map<String, String> result;

  /**
   * Constructs a modal dialog for exporting a calendar from the application.
   *
   * @param parent the parent frame of this dialog
   */
  public ExportCalendarFormDialog(JFrame parent) {
    super(parent, "Export Calendar", true);
    this.parent = parent;
    result = new HashMap<>();

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        result = null;
        dispose();
      }
    });

    constructExportCalendarPanel();
  }

  /**
   * Builds the form UI for entering the export file name and selecting the file extension.
   */
  private void constructExportCalendarPanel() {
    setSize(400, 200);
    setLocationRelativeTo(parent);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

    formPanel.add(new JLabel("File Name:"));
    JTextField fileNameField = new JTextField();
    formPanel.add(fileNameField);

    formPanel.add(new JLabel("File Extension:"));
    String[] extensions =
        Constants.SupportExportFormats.SUPPORTED_EXPORT_FORMATS.toArray(new String[0]);
    JComboBox<String> extensionComboBox = new JComboBox<>(extensions);
    formPanel.add(extensionComboBox);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton exportButton = new JButton("Export");
    buttonPanel.add(cancelButton);
    buttonPanel.add(exportButton);

    cancelButton.addActionListener(e -> {
      result = null;
      dispose();
    });

    exportButton.addActionListener(e -> {
      String fileName = fileNameField.getText().trim();
      String extension = extensionComboBox.getSelectedItem().toString();

      if (fileName.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please enter a file name",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      result.put(EXPORT_FILE_NAME, fileName);
      result.put(EXPORT_FILE_EXTENSION, extension);
      dispose();
    });

    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel);
  }

  /**
   * Displays the export dialog and returns the result after it is closed.
   *
   * @return a map containing file name and extension, or null if cancelled
   */
  public Map<String, String> showDialog() {
    setVisible(true);
    return result;
  }
}