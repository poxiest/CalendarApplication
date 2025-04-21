package components;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Initializes the AnalyticsPanel with layout and sub panels.
 */
public class AnalyticsPanel extends JPanel {
  private final JPanel statsPanel;
  private final JPanel eventsByWeekdayPanel;
  private final JPanel eventsByNamePanel;

  // Analytics data fields
  private int totalEvents;
  private Map<DayOfWeek, Integer> eventsByWeekday;
  private Map<String, Long> eventsByName;
  private double averageEventsPerDay;
  private List<LocalDate> busiestDays;
  private List<LocalDate> leastBusyDays;
  private List<LocalDate> busiestDaysByHours;
  private List<LocalDate> leastBusyDaysByHours;
  private double onlineEventsPercentage;
  private double offlineEventsPercentage;
  private boolean hasData = false;

  /**
   * Creates and sets up the layout for the AnalyticsPanel with sections for statistics,
   * events by weekday, and events by name.
   */
  public AnalyticsPanel() {
    this.setLayout(new BorderLayout());

    statsPanel = new JPanel(new GridLayout(8, 1));
    statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

    eventsByWeekdayPanel = new JPanel(new GridLayout(7, 1));
    eventsByWeekdayPanel.setBorder(BorderFactory.createTitledBorder("Events by Weekday"));

    eventsByNamePanel = new JPanel(new GridLayout(0, 2));
    eventsByNamePanel.setBorder(BorderFactory.createTitledBorder("Events by Name"));

    JPanel distributionPanel = new JPanel(new GridLayout(3, 1));
    distributionPanel.add(statsPanel);
    distributionPanel.add(eventsByWeekdayPanel);
    distributionPanel.add(eventsByNamePanel);

    this.add(new JScrollPane(distributionPanel), BorderLayout.CENTER);
  }

  /**
   * Updates the analytics data and refreshes the panel display.
   *
   * @param totalEvents total number of events
   * @param eventsByWeekday map of event count by weekday
   * @param eventsByName map of event count by event name
   * @param averageEventsPerDay average number of events per day
   * @param busiestDays list of busiest dates by event count
   * @param leastBusyDays list of least busy dates by event count
   * @param busiestDaysByHours list of busiest dates by total hours
   * @param leastBusyDaysByHours list of least busy dates by total hours
   * @param onlineEventsPercentage percentage of online events
   * @param offlineEventsPercentage percentage of offline events
   */
  public void updateData(
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

    this.totalEvents = totalEvents;
    this.eventsByWeekday = eventsByWeekday;
    this.eventsByName = eventsByName;
    this.averageEventsPerDay = averageEventsPerDay;
    this.busiestDays = busiestDays;
    this.leastBusyDays = leastBusyDays;
    this.busiestDaysByHours = busiestDaysByHours;
    this.leastBusyDaysByHours = leastBusyDaysByHours;
    this.onlineEventsPercentage = onlineEventsPercentage;
    this.offlineEventsPercentage = offlineEventsPercentage;
    this.hasData = true;

    refreshDisplay();
  }

  /**
   * Refreshes the display of the analytics data on the panel.
   */
  public void refreshDisplay() {
    if (!hasData) {
      return;
    }

    statsPanel.removeAll();
    eventsByWeekdayPanel.removeAll();
    eventsByNamePanel.removeAll();

    JPanel totalEventsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    totalEventsPanel.add(new JLabel("Total Events: " + totalEvents));

    JPanel avgEventsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    avgEventsPanel.add(new JLabel(String.format("Average Events Per Day: %.2f",
        averageEventsPerDay)));

    JPanel busiestDayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    busiestDayPanel.add(new JLabel("Most Busy Date(s) by Event Count: " +
        formatDateList(busiestDays)));

    JPanel leastBusyDayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    leastBusyDayPanel.add(new JLabel("Least Busy Date(s) by Event Count: " +
        formatDateList(leastBusyDays)));

    JPanel busiestDayByHoursPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    busiestDayByHoursPanel.add(new JLabel("Most Busy Date(s) by Total Hours: " +
        formatDateList(busiestDaysByHours)));

    JPanel leastBusyDayByHoursPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    leastBusyDayByHoursPanel.add(new JLabel("Least Busy Date(s) by Total Hours: " +
        formatDateList(leastBusyDaysByHours)));

    JPanel onlinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    onlinePanel.add(new JLabel(String.format("Online %%: %.2f", onlineEventsPercentage)));

    JPanel offlinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    offlinePanel.add(new JLabel(String.format("Offline %%: %.2f", offlineEventsPercentage)));

    statsPanel.add(totalEventsPanel);
    statsPanel.add(avgEventsPanel);
    statsPanel.add(busiestDayPanel);
    statsPanel.add(leastBusyDayPanel);
    statsPanel.add(busiestDayByHoursPanel);
    statsPanel.add(leastBusyDayByHoursPanel);
    statsPanel.add(onlinePanel);
    statsPanel.add(offlinePanel);

    if (eventsByWeekday != null) {
      for (DayOfWeek day : DayOfWeek.values()) {
        int count = eventsByWeekday.getOrDefault(day, 0);
        JPanel weekdayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String dayName = day.getDisplayName(TextStyle.FULL, Locale.getDefault());
        weekdayPanel.add(new JLabel(dayName + ": " + count));
        eventsByWeekdayPanel.add(weekdayPanel);
      }
    }

    if (eventsByName != null) {
      eventsByNamePanel.setLayout(new GridLayout(0, 1));
      eventsByName.entrySet().forEach(entry -> {
        JPanel entryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        entryPanel.add(new JLabel("\t" + entry.getKey() + ": " + entry.getValue()));
        eventsByNamePanel.add(entryPanel);
      });
    }

    statsPanel.revalidate();
    statsPanel.repaint();
    eventsByWeekdayPanel.revalidate();
    eventsByWeekdayPanel.repaint();
    eventsByNamePanel.revalidate();
    eventsByNamePanel.repaint();
  }

  /**
   * Formats a list of dates into a comma-separated string.
   *
   * @param dates list of dates to format
   * @return formatted string of dates or "None" if list is empty
   */
  private String formatDateList(List<LocalDate> dates) {
    if (dates == null || dates.isEmpty()) {
      return "None";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < dates.size(); i++) {
      sb.append(dates.get(i));
      if (i < dates.size() - 1) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }
}