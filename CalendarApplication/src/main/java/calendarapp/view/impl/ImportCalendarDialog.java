package calendarapp.view.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import calendarapp.controller.exporter.Constants;

import static calendarapp.utils.Constants.IMPORT_FILE_PATH;

/**
 * A dialog for selecting a calendar file to import.
 * Filters the file chooser based on supported calendar file formats.
 */
public class ImportCalendarDialog extends JDialog {
  private final JFrame parent;

  /**
   * Constructs a modal dialog for importing a calendar file.
   *
   * @param parent the parent frame of this dialog
   */
  public ImportCalendarDialog(JFrame parent) {
    super(parent, "Import Calendar", true);
    this.parent = parent;
  }

  /**
   * Opens a file chooser dialog for the user to select a calendar file to import.
   *
   * @return a map containing the selected file path, or null if the operation is cancelled
   */
  public Map<String, String> showDialog() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Import Calendar");

    StringBuilder description = new StringBuilder("Supported Formats: (");
    String[] extensions =
        Constants.SupportExportFormats.SUPPORTED_EXPORT_FORMATS.toArray(new String[0]);

    for (int i = 0; i < extensions.length; i++) {
      description.append(".").append(extensions[i]);
      if (i < extensions.length - 1) {
        description.append(", ");
      }
    }
    description.append(")");

    FileNameExtensionFilter filter = new FileNameExtensionFilter(description.toString(),
        extensions);
    fileChooser.setFileFilter(filter);
    fileChooser.setAcceptAllFileFilterUsed(false);

    int returnVal = fileChooser.showOpenDialog(parent);
    Map<String, String> result = new HashMap<>();

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      String filePath = selectedFile.getAbsolutePath();

      result.put(IMPORT_FILE_PATH, filePath);
      return result;
    } else {
      return null;
    }
  }
}