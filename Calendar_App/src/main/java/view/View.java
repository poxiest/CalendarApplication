package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

import components.AnalyticsPanel;
import components.CreateEventsFormPanel;
import components.EventsListPanel;
import components.HeaderPanel;
import components.ICreateEventsFormPanel;
import components.IEventsListPanel;
import components.IHeaderPanel;
import components.IMonthPanel;
import components.MonthPanel;
import controller.EditType;
import controller.Features;

/**
 * This is the class representation of the view for running the app
 * in GUI mode. This view includes a variety of components and upholds
 * ActionListeners which listen for events which occur in the components.
 * This view supports setter methods which are used by the controller
 * to provide relevant information to the view.
 */
public class View extends JFrame implements IView {

  private ViewTypes currentView;
  private YearMonth currentMonth;
  private LocalDate currentDate;
  private ZoneId currentZoneId;
  private JPanel mainPanel;
  private IMonthPanel monthPanel;
  private IEventsListPanel eventsListPanel;
  private ICreateEventsFormPanel createEventsFormPanel;
  private IHeaderPanel headerPanel;
  private Features features;
  private String exportPath;

  // Analytics related fields
  private JPanel analyticsDateRangePanel;
  private JTextField startDateField;
  private JTextField endDateField;
  private AnalyticsPanel analyticsPanel;
  private boolean hasAnalyticsData = false;

  /**
   * A constructor for the view class which takes no parameters.
   * This constructor displays the GUI JFrame and initializes
   * ActionListeners for components.
   */
  public View() {
    super("Calendar App");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(975, 650);
    this.setMinimumSize(new Dimension(1250, 650));
    this.setLayout(new BorderLayout());

    this.currentView = ViewTypes.MONTH;
    this.currentMonth = YearMonth.now();
    this.currentDate = LocalDate.now();
    this.currentZoneId = ZoneId.systemDefault();

    FeatureActionListener featureActionListener = new FeatureActionListener(this);
    DisplayActionListener displayActionListener = new DisplayActionListener();

    headerPanel = new HeaderPanel(
        currentMonth,
        currentDate,
        currentZoneId,
        featureActionListener,
        displayActionListener
    );

    this.add((JPanel) headerPanel, BorderLayout.NORTH);
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(1, 1));

    monthPanel = new MonthPanel(currentMonth, featureActionListener);
    eventsListPanel = new EventsListPanel(featureActionListener);
    createEventsFormPanel = new CreateEventsFormPanel(featureActionListener, currentDate);

    analyticsPanel = new AnalyticsPanel();
    analyticsDateRangePanel = new JPanel(new FlowLayout());
    analyticsDateRangePanel.setBorder(BorderFactory.createTitledBorder("Date Range"));

    JLabel startLabel = new JLabel("Start Date (yyyy-MM-dd):");
    startDateField = new JTextField(10);
    startDateField.setText(LocalDate.now().minusMonths(1).toString());

    JLabel endLabel = new JLabel("End Date (yyyy-MM-dd):");
    endDateField = new JTextField(10);
    endDateField.setText(LocalDate.now().toString());

    JButton generateButton = new JButton("Generate Analytics");
    generateButton.setActionCommand("generate-analytics");
    generateButton.addActionListener(featureActionListener);

    analyticsDateRangePanel.add(startLabel);
    analyticsDateRangePanel.add(startDateField);
    analyticsDateRangePanel.add(endLabel);
    analyticsDateRangePanel.add(endDateField);
    analyticsDateRangePanel.add(generateButton);

