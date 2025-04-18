package components;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Set;

import view.ViewTypes;

/**
 * Interface that outlines the getters and setters required by
 * the header component. This allows the view to send and query
 * information from this component.
 */
public interface IHeaderPanel {

  /**
   * Gets the name from the calendar field
   * used to create new calendars.
   * @return the calendar field name.
   */
  public String getCalendarFieldName();

  /**
   * Gets the name of the active calendar
   * from the calendar dropdown.
   * @return the active calendar name.
   */
  public String getCalendarDropdownValue();

  /**
   * Gets the active zone id.
   * @return the active zone id.
   */
  public String getZoneIdString();

  /**
   * Sets the calendar keys which are displayed
   * in the calendar keys dropdown. This allows
   * users to select the active calendar.
   * @param calendarKeys the calendar keys (names) to set.
   */
  public void setCalendarKeys(Set<String> calendarKeys);

  /**
   * Sets the current view out of the available
   * view types.
   * @param view the view type to set.
   */
  public void setCurrentView(ViewTypes view);

  /**
   * Sets the time zone associated with this
   * component.
   * @param zoneId the zone id corresponding to the timezone.
   */
  public void setTimeZone(ZoneId zoneId);

  /**
   * Sets the current month associated with this
   * component.
   * @param month the month to set.
   */
  public void setMonth(YearMonth month);

  /**
   * Sets the current date associated with this
   * component.
   * @param date the date to set.
   */
  public void setDate(LocalDate date);

  /**
   * Selects the day button in the day radio
   * buttons group.
   */
  public void selectDayRadio();

  /**
   * Gets the import file from the file choooser.
   * @return the import selected.
   */
  public File getImportFile();

}