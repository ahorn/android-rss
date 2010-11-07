package org.mcsoxford.rss;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class RSSParserTest {

  /**
   * Class under test
   */
  private RSSParser parser;

  /**
   * Fixture data
   */
  private InputStream rssfeed;

  @Before
  public void setup() throws FileNotFoundException {
    rssfeed = getClass().getClassLoader().getResourceAsStream("rssfeed.xml");
    assertNotNull(rssfeed);

    parser = new RSSParser();
  }

  @Test
  public void parse() throws Exception {

    final RSSFeed feed = parser.parse(rssfeed);
    assertEquals("Example Channel", feed.getTitle());
    assertEquals(new URI("http://example.com/"), feed.getLink());
    assertEquals("My example channel", feed.getDescription());

    final Iterator<RSSItem> items = feed.getItems().iterator();
    RSSItem item;

    item = items.next();
    assertEquals("News for November", item.getTitle());
    assertEquals(new URI("http://example.com/2010/11/07"), item.getLink());
    assertEquals("Other things happened today", item.getDescription());
    assertEquals("Sun, 07 Nov 2010 08:22:14 GMT", item.getPubDate());

    item = items.next();
    assertEquals("News for October", item.getTitle());
    assertEquals(new URI("http://example.com/2010/10/12"), item.getLink());
    assertEquals("October days", item.getDescription());
    assertEquals("Daily news", item.getCategory());

    assertFalse(items.hasNext());
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseNullArgument() {
    parser.parse(null);
  }
}
