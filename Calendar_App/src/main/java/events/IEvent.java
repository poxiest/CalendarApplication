package events;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * This interface represents a calendar event and upholds
 * several operations for retrieving and modifying event
 * information.
 */
public interface IEvent {

  /**
   * Gets the unique id associated with this event.
   * @return the id.
   */
  public UUID getId();

  /**
   * Gets the subject of this event.
   * @return the subject of this event.
   */
  public String getSubject();

  /**
   * Gets the startDate of this event.
   * @return the startDate of this event.
   */
  public LocalDate getStartDate();

  /**
   * Gets the endDate of this event.
   * @return the endDate of this event.
   */
  public LocalDate getEndDate();

  /**
   * Gets the startTime of this event.
   * @return the startTime of this event.
   */
  public LocalTime getStartTime();

  /**
   * Gets the endTime of this event.
   * @return the endTime of this event.
   */
  public LocalTime getEndTime();

  /**
   * Gets the startDateTime of this event.
   * @return the startDateTime of this event.
   */
  public LocalDateTime getStartDateTime();

  /**
   * Gets the endDateTime of this event.
   * @return the endDateTime of this event.
   */
  public LocalDateTime getEndDateTime();

  /**
   * Gets the isAllDayEvent of this event.
   * @return the isAllDayEvent of this event.
   */
  public Boolean isAllDayEvent();

  /**
   * Gets the description of this event.
   * @return the description of this event.
   */
  public String getDescription();

  /**
   * Gets the location of this event.
   * @return the location of this event.
   */
  public String getLocation();

  /**
   * Gets the isPrivate of this event.
   * @return the isPrivate of this event.
   */
  public Boolean isPrivate();

}