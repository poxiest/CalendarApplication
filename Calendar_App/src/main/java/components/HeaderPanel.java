package components;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import view.ViewTypes;

/**
 * An implementation of the IHeaderPanel interface which constructs
 * a JPanel with multiple nested components. These components communicate
 * with one another through getting and setting values as well as action
 * listeners. This header displays an indicator of the current day/month,
 * radio buttons for switching between available views, a dropdown used
 * to select the active calendar. A new calendar modal used to create
 * new calendars, navigation buttons for both the daily and monthly views,
 * and import and export buttons for importing/exporting files.
 */
public class HeaderPanel extends JPanel implements IHeaderPanel {
  private JButton showAddCalendarDisplayButton;
  private JLabel instantLabel;
  private JLabel zoneLabel;
  private JComboBox<String> calendarDropdown;
  private JButton prevButton;
  private JButton nextButton;
  private JButton createCalendarButton;
  private JFrame createCalendarDialog;
  private ViewTypes currentView;
  private YearMonth currentMonth;
  private LocalDate currentDate;
  private JTextField calendarNameField;
  private JComboBox<String> timeZoneComboBox;
  private IDayMonthRadioPanel dayMonthRadioPanel;
  private ActionListener featureActionListener;
  private JButton exportButton;
  private JButton importButton;
  private JButton analyticsButton;
  private File importFile;

  /**
   * Constructor for the header panel which accepts the current month to set,
   * the current date, the current zone id, as well as listeners for feature
   * and display events. Feature events correspond to action events that require
   * the controller and model, display events correspond to display changes.
   * @param currentMonth the current month in the view.
   * @param currentDate the current date in the view.
   * @param currentZoneId the current zoneId in the view.
   * @param featureActionListener the feature action listener.
   * @param displayActionListener the display action listener.
   */
  public HeaderPanel(
      YearMonth currentMonth, 
      LocalDate currentDate, 
      ZoneId currentZoneId,
      ActionListener featureActionListener,
      ActionListener displayActionListener
  ) {
    this.featureActionListener = featureActionListener;

    this.analyticsButton = new JButton("Analytics");
    this.exportButton = new JButton("Export");
    this.importButton = new JButton("Import");

    this.prevButton = new JButton("<");
    this.nextButton = new JButton(">");
    this.createCalendarButton = new JButton("Create");

    dayMonthRadioPanel = new DayMonthRadioPanel(
        featureActionListener,
        displayActionListener
    );
    
    this.showAddCalendarDisplayButton = new JButton("New Calendar");
    this.instantLabel = new JLabel();
    instantLabel.setText(" " + currentMonth.getMonth() + " " + currentMonth.getYear());

    this.zoneLabel = new JLabel();
    this.setTimeZone(currentZoneId);

    this.currentView = ViewTypes.MONTH;
    this.currentMonth = currentMonth;
    this.currentDate = currentDate;

    Set<String> calendarKeys = new HashSet<String>();
    calendarKeys.add("default");
    calendarDropdown = new JComboBox<>();
    this.setCalendarKeys(calendarKeys);

    this.add(instantLabel);
    this.add(zoneLabel);
    this.add((JPanel) dayMonthRadioPanel);
    this.add(calendarDropdown);
    this.add(showAddCalendarDisplayButton);
    this.add(prevButton);
    this.add(nextButton);
    this.add(importButton);
    this.add(exportButton);
    this.add(analyticsButton);

    this.setLayout(new GridLayout(0, 10));

    this.calendarNameField = new JTextField(20);
    this.initializeActionCommands();
    this.initializeActionListeners();
  }
  
  private void initializeActionListeners() {
    analyticsButton.addActionListener(featureActionListener);
    importButton.addActionListener(featureActionListener);
    exportButton.addActionListener(featureActionListener);
    analyticsButton.addActionListener(e -> {});
    analyticsButton.setActionCommand("show-analytics");
    importButton.addActionListener(e -> showFileChooserDisplay());
    showAddCalendarDisplayButton.addActionListener(e -> showAddCalendarDisplay());
    prevButton.addActionListener(featureActionListener);
    prevButton.addActionListener(e -> {
      handleNextPrevButtonAction(-1);
      setInstantLabel();
    });
    nextButton.addActionListener(featureActionListener);
    nextButton.addActionListener(e -> {
      handleNextPrevButtonAction(1);
      setInstantLabel();
    });
    createCalendarButton.addActionListener(featureActionListener);
    createCalendarButton.addActionListener(e -> {
      this.createCalendarDialog.setVisible(false);
    });
    calendarDropdown.addActionListener(featureActionListener);
  }

