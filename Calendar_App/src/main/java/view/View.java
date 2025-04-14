package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

    mainPanel.add((JPanel) monthPanel);
    this.add(mainPanel, BorderLayout.CENTER);
    this.setLocationRelativeTo(null);
  }

  private void renderMonth() {
    this.currentView = ViewTypes.MONTH;
    this.mainPanel.removeAll();
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

  public void setExportPath(String exportPath) {
    this.exportPath = exportPath;
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
