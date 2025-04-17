package model;

public interface ICalendarVisitor<R> {
  R visitCalendarModel(CalendarModel calendar);
}
