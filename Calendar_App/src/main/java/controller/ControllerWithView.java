package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import commands.Create;
import events.IEvent;
import events.IRecurringSequenceEvent;
import events.IRecurringUntilEvent;
import events.RecurringSequenceEvent;
import events.RecurringUntilEvent;
import model.AnalyticsVisitor;
import model.IMultipleCalendarModel;
import view.IView;
import view.IViewEvent;
import view.IViewRecurringEvent;
import view.IViewRecurringSequenceEvent;
import view.IViewRecurringUntilEvent;
import view.ViewEvent;
import view.ViewRecurringSequenceEvent;
import view.ViewRecurringUntilEvent;

/**
 * A controller with a view which controls application flow. This controller
 * provides the view with data from the model, and fetches data from the model
 * which requested by the view. All functionality is contained within a features
 * object which implements the methods available to the view.
 */
public class ControllerWithView extends Controller implements IControllerWithView {

  private IView view;

  /**
   * A default constructor which initializes the input and output
   * for this program.
   * @param calendarModel the calendarModel used to perform operations.
   * @param in the input readable associated with this controller.
   * @param out the output buffer associated with this controller.
   */
  public ControllerWithView(
      IMultipleCalendarModel calendarModel,
      Readable in,
      Appendable out
  ) {
    super(calendarModel, in, out);
  }

  @Override
  public void setView(IView view) {
    this.view = view;
    this.view.addFeatures(new FeaturesImpl());
    view.renderFrame();
  }

  /**
   * An implementation of the features interfaces which implements all functionality
   * advertised by this controller.
   */
  private class FeaturesImpl implements Features {
    
    @Override
    public void createCalendar(String calendarName, String zoneIdString) 
        throws IOException {
      if (calendarName.isEmpty()) {
        throw new IOException("Calendar name cannot be empty");
      }
      if (calendarModel.getCalendarKeys().contains(calendarName)) {
        throw new IOException("A calendar with this name already exists");
      }
      else {
        ZoneId zoneId = ZoneId.of(zoneIdString);
        calendarModel.createCalendar(calendarName, zoneId);
        view.setCalendarKeys(calendarModel.getCalendarKeys());
      }
    }

    @Override
    public void useCalendar(String calendarName) {
      calendarModel.useCalendar(calendarName);
      view.setTimeZone(calendarModel.getActiveCalendar().getZoneId());
    }

    @Override
    public void queryEventsOnDate(LocalDate date) {
      List<IEvent> events = calendarModel.getActiveCalendar().queryDate(date);
      view.setEvents(createViewEvents(events));
    }

    @Override
    public void export() throws IOException {
      handleCommand("export cal calendar.csv");
      view.setExportPath(out.toString());
    }

    @Override
    public void importFile(File importFile) 
        throws FileNotFoundException, IOException {
      Readable fileReader = new FileReader(importFile);
      Scanner scan = new Scanner(fileReader);
      String header = scan.nextLine();
      String headerFormat = "Subject,Start Date,Start Time,End Date,"
          + "End Time,All Day Event,Description,Location,Private";
      if (header.compareTo(headerFormat) != 0) {
        scan.close();
        throw new IOException("CSV Header should be in format: " + headerFormat);
      }
      while (scan.hasNextLine()) {
        String nextLine = scan.nextLine();
        String[] fields = nextLine.split(",");
        String subject = fields[0];
        String startDateString = fields[1];
        String startTimeString = fields[2];
        if (startTimeString.isEmpty()) {
          startTimeString = "09:00";
        }
        String endDateString = fields[3];
        if (endDateString.isEmpty()) {
          endDateString = startDateString;
        }
        String endTimeString = fields[4];
        if (endTimeString.isEmpty()) {
          endTimeString = "10:00";
        }
        String allDay = fields[5];
        if (allDay.isEmpty()) {
          allDay = "false";
        }
        String description = fields[6];
        String location = fields[7];
        String isPrivateString = fields[8];
        if (isPrivateString.isEmpty()) {
          isPrivateString = "false";
        }
        LocalDate startDate = handleParseDate(startDateString, "Start Date");
        LocalTime startTime = handleParseTime(startTimeString, "Start Time");
        LocalDate endDate = handleParseDate(endDateString, "End Date");
        LocalTime endTime = handleParseTime(endTimeString, "End Time");
        Boolean isAllDay = handleParseBoolean(allDay, "All Day Event");
        Boolean isPrivate = handleParseBoolean(isPrivateString, "Private");
        calendarModel.getActiveCalendar().createSingle(
            subject, 
            LocalDateTime.of(startDate, startTime), 
            LocalDateTime.of(endDate, endTime), 
            isAllDay, 
            description, 
            location, 
            isPrivate
        );
      }
      scan.close();
    }

