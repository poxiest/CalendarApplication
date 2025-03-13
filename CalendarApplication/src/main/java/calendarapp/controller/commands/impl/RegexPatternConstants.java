package calendarapp.controller.commands.impl;

public class RegexPatternConstants {

  /*
  Create command regex pattern constants.
   */
  public static final String CREATE_AUTO_DECLINE_PATTERN = "--autoDecline";
  public static final String CREATE_EVENT_NAME_PATTERN = "(?i)\\s+event\\s+(?:\"([^\"]+)\"|(\\S+))\\s+(on|from)";
  public static final String CREATE_FROM_TO_PATTERN = "(?i)\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String CREATE_ON_PATTERN = "(?i)\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String CREATE_REPEATS_F0R_PATTERN = "(?i)\\s+repeats\\s+(?:\"([^\"]+)\"|(\\S+))\\s+for\\s+(\\d+)\\s+times";
  public static final String CREATE_REPEATS_UNTIL_PATTERN = "(?i)\\s+repeats\\s+(?:\"([^\"]+)\"|(\\S+))\\s+until\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String CREATE_OPTIONAL_PARAMETERS = "(?i)(description|location|visibility)\\s+(?:\"([^\"]+)\"|(\\S+))";

  /*
  Edit command regex pattern constants.
   */
  public static final String EDIT_FROM_TO_PATTERN = "(?i)\\s+event\\s+(\\S+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))\\s+with\\s+(?:\"([^\"]+)\"|(\\S+))$";
  public static final String EDIT_FROM_PATTERN = "(?i)\\s+events\\s+(\\S+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+with\\s+(?:\"([^\"]+)\"|(\\S+))$";
  public static final String EDIT_EVENT_NAME_PATTERN = "(?i)\\s+events\\s+(\\S+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Export command regex pattern constants.
   */
  public static final String EXPORT_FILENAME_PATTERN = "(?i)\\s+cal\\s+(?:\"([^\"]+)\"|(\\S+))$";

  /*
  Print command regex pattern constants.
   */
  public static final String PRINT_FROM_TO_PATTERN = "(?i)\\s+events\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String PRINT_ON_PATTERN = "(?i)\\s+events\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))";

  /*
  Show command regex pattern constants.
   */
  public static final String STATUS_ON_PATTERN = "(?i)\\s+status\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))$";
}
