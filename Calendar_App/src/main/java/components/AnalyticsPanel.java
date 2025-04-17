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

public class AnalyticsPanel extends JPanel {
  private final JPanel statsPanel;
  private final JPanel eventsByWeekdayPanel;
  private final JPanel eventsByNamePanel;
  private final JPanel onlineOfflinePanel;

  // Analytics data fields
  private int totalEvents;
  private Map<DayOfWeek, Integer> eventsByWeekday;
  private Map<String, Long> eventsByName;
  private double averageEventsPerDay;
  private List<LocalDate> busiestDays;
  private List<LocalDate> leastBusyDays;
  private double onlineEventsPercentage;
  private double offlineEventsPercentage;
  private boolean hasData = false;

  public AnalyticsPanel() {
    this.setLayout(new BorderLayout());

    // Stats panel
    statsPanel = new JPanel(new GridLayout(4, 1));
    statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

    eventsByWeekdayPanel = new JPanel(new GridLayout(7, 1));
    eventsByWeekdayPanel.setBorder(BorderFactory.createTitledBorder("Events by Weekday"));

    eventsByNamePanel = new JPanel(new GridLayout(0, 2));
    eventsByNamePanel.setBorder(BorderFactory.createTitledBorder("Events by Name"));

    // Changed from GridLayout to FlowLayout to reduce vertical gap
    onlineOfflinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    onlineOfflinePanel.setBorder(BorderFactory.createTitledBorder("Online & Offline"));

    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.add(statsPanel, BorderLayout.NORTH);

    JPanel distributionPanel = new JPanel(new GridLayout(3, 1));
    distributionPanel.add(eventsByWeekdayPanel);
    distributionPanel.add(eventsByNamePanel);
    distributionPanel.add(onlineOfflinePanel);

    contentPanel.add(distributionPanel, BorderLayout.CENTER);

    this.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
  }

  public void updateData(
      int totalEvents,
      Map<DayOfWeek, Integer> eventsByWeekday,
      Map<String, Long> eventsByName,
      double averageEventsPerDay,
      List<LocalDate> busiestDays,
      List<LocalDate> leastBusyDays,
      double onlineEventsPercentage,
      double offlineEventsPercentage) {

    this.totalEvents = totalEvents;
    this.eventsByWeekday = eventsByWeekday;
    this.eventsByName = eventsByName;
    this.averageEventsPerDay = averageEventsPerDay;
    this.busiestDays = busiestDays;
    this.leastBusyDays = leastBusyDays;
    this.onlineEventsPercentage = onlineEventsPercentage;
    this.offlineEventsPercentage = offlineEventsPercentage;
    this.hasData = true;

    refreshDisplay();
  }

  public void refreshDisplay() {
    if (!hasData) {
      return;
    }

    statsPanel.removeAll();
    eventsByWeekdayPanel.removeAll();
    eventsByNamePanel.removeAll();
    onlineOfflinePanel.removeAll();

    JPanel totalEventsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    totalEventsPanel.add(new JLabel("Total Events: " + totalEvents));

    JPanel avgEventsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    avgEventsPanel.add(new JLabel(String.format("Average Events Per Day: %.2f",
        averageEventsPerDay)));

    JPanel busiestDayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    busiestDayPanel.add(new JLabel("Busiest Day(s): " +
        formatDateList(busiestDays)));

    JPanel leastBusyDayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    leastBusyDayPanel.add(new JLabel("Least Busy Day(s): " +
        formatDateList(leastBusyDays)));

    statsPanel.add(totalEventsPanel);
    statsPanel.add(avgEventsPanel);
    statsPanel.add(busiestDayPanel);
    statsPanel.add(leastBusyDayPanel);

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
      eventsByName.entrySet().stream()
          .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
          .limit(10)
          .forEach(entry -> {
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            namePanel.add(new JLabel(entry.getKey() + ": " + entry.getValue()));
            eventsByNamePanel.add(namePanel);
          });
    }

    // Modified to add labels directly to the panel without creating separate panels
    onlineOfflinePanel.add(new JLabel(String.format("Online: %.2f%%", onlineEventsPercentage)));
    onlineOfflinePanel.add(new JLabel(String.format("Offline: %.2f%%", offlineEventsPercentage)));

    statsPanel.revalidate();
    statsPanel.repaint();
    eventsByWeekdayPanel.revalidate();
    eventsByWeekdayPanel.repaint();
    eventsByNamePanel.revalidate();
    eventsByNamePanel.repaint();
    onlineOfflinePanel.revalidate();
    onlineOfflinePanel.repaint();
  }

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