    @Override
    public void createSingle(
        IViewEvent viewEvent
    ) throws IOException {
      validateSubject(viewEvent.getSubject());
      LocalDate startDate = handleParseDate(viewEvent.getStartDateString(), "Start Date");
      LocalTime startTime = handleParseTime(viewEvent.getStartTimeString(), "Start Time");
      LocalDate endDate = handleParseDate(viewEvent.getEndDateString(), "End Date");
      LocalTime endTime = handleParseTime(viewEvent.getEndTimeString(), "End Time");
      LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
      LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
      if (endDateTime.isBefore(startDateTime)) {
        throw new IOException("End date must be after start date");
      }
      calendarModel.getActiveCalendar().createSingle(
          viewEvent.getSubject(), 
          startDateTime, 
          endDateTime, 
          viewEvent.isAllDayEvent(), 
          viewEvent.getDescription(), 
          viewEvent.getLocation(), 
          viewEvent.isPrivate()
      );
    }

    @Override
    public void createRecurring(
        IViewRecurringEvent viewEvent
    ) throws IOException {
      validateSubject(viewEvent.getSubject());
      LocalDate startDate = handleParseDate(viewEvent.getStartDateString(), "Start Date");
      LocalTime startTime = handleParseTime(viewEvent.getStartTimeString(), "Start Time");
      LocalDate endDate = handleParseDate(viewEvent.getEndDateString(), "End Date");
      LocalTime endTime = handleParseTime(viewEvent.getEndTimeString(), "End Time");
      LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
      LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
      if (endDateTime.isBefore(startDateTime)) {
        throw new IOException("End date must be after start date");
      }
      List<DayOfWeek> weekdays = Create.getWeekDays(viewEvent.getWeekdaysString());
      if (viewEvent instanceof ViewRecurringUntilEvent) {
        ViewRecurringUntilEvent casted = (ViewRecurringUntilEvent) viewEvent;
        LocalDate onDate = handleParseDate(casted.getOnDateString(), "On Date");
        LocalTime onTime = handleParseTime(casted.getOnTimeString(), "On Time");
        LocalDateTime onDateTime = LocalDateTime.of(onDate, onTime);
        if (onDateTime.isBefore(startDateTime)) {
          throw new IOException("On date must be after start date");
        }
        calendarModel.getActiveCalendar().createRecurringUntil(
            viewEvent.getSubject(), 
            startDateTime, 
            endDateTime, 
            viewEvent.isAllDayEvent(), 
            viewEvent.getDescription(), 
            viewEvent.getLocation(), 
            viewEvent.isPrivate(),
            weekdays,
            onDateTime,
            UUID.randomUUID()
        );
      }
      else {
        ViewRecurringSequenceEvent casted = (ViewRecurringSequenceEvent) viewEvent;
        Integer sequenceSize = handleParseInteger(casted.getSequenceSizeString(), "After");
        calendarModel.getActiveCalendar().createRecurringSequence(
            viewEvent.getSubject(), 
            startDateTime, 
            endDateTime, 
            viewEvent.isAllDayEvent(), 
            viewEvent.getDescription(), 
            viewEvent.getLocation(), 
            viewEvent.isPrivate(),
            weekdays,
            sequenceSize,
            UUID.randomUUID()
        );
      }
    }

    @Override
    public void editSingle(String idString, IViewEvent viewEvent) throws IOException {
      validateSubject(viewEvent.getSubject());
      LocalDate startDate = handleParseDate(viewEvent.getStartDateString(), "Start Date");
      LocalTime startTime = handleParseTime(viewEvent.getStartTimeString(), "Start Time");
      LocalDate endDate = handleParseDate(viewEvent.getEndDateString(), "End Date");
      LocalTime endTime = handleParseTime(viewEvent.getEndTimeString(), "End Time");
      LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
      LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
      UUID id = UUID.fromString(idString);
      calendarModel.getActiveCalendar().editSingleById(
          id, 
          viewEvent.getSubject(),
          startDateTime,
          endDateTime,
          viewEvent.isAllDayEvent(),
          viewEvent.getDescription(),
          viewEvent.getLocation(),
          viewEvent.isPrivate()
      );
    }

