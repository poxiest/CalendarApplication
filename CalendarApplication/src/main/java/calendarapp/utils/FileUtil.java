package calendarapp.utils;

public class FileUtil {
  /**
   * Extracts the file extension from a given file path.
   *
   * @param filePath The full file path or filename.
   * @return The extracted file extension in lowercase.
   * @throws IllegalArgumentException If no file extension is found.
   */
  public static String getFileExtension(String filePath) {
    String[] filenameSplit = filePath.split("\\.");
    if (filenameSplit.length <= 1) {
      throw new IllegalArgumentException("No extension found for file: " + filePath);
    }
    return filenameSplit[filenameSplit.length - 1];
  }
}
