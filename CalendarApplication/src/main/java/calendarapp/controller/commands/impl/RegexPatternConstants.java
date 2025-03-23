package calendarapp.controller.commands.impl;

/**
 * This class contains the regex patterns used to validate commands.
 */
public class RegexPatternConstants {

  /*
  Create command regex pattern constants.
   */
  public static final String EVENT = "(?i)create\\s+event";
  public static final String CALENDAR = "(?i)create\\s+calendar";
  public static final String CREATE_AUTO_DECLINE_PATTERN = "--autoDecline";
  public static final String CREATE_EVENT_NAME_PATTERN =
      "(?i)\\s+event\\s+(?:\"([^\"]+)\"|(\\S+))\\s+(on|from)";
  public static final String CREATE_FROM_TO_PATTERN =
      "(?i)\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String CREATE_ON_PATTERN = "(?i)\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String CREATE_REPEATS_F0R_PATTERN =
      "(?i)\\s+repeats\\s+(?:\"([^\"]+)\"|(\\S+))\\s+for\\s+(\\d+)\\s+times";
  public static final String CREATE_REPEATS_UNTIL_PATTERN =
      "(?i)\\s+repeats\\s+(?:\"([^\"]+)\"|(\\S+))\\s+until\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String CREATE_OPTIONAL_PARAMETERS =
      "(?i)(description|location|visibility)\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String IS_RECURRING_EVENT = "repeats";
  public static final String CREATE_NEW_CALENDAR = "(?i)calendar\\s+--name\\s+(?:\"([^\"]+)\"|"
      + "(\\S+))\\s+--timezone\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Edit command regex pattern constants.
   */
  public static final String EDIT_FROM_TO_PATTERN =
      "(?i)\\s+event\\s+([a-zA-Z_]+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+from\\s+(?:\"([^\"]+)\"" +
          "|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))\\s+with\\s+(?:\"([^\"]+)\"|(\\S+))$";
  public static final String EDIT_FROM_PATTERN =
      "(?i)\\s+events\\s+([a-zA-Z_]+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+from\\s+(?:\"([^\"]+)\"" +
          "|(\\S+))\\s+with\\s+(?:\"([^\"]+)\"|(\\S+))$";
  public static final String EDIT_EVENT_NAME_PATTERN =
      "(?i)\\s+events\\s+([a-zA-Z_]+)\\s+(?:\"([^\"]+)\"|([^\\s\"]+))\\s+(?:\"([^\"]+)\"" +
          "|([^\\s\"]+))$";
  public static final String IS_RECURRING_EVENTS = "edit events";
  public static final String EDIT_CALENDAR_PATTERN = "(?i)\\s+--name\\s+(?:\"([^\"]+)\"|(\\S+))"
      + "\\s+--property\\s+([a-zA-Z_]+)\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Export command regex pattern constants.
   */
  public static final String EXPORT_FILENAME_PATTERN = "(?i)\\s+cal\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Print command regex pattern constants.
   */
  public static final String PRINT_FROM_TO_PATTERN =
      "(?i)\\s+events\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String PRINT_ON_PATTERN = "(?i)\\s+events\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))";

  /*
  Show command regex pattern constants.
   */
  public static final String STATUS_ON_PATTERN = "(?i)\\s+status\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Use calendar command regex pattern constant.
   */
  public static final String USE_COMMAND = "(?i)\\s+calendar\\s+--name\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Copy command regex pattern constants.
   */
  public static final String COPY_EVENT_COMMAND = "(?i)\\s+event\\s+(?:\"([^\"]+)\"|(\\S+))"
      + "\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))\\s+--target\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\""
      + "([^\"]+)\"|(\\S+))$";
  public static final String COPY_EVENTS_ON_COMMAND = "(?i)\\s+events\\s+on\\s+(?:\"([^\"]+)\"|"
      + "(\\S+))\\s+--target\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))$";
  public static final String COPY_EVENTS_BETWEEN_COMMAND = "(?i)\\s+events\\s+between\\s+(?:\"([^\"]+)\"|(\\S+))\\s+and\\s+(?:\"([^\"]+)\"|(\\S+))\\s+--target\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))$";
}