    @Override
    public void editRecurring(
        String idString, 
        IViewRecurringEvent viewEvent,
        EditType editType) throws IOException {
      LocalDate startDate = handleParseDate(viewEvent.getStartDateString(), "Start Date");
      LocalTime startTime = handleParseTime(viewEvent.getStartTimeString(), "Start Time");
      LocalDate endDate = handleParseDate(viewEvent.getEndDateString(), "End Date");
      LocalTime endTime = handleParseTime(viewEvent.getEndTimeString(), "End Time");
      LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
      LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
      validateSubject(viewEvent.getSubject());
      if (endDateTime.isBefore(startDateTime)) {
        throw new IOException("End date must be after start date");
      }
      UUID id = UUID.fromString(idString);
      List<DayOfWeek> weekdays = Create.getWeekDays(viewEvent.getWeekdaysString());
      if (viewEvent instanceof ViewRecurringUntilEvent) {
        ViewRecurringUntilEvent casted = (ViewRecurringUntilEvent) viewEvent;
        LocalDate onDate = handleParseDate(casted.getOnDateString(), "On Date");
        LocalTime onTime = handleParseTime(casted.getOnTimeString(), "On Time");
        LocalDateTime onDateTime = LocalDateTime.of(onDate, onTime);
        if (onDateTime.isBefore(startDateTime)) {
          throw new IOException("On date must be after start date");
        }
        switch (editType) {
          case SINGLE:
            calendarModel.getActiveCalendar().editSingleRecurringUntilById(
                id,
                viewEvent.getSubject(),
                startDateTime,
                endDateTime,
                viewEvent.isAllDayEvent(),
                viewEvent.getDescription(),
                viewEvent.getLocation(),
                viewEvent.isPrivate(),
                weekdays,
                onDateTime
            );
            break;
          case THISANDFOLLOWING:
            calendarModel.getActiveCalendar().editThisAndFollowingUntilById(
                id,
                viewEvent.getSubject(),
                startDateTime,
                endDateTime,
                viewEvent.isAllDayEvent(),
                viewEvent.getDescription(),
                viewEvent.getLocation(),
                viewEvent.isPrivate(),
                weekdays,
                onDateTime
            );
            break;
          case ALL:
            calendarModel.getActiveCalendar().editAllUntilById(
                id,
                viewEvent.getSubject(),
                startDateTime,
                endDateTime,
                viewEvent.isAllDayEvent(),
                viewEvent.getDescription(),
                viewEvent.getLocation(),
                viewEvent.isPrivate(),
                weekdays,
                onDateTime
            );
            break;
          default:
            break;
        }
      }
      else {
        ViewRecurringSequenceEvent casted = (ViewRecurringSequenceEvent) viewEvent;
        Integer sequenceSize = handleParseInteger(casted.getSequenceSizeString(), "After");
        switch (editType) {
          case SINGLE:
            calendarModel.getActiveCalendar().editSingleRecurringSequenceById(
                id,
                viewEvent.getSubject(),
                startDateTime,
                endDateTime,
                viewEvent.isAllDayEvent(),
                viewEvent.getDescription(),
                viewEvent.getLocation(),
                viewEvent.isPrivate(),
                weekdays, 
                sequenceSize
            );
            break;
          case THISANDFOLLOWING:
            calendarModel.getActiveCalendar().editThisAndFollowingSequenceById(
                id,
                viewEvent.getSubject(),
                startDateTime,
                endDateTime,
                viewEvent.isAllDayEvent(),
                viewEvent.getDescription(),
                viewEvent.getLocation(),
                viewEvent.isPrivate(),
                weekdays, 
                sequenceSize
            );
            break;
          case ALL:
            calendarModel.getActiveCalendar().editAllSequenceById(
                id,
                viewEvent.getSubject(),
                startDateTime,
                endDateTime,
                viewEvent.isAllDayEvent(),
                viewEvent.getDescription(),
                viewEvent.getLocation(),
                viewEvent.isPrivate(),
                weekdays, 
                sequenceSize
            );
            break;
          default:
            break;
        }
      }
    }

    @Override
    public void queryAnalytics(LocalDate startDate, LocalDate endDate) throws IOException {
      AnalyticsVisitor visitor = new AnalyticsVisitor(startDate, endDate);
      calendarModel.getActiveCalendar().accept(visitor);

      view.setAnalyticsData(
          visitor.getTotalCount(),
          visitor.getDaysCount(),
          visitor.getSubjectCountMap(),
          visitor.getAverageEventsPerDay(),
          visitor.getMostBusyByEvents(),
          visitor.getLeastBusyByEvents(),
          visitor.getMostBusyByDuration(),
          visitor.getLeastBusyByDuration(),
          visitor.getOnlinePercentage(),
          visitor.getOfflinePercentage()
      );
    }