  private void initializeActionCommands() {
    prevButton.setActionCommand("prev");
    nextButton.setActionCommand("next");
    createCalendarButton.setActionCommand("create-calendar");
    calendarDropdown.setActionCommand("use-calendar");
    importButton.setActionCommand("import");
    exportButton.setActionCommand("export");
  }

  private void showFileChooserDisplay() {
    final JFileChooser fchooser = new JFileChooser(".");
    FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV", "csv");
    fchooser.setFileFilter(csvFilter);
    int retValue = fchooser.showOpenDialog(this.getParent());
    if (retValue == JFileChooser.APPROVE_OPTION) {
      this.importFile = fchooser.getSelectedFile();
    }
  }
  
  private void showAddCalendarDisplay() {
    createCalendarDialog = new JFrame("Create Calendar");
    JPanel dialogPanel = new JPanel();
    createCalendarDialog.setSize(325,180);
    createCalendarDialog.setResizable(false);
    calendarNameField = new JTextField(25);
    timeZoneComboBox = new JComboBox<String>(TimeZone.getAvailableIDs());
    dialogPanel.add(new JLabel("Calendar Name"));
    dialogPanel.add(calendarNameField);
    dialogPanel.add(new JLabel("Time Zone"));
    dialogPanel.add(timeZoneComboBox);
    dialogPanel.add(createCalendarButton);
    createCalendarDialog.add(dialogPanel);
    createCalendarDialog.setLocationRelativeTo(this.getParent());
    createCalendarDialog.setVisible(true);
  }

  @Override
  public File getImportFile() {
    return this.importFile;
  }

  @Override
  public String getCalendarDropdownValue() {
    return this.calendarDropdown.getSelectedItem().toString();
  }

  @Override
  public String getCalendarFieldName() {
    return this.calendarNameField.getText();
  }

  @Override
  public String getZoneIdString() {
    return this.timeZoneComboBox.getSelectedItem().toString();
  }

  @Override
  public void setMonth(YearMonth month) {
    this.currentMonth = month;
    this.setInstantLabel();
  }

  @Override
  public void setDate(LocalDate date) {
    this.currentDate = date;
    this.setInstantLabel();
  }

  @Override
  public void setCalendarKeys(Set<String> calendarKeys) {
    Set<String> keysInDropdown = new HashSet<>();
    for (int i = 0; i < calendarDropdown.getItemCount(); i++) {
      keysInDropdown.add(calendarDropdown.getItemAt(i));
    }
    for (String key: calendarKeys) {
      if (! keysInDropdown.contains(key)) {
        this.calendarDropdown.addItem(key); 
      }
    }
  }

  @Override
  public void setCurrentView(ViewTypes view) {
    this.currentView = view;
    this.setInstantLabel();
  }

  @Override
  public void setTimeZone(ZoneId zoneId) {
    zoneLabel.setText("Timezone: " + TimeZone.getTimeZone(zoneId)
        .getDisplayName(false, TimeZone.SHORT));
  }

  @Override
  public void selectDayRadio() {
    this.dayMonthRadioPanel.selectDayRadio();
  }

  @Override
  public void selectMonthRadio() {
    this.dayMonthRadioPanel.selectMonthRadio();
  }

  private void setInstantLabel() {
    switch (this.currentView) {
      case DAY:
        instantLabel.setText(" " + currentDate.getMonth().toString() 
            + " " + currentDate.getDayOfMonth() + ", " + currentDate.getYear());
        break;
      case MONTH:
        instantLabel.setText(" " + currentMonth.getMonth() + " " + currentMonth.getYear());
        break;
      default:
        break;
    }
  }

  private void handleNextPrevButtonAction(int offset) {
    switch (currentView) {
      case DAY:
        currentDate = currentDate.plusDays(offset);
        break;
      case MONTH:
        currentMonth = currentMonth.plusMonths(offset);
        break;
      default:
        break;
    }
  }

}
