package model;

import java.time.ZoneId;

import calendar.ConflictException;
import events.IEvent;

/**
 * An interface for a calendar model with an 
 * associated time zone id.
 */
public interface IZonedCalendarModel extends ICalendarModel {

  /**
   * Gets the zone id associated with this
   * zoned calendar model.
   * @return the zone id.
   */
  public ZoneId getZoneId();

  /**
   * Sets the zone id associated with this
   * zoned calendar model and updates all events
   * in the calendar to reflect this change.
   * @param zoneId the zone id to set.
   */
  public void updateCalendarZoneId(ZoneId zoneId);

  /**
   * Adds an event to this calendar and checks for conflicts.
   * @param event the event to add to the calendar.
   * @throws ConflictException if there is a conflict.
   */
  public void addEventAndCheckConflicts(IEvent event) throws ConflictException;
  
}