    private List<IViewEvent> createViewEvents(List<IEvent> events) {
      List<IViewEvent> viewEvents = new ArrayList<>(0);
      for (IEvent event: events) {
        if (event instanceof RecurringUntilEvent) {
          IRecurringUntilEvent casted = (RecurringUntilEvent) event;
          IViewRecurringUntilEvent viewEvent = new ViewRecurringUntilEvent();
          viewEvent.setWeekdaysString(getWeekDaysString(casted.getWeekdays()));
          viewEvent.setRecurringSequenceIdString(casted.getRecurringSequenceId().toString());
          viewEvent.setOnDateString(casted.getUntilDatetime().toLocalDate().toString());
          viewEvent.setOnTimeString(casted.getUntilDatetime().toLocalTime().toString());
          viewEvent.setId(event.getId().toString());
          viewEvent.setSubject(event.getSubject());
          viewEvent.setDescription(event.getDescription());
          viewEvent.setLocation(event.getLocation());
          viewEvent.setIsAllDayEvent(event.isAllDayEvent());
          viewEvent.setIsPrivate(event.isPrivate());
          viewEvent.setStartDateString(event.getStartDate().toString());
          viewEvent.setStartTimeString(event.getStartTime().toString());
          viewEvent.setEndDateString(event.getEndDate().toString());
          viewEvent.setEndTimeString(event.getEndTime().toString());
          viewEvents.add(viewEvent);
        }
        else if (event instanceof RecurringSequenceEvent) {
          IRecurringSequenceEvent casted = (RecurringSequenceEvent) event;
          IViewRecurringSequenceEvent viewEvent = new ViewRecurringSequenceEvent();
          viewEvent.setWeekdaysString(getWeekDaysString(casted.getWeekdays()));
          viewEvent.setRecurringSequenceIdString(casted.getRecurringSequenceId().toString());
          viewEvent.setSequenceSizeString(Integer.toString(casted.getSequenceSize()));
          viewEvent.setId(event.getId().toString());
          viewEvent.setSubject(event.getSubject());
          viewEvent.setDescription(event.getDescription());
          viewEvent.setLocation(event.getLocation());
          viewEvent.setIsAllDayEvent(event.isAllDayEvent());
          viewEvent.setIsPrivate(event.isPrivate());
          viewEvent.setStartDateString(event.getStartDate().toString());
          viewEvent.setStartTimeString(event.getStartTime().toString());
          viewEvent.setEndDateString(event.getEndDate().toString());
          viewEvent.setEndTimeString(event.getEndTime().toString());
          viewEvents.add(viewEvent);
        }
        else {
          IViewEvent viewEvent = new ViewEvent();
          viewEvent.setId(event.getId().toString());
          viewEvent.setSubject(event.getSubject());
          viewEvent.setDescription(event.getDescription());
          viewEvent.setLocation(event.getLocation());
          viewEvent.setIsAllDayEvent(event.isAllDayEvent());
          viewEvent.setIsPrivate(event.isPrivate());
          viewEvent.setStartDateString(event.getStartDate().toString());
          viewEvent.setStartTimeString(event.getStartTime().toString());
          viewEvent.setEndDateString(event.getEndDate().toString());
          viewEvent.setEndTimeString(event.getEndTime().toString());
          viewEvents.add(viewEvent);
        }
      }
      return viewEvents;
    }

    private String getWeekDaysString(List<DayOfWeek> weekdays) {
      String result = "";
      for (DayOfWeek day: weekdays) {
        switch (day) {
          case MONDAY:
            result += "M";
            break;
          case TUESDAY:
            result += "T";
            break;
          case WEDNESDAY:
            result += "W";
            break;
          case THURSDAY:
            result += "R";
            break;
          case FRIDAY:
            result += "F";
            break;
          case SATURDAY:
            result += "S";
            break;
          case SUNDAY:
            result += "U";
            break;
          default:
            break;
        }

      }
      return result;
    }

    private LocalDate handleParseDate(String dateString, String field) throws IOException {
      try {
        LocalDate date = LocalDate.parse(dateString);
        return date;
      }
      catch (Exception ex) {
        throw new IOException(
            "Unable to parse date for " + field + " field: " + dateString
            + "\nFormat should be (yyyy-MM-dd)");
      }
    }

    private LocalTime handleParseTime(String timeString, String field) throws IOException {
      try {
        LocalTime time = LocalTime.parse(timeString);
        return time;
      }
      catch (Exception ex) {
        throw new IOException(
            "Unable to parse time for " + field + " field: " + timeString
            + "\nFormat should be (hh:mm)");
      }
    }

    private Boolean handleParseBoolean(String boolString, String field) throws IOException {
      try {
        Boolean result = Boolean.parseBoolean(boolString);
        return result;
      }
      catch (Exception ex) {
        throw new IOException(
            "Unable to parse value for " + field + " field: " + boolString
            + "\nFormat should be true | false");
      }
    }

    private Integer handleParseInteger(String value, String field) throws IOException {
      try {
        Integer result = Integer.parseInt(value);
        return result;
      }
      catch (Exception ex) {
        throw new IOException(
          "Unable to parse value for " + field + " field: " + value
          + "\nFormat should be true | false");
      }
    }

    private void validateSubject(String subject) throws IOException {
      if (subject.isEmpty()) {
        throw new IOException("Subject cannot be null");
      }
    }
    
  }

}
