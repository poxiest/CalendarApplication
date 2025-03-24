package calendarapp.model.impl;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.stream.Collectors;

import calendarapp.controller.InvalidCommandException;
import calendarapp.model.EventConflictException;
import calendarapp.model.ICalendar;
import calendarapp.model.ICalendarExporter;
import calendarapp.model.ICalendarModel;
import calendarapp.model.ICalendarRepository;
import calendarapp.model.dto.CopyEventDTO;
import calendarapp.model.dto.PrintEventsDTO;
import calendarapp.utils.TimeUtil;

import static calendarapp.model.impl.Constants.EXPORTER_MAP;

/**
 * The CalendarModel class implements the ICalendarModel interface
 * and is responsible for managing calendar events, including creating, editing,
 * and displaying events, handling conflicts, and exporting events in a specific format.
 */
public class CalendarModel implements ICalendarModel {

  private final ICalendarRepository calendarRepository;
  private ICalendar activeCalendar;

  /**
   * Constructs a CalendarModel object, initializing the event list and day mapping.
   */
  public CalendarModel() {
    calendarRepository = new CalendarRepository();
    calendarRepository.addCalendar("default", null, new EventRepository());
    activeCalendar = calendarRepository.getCalendar("default");
  }

  /**
   * Creates a new event, either recurring or one-time.
   *
   * @param eventName         The name of the event.
   * @param startTime         The start time of the event.
   * @param endTime           The end time of the event.
   * @param recurringDays     A string of characters representing recurring days.
   * @param occurrenceCount   The number of occurrences of the event (for recurring events).
   * @param recurrenceEndDate The end date of the recurrence (for recurring events).
   * @param description       The description of the event.
   * @param location          The location of the event.
   * @param visibility        The visibility of the event.
   * @param autoDecline       Flag indicating whether the event should auto-decline
   *                          conflicting events.
   * @throws EventConflictException if the event conflicts with an existing event.
   */
  @Override
  public void createEvent(String eventName, Temporal startTime, Temporal endTime,
                          String recurringDays, String occurrenceCount, Temporal recurrenceEndDate,
                          String description, String location, String visibility,
                          boolean autoDecline) throws EventConflictException {
    activeCalendar.getEventRepository().create(eventName, startTime, endTime, description, location,
        visibility, recurringDays, occurrenceCount, recurrenceEndDate, true);
  }

  /**
   * Edits an event based on the specified property and value.
   *
   * @param eventName The name of the event.
   * @param startTime The start time of the event.
   * @param endTime   The end time of the event.
   * @param property  The property to be edited.
   * @param value     The new value of the property.
   */
  @Override
  public void editEvent(String eventName, Temporal startTime, Temporal endTime, String property,
                        String value, boolean isRecurringEvents) {
    activeCalendar.getEventRepository().update(eventName, startTime, endTime, property, value
    );
  }

  /**
   * Returns a list of events that fall within the given date range, formatted for display.
   *
   * @param startDateTime The start date-time for the range.
   * @param endDateTime   The end date-time for the range.
   * @return A list of formatted event strings.
   */
  @Override
  public List<PrintEventsDTO> getEventsForPrinting(Temporal startDateTime, Temporal endDateTime) {
    if (endDateTime == null) {
      endDateTime = TimeUtil.GetStartOfNextDay(startDateTime);
    }
    return activeCalendar.getEventRepository().getOverlappingEvents(startDateTime, endDateTime)
        .stream().map(event -> PrintEventsDTO.builder()
            .eventName(event.getName())
            .startTime(event.getStartTime())
            .endTime(event.getEndTime())
            .location(event.getLocation())
            .build()).collect(Collectors.toList());
  }

  /**
   * Exports the events to a CSV file.
   *
   * @param fileName The name of the file to export.
   * @return The file path of the exported CSV.
   */
  @Override
  public String export(String fileName) {
    String fileExtension = getFileExtension(fileName);
    if (!Constants.SupportExportFormats.SUPPORTED_EXPORT_FORMATS.contains(fileExtension)) {
      throw new IllegalArgumentException("Unsupported export format: " + fileExtension
          + ". Supported formats are: " + Constants.SupportExportFormats.SUPPORTED_EXPORT_FORMATS);
    }
    ICalendarExporter exporter = EXPORTER_MAP.get(fileExtension);

    if (exporter == null) {
      throw new IllegalStateException("No exporter for format: " + fileExtension);
    }

    return activeCalendar.getEventRepository().export(fileName, exporter);
  }

  /**
   * Checks the availability status of the user for a given date-time.
   *
   * @param dateTime The date-time to check.
   * @return The availability status ("busy" or "available").
   */
  @Override
  public String showStatus(Temporal dateTime) {
    boolean isBusy = activeCalendar.getEventRepository().isActiveAt(dateTime);
    return isBusy ? Constants.Status.BUSY : Constants.Status.AVAILABLE;
  }

  @Override
  public void createCalendar(String calendarName, String timezone) {
    calendarRepository.addCalendar(calendarName, timezone, new EventRepository());
  }

  @Override
  public void editCalendar(String calendarName, String propertyName, String propertyValue) {
    calendarRepository.editCalendar(calendarName, propertyName, propertyValue);
  }

  @Override
  public void setCalendar(String calendarName) {
    ICalendar temp = calendarRepository.getCalendar(calendarName);
    if (temp == null) {
      throw new InvalidCommandException("Calendar does not exist.\n");
    }
    activeCalendar = temp;
  }

  @Override
  public void copyEvent(CopyEventDTO copyEventDTO) {
    calendarRepository.copyCalendarEvents(activeCalendar.getName(), copyEventDTO);
  }

  private String getFileExtension(String filePath) {
    int lastDot = filePath.lastIndexOf(".");
    if (lastDot == -1 || lastDot == filePath.length() - 1) {
      return "";
    }
    return filePath.substring(lastDot + 1);
  }
}
