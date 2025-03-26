package calendarapp.model.impl.searchstrategies;

public enum SearchType {
  OVERLAPPING,
  MATCHING,
  EXACT;

  public static SearchType fromString(String searchType) {
    for (SearchType type : SearchType.values()) {
      if (type.name().equalsIgnoreCase(searchType)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid search type: " + searchType);
  }
}
