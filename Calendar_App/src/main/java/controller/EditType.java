package controller;

/**
 * An enum with values representing the possible types of edits
 * for recurring events. Edits can either be single, updating exclusively
 * the event selected, this and following, updating all events after and
 * including the event selected in corresponding series, and all, updating
 * all events in the series.
 */
public enum EditType {
  SINGLE,
  THISANDFOLLOWING,
  ALL
}
