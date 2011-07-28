package org.mcsoxford.rss;

import java.io.FileNotFoundException;
import java.io.InputStream;
import android.net.Uri;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

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

    parser = new RSSParser(new RSSConfig());
  }

  @Test
  public void parse() throws Exception {
    final RSSFeed feed = parse(stream);
    assertEquals("Example Channel", feed.getTitle());
    assertEquals(Uri.parse("http://example.com/"), feed.getLink());
    assertEquals("My example channel", feed.getDescription());

    final Iterator<RSSItem> items = feed.getItems().iterator();
    RSSItem item;

    item = items.next();
    assertEquals("News for November", item.getTitle());
    assertEquals(Uri.parse("http://example.com/2010/11/07"), item.getLink());
    assertEquals("Other things happened today", item.getDescription());
    assertTrue(item.getCategories().isEmpty());
    GregorianCalendar calendar = new GregorianCalendar(2010, 10, 07, 8, 22, 14);
    calendar.setTimeZone(TimeZone.getTimeZone("Etc/GMT"));
    Date expectedDate = calendar.getTime();
    assertEquals(expectedDate, item.getPubDate());

    assertEquals(2, item.getThumbnails().size());

    MediaThumbnail expectedThumbnail0 = new MediaThumbnail(
        Uri.parse("http://example.com/media/images/12/jpg/_7_2.jpg"), 49, 66);
    assertEquals(expectedThumbnail0.getUrl(),
        item.getThumbnails().get(0).getUrl());
    assertEquals(expectedThumbnail0.getHeight(),
        item.getThumbnails().get(0).getHeight());
    assertEquals(expectedThumbnail0.getWidth(),
        item.getThumbnails().get(0).getWidth());

    MediaThumbnail expectedThumbnail1 = new MediaThumbnail(
        Uri.parse("http://example.com/media/images/12/jpg/_7_2-1.jpg"), 81, 144);
    assertEquals(expectedThumbnail1.getUrl(),
        item.getThumbnails().get(1).getUrl());
    assertEquals(expectedThumbnail1.getHeight(),
        item.getThumbnails().get(1).getHeight());
    assertEquals(expectedThumbnail1.getWidth(),
        item.getThumbnails().get(1).getWidth());

    item = items.next();
    assertEquals("News for October", item.getTitle());
    assertEquals(Uri.parse("http://example.com/2010/10/12"), item.getLink());
    assertEquals("October days", item.getDescription());
    assertEquals(2, item.getCategories().size());
    assertTrue(item.getCategories().contains("Daily news"));
    assertTrue(item.getCategories().contains("October news"));
    assertEquals(0, item.getThumbnails().size());

    assertFalse(items.hasNext());
  }

  @Test
  public void parseContentTest() {
    stream = getClass().getClassLoader().getResourceAsStream(
        "wordpress_feed.xml");
    final RSSFeed feed = parse(stream);

    final Iterator<RSSItem> items = feed.getItems().iterator();
    RSSItem item;

    item = items.next();

    assertEquals("WordPress 3.2.1", item.getTitle());
    assertEquals(
        Uri.parse("http://wordpress.org/news/2011/07/wordpress-3-2-1/"),
        item.getLink());
    assertEquals("test: we&#8217;re, <b>apple</b>", item.getDescription());
    GregorianCalendar calendar = new GregorianCalendar(2011, 6, 12, 19, 49, 0);
    calendar.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
    Date expectedDate = calendar.getTime();

    assertEquals(expectedDate, item.getPubDate());
    assertEquals("test: we&#8217;re, <b>apple</b>", item.getDescription());
    assertEquals("<b>apple</b>", item.getContent());

    item = items.next();

    assertEquals("WordPress 3.2 now available", item.getTitle());
    assertEquals(Uri.parse("http://wordpress.org/news/2011/07/gershwin/"),
        item.getLink());
    assertEquals("test desc: ##<b>hash</b>", item.getDescription());
    calendar = new GregorianCalendar(2011, 6, 4, 21, 7, 0);
    calendar.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
    expectedDate = calendar.getTime();

    assertEquals(expectedDate, item.getPubDate());
    assertEquals("<b>apple</b>", item.getContent());

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
