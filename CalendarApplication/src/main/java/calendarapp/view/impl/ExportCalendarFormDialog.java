package calendarapp.view.impl;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import calendarapp.controller.exporter.Constants;

import static calendarapp.utils.Constants.EXPORT_FILE_EXTENSION;
import static calendarapp.utils.Constants.EXPORT_FILE_NAME;

public class ExportCalendarFormDialog extends JDialog {
  private final JFrame parent;
  private Map<String, String> result;

  public ExportCalendarFormDialog(JFrame parent) {
    super(parent, "Export Calendar", true);
    this.parent = parent;
    result = new HashMap<>();
    constructExportCalendarPanel();
  }

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

  public Map<String, String> showDialog() {
    setVisible(true);
    return result;
  }
}