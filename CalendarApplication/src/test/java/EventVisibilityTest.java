
import org.junit.Test;

import calendarapp.model.EventVisibility;

import static org.junit.Assert.*;

/**
 * Test class for Enum {@link EventVisibility}.
 */
public class EventVisibilityTest {

  @Test
  public void testPublicVisibility() {
    EventVisibility visibility = EventVisibility.getVisibility("public");
    assertEquals(EventVisibility.PUBLIC, visibility);
  }

  @Test
  public void testPrivateVisibility() {
    EventVisibility visibility = EventVisibility.getVisibility("private");
    assertEquals(EventVisibility.PRIVATE, visibility);
  }

  @Test
  public void testDefaultVisibility() {
    EventVisibility visibility = EventVisibility.getVisibility("default");
    assertEquals(EventVisibility.DEFAULT, visibility);
  }

  @Test
  public void testUnknownVisibility() {
    EventVisibility visibility = EventVisibility.getVisibility("unknownVisibility");
    assertEquals(EventVisibility.UNKNOWN, visibility);
  }

  @Test
  public void testCaseInsensitiveVisibility() {
    EventVisibility visibility = EventVisibility.getVisibility("PUBLIC");
    assertEquals(EventVisibility.PUBLIC, visibility);

    visibility = EventVisibility.getVisibility("PrIvAtE");
    assertEquals(EventVisibility.PRIVATE, visibility);

    visibility = EventVisibility.getVisibility("DeFaUlT");
    assertEquals(EventVisibility.DEFAULT, visibility);
  }

  @Test
  public void testEmptyVisibility() {
    EventVisibility visibility = EventVisibility.getVisibility("");
    assertEquals(EventVisibility.UNKNOWN, visibility);
  }

  @Test
  public void testNullVisibility() {
    EventVisibility visibility = EventVisibility.getVisibility(null);
    assertEquals(EventVisibility.UNKNOWN, visibility);
  }

  @Test
  public void testGetValue() {
    // Verify that getValue() returns the correct string representation for each enum constant.
    assertEquals("public", EventVisibility.PUBLIC.getValue());
    assertEquals("private", EventVisibility.PRIVATE.getValue());
    assertEquals("default", EventVisibility.DEFAULT.getValue());
    assertEquals("unknown", EventVisibility.UNKNOWN.getValue());
  }
}
