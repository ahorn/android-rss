package org.mcsoxford.rss;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for SAX handler which parses RSS feeds.
 * 
 * @author Mr Horn
 */
public class RSSParserTest {

  /**
   * Class under test
   */
  private RSSParser parser;

  /**
   * Fixture data
   */
  private InputStream stream;

  @Before
  public void setup() throws FileNotFoundException {
    stream = getClass().getClassLoader().getResourceAsStream("rssfeed.xml");
    assertNotNull(stream);

    parser = new RSSParser();
  }

  @Test
  public void parse() throws Exception {
    final RSSFeed feed = parse(stream);
    assertEquals("Example Channel", feed.getTitle());
    assertEquals(new URI("http://example.com/"), feed.getLink());
    assertEquals("My example channel", feed.getDescription());

    final Iterator<RSSItem> items = feed.getItems().iterator();
    RSSItem item;

    item = items.next();
    assertEquals("News for November", item.getTitle());
    assertEquals(new URI("http://example.com/2010/11/07"), item.getLink());
    assertEquals("Other things happened today", item.getDescription());
    Date expectedDate = new GregorianCalendar(2010, 10, 07, 8, 22, 14).getTime();
    assertEquals(expectedDate, item.getPubDate());

    item = items.next();
    assertEquals("News for October", item.getTitle());
    assertEquals(new URI("http://example.com/2010/10/12"), item.getLink());
    assertEquals("October days", item.getDescription());
    assertEquals("Daily news", item.getCategory());

    assertFalse(items.hasNext());
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseStreamNullArgument() throws Exception {
    parse(null);
  }

  /**
   * Helper method to parse an RSS feed and closes the input stream.
   */
  private RSSFeed parse(InputStream stream) {
    try {
      return parser.parse(stream);
    } finally {
      Resources.closeQuietly(stream);
    }
  }
}
