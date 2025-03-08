package calendarapp.controller.commands.impl;

public class RegexPatternConstants {
  public static final String autoDeclinePattern = "--autoDecline";
  public static final String eventNamePattern = "(?i)\\s+event\\s+(?:\"([^\"]+)\"|(\\S+))\\s+(on|from)";
  public static final String dateFromToPattern = "(?i)\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String dateOnPattern = "(?i)\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String repeatsForPattern = "(?i)\\s+repeats\\s+(?:\"([^\"]+)\"|(\\S+))\\s+for\\s+(\\d+)\\s+times";
  public static final String repeatsUntilPattern = "(?i)\\s+repeats\\s+(?:\"([^\"]+)\"|(\\S+))\\s+until\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String optionalParameters = "(?i)(description|location|scope)\\s+(?:\"([^\"]+)\"|(\\S+))";

  public static final String fromToPattern = "(?i)\\s+event\\s+(\\S+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))\\s+with\\s+(?:\"([^\"]+)\"|(\\S+))$";
  public static final String fromPattern = "(?i)\\s+events\\s+(\\S+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+with\\s+(?:\"([^\"]+)\"|(\\S+))$";
  public static final String editEventNamePattern = "(?i)\\s+events\\s+(\\S+)\\s+(?:\"([^\"]+)\"|(\\S+))\\s+(?:\"([^\"]+)\"|(\\S+))$";

  public static final String filenamePatter = "(?i)\\s+cal\\s+(?:\"([^\"]+)\"|(\\S+))$";

  public static final String printFromToPattern = "(?i)\\s+events\\s+from\\s+(?:\"([^\"]+)\"|(\\S+))\\s+to\\s+(?:\"([^\"]+)\"|(\\S+))";
  public static final String printOnPattern = "(?i)\\s+events\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))";

  public static final String statusPattern = "(?i)\\s+status\\s+on\\s+(?:\"([^\"]+)\"|(\\S+))$";

}
