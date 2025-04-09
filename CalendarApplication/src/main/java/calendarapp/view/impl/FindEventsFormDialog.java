package calendarapp.view.impl;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static calendarapp.utils.Constants.FIND_END_TIME;
import static calendarapp.utils.Constants.FIND_EVENT_NAME;
import static calendarapp.utils.Constants.FIND_ON;
import static calendarapp.utils.Constants.FIND_START_TIME;

/**
 * A dialog for searching events by name and either a specific date or a date-time range.
 * Returns the search criteria as a map when submitted.
 */
public class FindEventsFormDialog extends JDialog {
  private final JFrame parent;
  private Map<String, String> result;

  private JRadioButton findInBetweenRadio;
  private JRadioButton findOnRadio;

  /**
   * Constructs the dialog used to input criteria for finding events.
   *
   * @param parent the parent frame of this dialog
   */
  public FindEventsFormDialog(JFrame parent) {
    super(parent, "Find Events", true);
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
   * Builds the user interface for the event search form with search options and fields.
   */
  private void constructCalendarPanel() {
    JTextField eventNameField;
    JSpinner startDateField;
    JSpinner endDateField;
    JSpinner findOnField;
    setSize(550, 350);
    setLocationRelativeTo(parent);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    formPanel.add(new JLabel("Event Name:"));
    eventNameField = new JTextField();
    formPanel.add(eventNameField);

    formPanel.add(new JLabel("Search options:"));
    JPanel recurrenceOptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    findInBetweenRadio = new JRadioButton("Find In Between");
    findOnRadio = new JRadioButton("Find on");
    ButtonGroup recurrenceGroup = new ButtonGroup();
    recurrenceGroup.add(findInBetweenRadio);
    recurrenceGroup.add(findOnRadio);
    recurrenceOptionPanel.add(findInBetweenRadio);
    recurrenceOptionPanel.add(findOnRadio);
    formPanel.add(recurrenceOptionPanel);

    formPanel.add(new JLabel("Start Time:"));
    startDateField = createDateTimeSpinner(new Date(), "MM-dd-yyyy HH:mm", Calendar.MINUTE);
    formPanel.add(startDateField);
    startDateField.setEnabled(false);

    formPanel.add(new JLabel("End Time:"));
    endDateField = createDateTimeSpinner(Date.from(new Date().toInstant().plus(1,
        ChronoUnit.DAYS)), "MM-dd-yyyy HH:mm", Calendar.MINUTE);
    formPanel.add(endDateField);
    endDateField.setEnabled(false);

    formPanel.add(new JLabel("Find on:"));
    findOnField = createDateTimeSpinner(new Date(), "MM-dd-yyyy", Calendar.DAY_OF_MONTH);
    formPanel.add(findOnField);
    findOnField.setEnabled(false);

    findInBetweenRadio.addActionListener(e -> {
      startDateField.setEnabled(true);
      endDateField.setEnabled(true);
      findOnField.setEnabled(false);
    });
    findOnRadio.addActionListener(e -> {
      startDateField.setEnabled(false);
      endDateField.setEnabled(false);
      findOnField.setEnabled(true);
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton findButton = new JButton("Find");
    buttonPanel.add(cancelButton);
    buttonPanel.add(findButton);

    cancelButton.addActionListener(e -> {
      result = null;
      dispose();
    });

    findButton.addActionListener(e -> {
      Date startDate = (Date) startDateField.getValue();
      Date endDate = (Date) endDateField.getValue();
      Date findOn = (Date) findOnField.getValue();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
      SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

      String eventName = eventNameField.getText().isEmpty() ? null :
          eventNameField.getText().trim();
      result.put(FIND_EVENT_NAME, eventName);
      result.put(FIND_START_TIME, null);
      result.put(FIND_END_TIME, null);
      result.put(FIND_ON, null);
      if (findInBetweenRadio.isSelected()) {
        result.put(FIND_START_TIME, sdf.format(startDate));
        result.put(FIND_END_TIME, sdf.format(endDate));
      }
      if (findOnRadio.isSelected()) {
        result.put(FIND_ON, sdfDate.format(findOn));
      }
      dispose();
    });

    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel);
  }

  /**
   * Creates and returns a date/time spinner for user input.
   *
   * @param initialDate   the initial date to display
   * @param format        the format of the date/time in the spinner
   * @param calendarField the calendar field for increment steps
   * @return the configured JSpinner component
   */
  private JSpinner createDateTimeSpinner(Date initialDate, String format, int calendarField) {
    SpinnerDateModel model = new SpinnerDateModel(initialDate, null, null, calendarField);
    JSpinner spinner = new JSpinner(model);
    JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, format);
    spinner.setEditor(editor);
    return spinner;
  }

  /**
   * Displays the event search dialog and returns the search criteria entered by the user.
   *
   * @return a map of search parameters or null if cancelled
   */
  public Map<String, String> showDialog() {
    setVisible(true);
    return result;
  }
}
