package view;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import controller.Features;

/**
 * An interface outlining methods upheld by the main view class, 
 * which renders the GUI. These methods are used by the controller
 * to provide this View with necessary data.
 */
public interface IView {

  /**
   * Renders the GUI.
   */
  public void renderFrame();

  /**
   * Sets the calendar keys, or calendar names, that
   * are currently in use by the user.
   * @param calendarKeys the calendar keys to set.
   */
  public void setCalendarKeys(Set<String> calendarKeys);

  /**
   * Sets the timezone currently in use by the user.
   * @param zoneId the zoneId denoting the timezone.
   */
  public void setTimeZone(ZoneId zoneId);

  /**
   * Add features to this view. The Features object
   * upholds methods that are upheld by the controller.
   * @param features the features object to provision this view with.
   */
  public void addFeatures(Features features);

  /**
   * Set the events relevant to the current view.
   * The events provided by the controller are of type
   * IViewEvents, whos values are copies of those
   * upheld by the model.
   * @param events the view events to set for the view.
   */
  public void setEvents(List<IViewEvent> events);

  /**
   * Sets the export path on exporting the calendar.
   * This value indicates where the exported file is
   * saved on the user's machine.
   * @param exportPath the export path of the output file.
   */
  public void setExportPath(String exportPath);

  /**
   * Sets all analytics data for display.
   * @param totalEvents the total number of events
   * @param eventsByWeekday map of day of week to event count
   * @param eventsByName map of event name to event count
   * @param averageEventsPerDay the average number of events per day
   * @param busiestDays list of the busiest days
   * @param leastBusyDays list of the least busy days
   * @param onlineEventsPercentage percentage of events that are online
   * @param offlineEventsPercentage percentage of events that are offline
   */
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
      double offlineEventsPercentage
  );
}
