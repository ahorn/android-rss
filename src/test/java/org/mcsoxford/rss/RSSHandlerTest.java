package org.mcsoxford.rss;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
    handler = new RSSHandler(new RSSConfig());
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
  public void isBufferingContent() {
    assertFalse(handler.isBuffering());
    handler.startElement(null, null, "content:encoded", null);
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

  @Test
  public void isBufferingThumbnail() {
    // setup
    isBufferingItem();

    // add required url attribute to media:thumbnail element
    final org.xml.sax.helpers.AttributesImpl attributes = new org.xml.sax.helpers.AttributesImpl();
    attributes.addAttribute(null, null, "url", null, "http://example.com/thumbnails/1.jpg");

    handler.startElement(null, null, "media:thumbnail", attributes);
    assertFalse(handler.isBuffering());
  }

  @Test
  public void parseChannelWithThumbnail() {
    // no exception
    handler.startElement(null, null, "media:thumbnail", null);
  }

  @Test
  public void parseThumbnailWithoutUrl() {
    // no exception
    handler.startElement(null, null, "media:thumbnail", new org.xml.sax.helpers.AttributesImpl());
  }

  @Test
  public void channelTitle() {
    assertNull(handler.feed().getTitle());
    handler.startElement(null, null, "title", null);
    handler.characters(new char[] { 'a', 'b', 'c' }, 0, 3);
    handler.endElement(null, null, "title");
    assertEquals("abc", handler.feed().getTitle());
  }

  @Test
  public void itemTitle() {
    assertNull(handler.feed().getTitle());
    assertFalse(handler.feed().getItems().iterator().hasNext());
    handler.startElement(null, null, "item", null);
    handler.startElement(null, null, "title", null);
    handler.characters(new char[] { 'a', 'b', 'c' }, 0, 3);
    handler.endElement(null, null, "title");
    handler.endElement(null, null, "item");

    final Iterator<RSSItem> items = handler.feed().getItems().iterator();
    assertTrue(items.hasNext());
    assertNull(handler.feed().getTitle());
    assertEquals("abc", items.next().getTitle());
    assertFalse(items.hasNext());
  }

  @Test
  public void items() {
    assertFalse(handler.feed().getItems().iterator().hasNext());

    final char[][] titles = { { 'a', 'b', 'c' }, { '1', '2', '3' } };
    for (char[] title : titles) {
      handler.startElement(null, null, "item", null);
      handler.startElement(null, null, "title", null);
      handler.characters(title, 0, 3);
      handler.endElement(null, null, "title");
      handler.endElement(null, null, "item");
    }

    final Iterator<RSSItem> items = handler.feed().getItems().iterator();
    assertTrue(items.hasNext());
    assertEquals("abc", items.next().getTitle());
    assertEquals("123", items.next().getTitle());
    assertFalse(items.hasNext());
  }
}
