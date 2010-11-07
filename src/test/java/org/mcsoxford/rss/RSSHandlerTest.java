package org.mcsoxford.rss;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Unit test for {@link RSSHandler} SAX handler.
 * 
 * @author Mr Horn
 */
public class RSSHandlerTest {

  /**
   * Class under test
   */
  private RSSHandler handler;

  @Before
  public void setup() {
    handler = new RSSHandler();
  }

  @Test
  public void isBufferingChannel() {
    assertFalse(handler.isBuffering());
    handler.startElement(null, null, "channel", null);
    assertFalse(handler.isBuffering());
  }

  @Test
  public void isBufferingItem() {
    assertFalse(handler.isBuffering());
    handler.startElement(null, null, "item", null);
    assertFalse(handler.isBuffering());
  }

  @Test
  public void isBufferingTitle() {
    assertFalse(handler.isBuffering());
    handler.startElement(null, null, "title", null);
    assertTrue(handler.isBuffering());
  }

  @Test
  public void isBufferingDescription() {
    assertFalse(handler.isBuffering());
    handler.startElement(null, null, "description", null);
    assertTrue(handler.isBuffering());
  }

  @Test
  public void isBufferingCategory() {
    assertFalse(handler.isBuffering());
    handler.startElement(null, null, "category", null);
    assertTrue(handler.isBuffering());
  }

  @Test
  public void isBufferingLink() {
    assertFalse(handler.isBuffering());
    handler.startElement(null, null, "link", null);
    assertTrue(handler.isBuffering());
  }

  @Test
  public void isBufferingPubDate() {
    assertFalse(handler.isBuffering());
    handler.startElement(null, null, "pubDate", null);
    assertTrue(handler.isBuffering());
  }

}