    mainPanel.add((JPanel) monthPanel);
    this.add(mainPanel, BorderLayout.CENTER);
    this.setLocationRelativeTo(null);
  }

  private void renderMonth() {
    this.currentView = ViewTypes.MONTH;
    this.headerPanel.setCurrentView(currentView);
    this.mainPanel.removeAll();
    this.mainPanel.setLayout(new GridLayout(1, 1));
    this.mainPanel.add((JPanel) monthPanel);
    this.monthPanel.rerender();
    this.mainPanel.revalidate();
    this.mainPanel.repaint();
  }

  private void renderDay() {
    this.currentView = ViewTypes.DAY;
    this.headerPanel.setCurrentView(currentView);
    this.headerPanel.setDate(currentDate);
    this.mainPanel.removeAll();
    this.mainPanel.setLayout(new GridLayout(1, 2));
    this.mainPanel.add((JPanel) eventsListPanel);
    this.mainPanel.add((JPanel) createEventsFormPanel);
    this.eventsListPanel.rerender();
    this.createEventsFormPanel.setLocalDate(currentDate);
    this.createEventsFormPanel.rerender();
    this.mainPanel.revalidate();
    this.mainPanel.repaint();
  }

  private void renderAnalytics() {
    this.currentView = ViewTypes.ANALYTICS;
    this.headerPanel.setCurrentView(currentView);
    this.mainPanel.removeAll();
    this.mainPanel.setLayout(new BorderLayout());
    this.mainPanel.add(analyticsDateRangePanel, BorderLayout.NORTH);
    this.mainPanel.add(analyticsPanel, BorderLayout.CENTER);

    if (hasAnalyticsData) {
      analyticsPanel.refreshDisplay();
    }

    this.mainPanel.revalidate();
    this.mainPanel.repaint();
  }

  @Override
  public void renderFrame() {
    this.setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    this.features = features;
  }

  @Override
  public void setCalendarKeys(Set<String> calendarKeys) {
    this.headerPanel.setCalendarKeys(calendarKeys);
  }

  @Override
  public void setEvents(List<IViewEvent> events) {
    this.eventsListPanel.setEvents(events);
  }

  @Override
  public void setTimeZone(ZoneId zoneId) {
    this.currentZoneId = zoneId;
    this.headerPanel.setTimeZone(zoneId);
  }

  @Override
  public void setAnalyticsData(
      int totalEvents,
      Map<DayOfWeek, Integer> eventsByWeekday,
      Map<String, Long> eventsByName,
      double averageEventsPerDay,
      List<LocalDate> busiestDays,
      List<LocalDate> leastBusyDays,
      List<LocalDate> busiestDaysByHours,
      List<LocalDate> leastBusyDaysByHours,
      double onlineEventsPercentage,
      double offlineEventsPercentage) {

    analyticsPanel.updateData(
        totalEvents,
        eventsByWeekday,
        eventsByName,
        averageEventsPerDay,
        busiestDays,
        leastBusyDays,
        busiestDaysByHours,
        leastBusyDaysByHours,
        onlineEventsPercentage,
        offlineEventsPercentage
    );
    hasAnalyticsData = true;
  }

  public void setExportPath(String exportPath) {
    this.exportPath = exportPath;
  }

  private class FeatureActionListener implements ActionListener {

    private JFrame contextFrame;

    private FeatureActionListener(JFrame contextFrame) {
      this.contextFrame = contextFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        String actionCommand = e.getActionCommand();
        switch (actionCommand) {
          case "prev":
            handleNextPrevButtonAction(-1);
            break;
          case "next":
            handleNextPrevButtonAction(1);
            break;
          case "day-radio":
            renderDay();
            break;
          case "show-analytics":
            renderAnalytics();
            break;
          case "create-calendar":
            features.createCalendar(
                headerPanel.getCalendarFieldName(),
                headerPanel.getZoneIdString());
            showSuccessMessage("Create success");
            break;
          case "use-calendar":
            String calendarName = headerPanel.getCalendarDropdownValue();
            features.useCalendar(calendarName);
            features.queryEventsOnDate(currentDate);
            eventsListPanel.rerender();
            showSuccessMessage("Using calendar: " + calendarName);
            break;
          case "create-single":
            features.createSingle(createEventsFormPanel.getSingleEventModel());
            features.queryEventsOnDate(currentDate);
            eventsListPanel.rerender();
            showSuccessMessage("Create success");
            break;
          case "create-recurring":
            features.createRecurring(createEventsFormPanel.getRecurringEventModel());
            features.queryEventsOnDate(currentDate);
            eventsListPanel.rerender();
            showSuccessMessage("Create success");
            break;
          case "edit-single":
            features.editSingle(
                eventsListPanel.getSelectedEventId(),
                eventsListPanel.getEditSingleEvent());
            features.queryEventsOnDate(currentDate);
            eventsListPanel.rerender();
            showSuccessMessage("Edit success");
            break;
          case "edit-recurring-single":
            features.editRecurring(
                eventsListPanel.getSelectedEventId(),
                eventsListPanel.getEditRecurringEvent(),
                EditType.SINGLE);
            features.queryEventsOnDate(currentDate);
            eventsListPanel.rerender();
            showSuccessMessage("Edit success");
            break;
          case "edit-recurring-this-and-following":
            features.editRecurring(
                eventsListPanel.getSelectedEventId(),
                eventsListPanel.getEditRecurringEvent(),
                EditType.THISANDFOLLOWING);
            features.queryEventsOnDate(currentDate);
            eventsListPanel.rerender();
            showSuccessMessage("Edit success");
            break;
          case "edit-recurring-all":
            features.editRecurring(
                eventsListPanel.getSelectedEventId(),
                eventsListPanel.getEditRecurringEvent(),
                EditType.ALL);
            features.queryEventsOnDate(currentDate);
            eventsListPanel.rerender();
            showSuccessMessage("Edit success");
            break;
          case "import":
            if (headerPanel.getImportFile() == null) {
              break;
            }
            features.importFile(headerPanel.getImportFile());
            features.queryEventsOnDate(currentDate);
            showSuccessMessage("Import success");
            break;
          case "export":
            features.export();
            showSuccessMessage("Exported to file at: " + exportPath);
            break;
          case "generate-analytics":
            features.queryAnalytics(
                LocalDate.parse(startDateField.getText()),
                LocalDate.parse(endDateField.getText())
            );
            break;
          default:
            currentView = ViewTypes.DAY;
            currentDate = currentMonth.atDay(Integer.parseInt(actionCommand));
            headerPanel.selectDayRadio();
            features.queryEventsOnDate(currentDate);
            renderDay();
            break;
        }
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(
            contextFrame,
            ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
      }
    }

    private void showSuccessMessage(String message) {
      JOptionPane.showMessageDialog(
          contextFrame,
          message,
          "Success",
          JOptionPane.INFORMATION_MESSAGE
      );
    }

    private void handleNextPrevButtonAction(int offset) throws IOException {
      switch (currentView) {
        case DAY:
          currentDate = currentDate.plusDays(offset);
          headerPanel.setDate(currentDate);
          features.queryEventsOnDate(currentDate);
          renderDay();
          break;
        case MONTH:
          currentMonth = currentMonth.plusMonths(offset);
          headerPanel.setMonth(currentMonth);
          monthPanel.setMonth(currentMonth);
          renderMonth();
          break;
        default:
          break;
      }
    }
  }

  private class DisplayActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      String actionCommand = e.getActionCommand();
      if (actionCommand.compareTo("month-radio") == 0) {
        renderMonth();
      }
    }
  }
}