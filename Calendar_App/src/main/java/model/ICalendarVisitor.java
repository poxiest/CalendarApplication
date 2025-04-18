package model;

/**
 * A visitor interface for visiting calendar models.
 *
 * @param <R> the return type of the visitor operation
 */
public interface ICalendarVisitor<R> {
  /**
   * Visit a {@link CalendarModel} and return a result of type R.
   *
   * @param calendar the calendar model being visited
   * @return the result of the visit
   */
  R visitCalendarModel(CalendarModel calendar);
}
