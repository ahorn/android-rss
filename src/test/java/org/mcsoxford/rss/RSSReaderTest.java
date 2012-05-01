package org.mcsoxford.rss;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Integration test of HTTP client which retrieves RSS feeds.
 * 
 * @author Mr Horn
 */
public class RSSReaderTest {

  /**
   * URI of the "BBC News - World" RSS feed
   */
  private final static String BBC_NEWS = "http://feeds.bbci.co.uk/news/world/rss.xml";

  /**
   * Class under test
   */
  private RSSReader reader;

  @Before
  public void setup() {
    reader = new RSSReader();
  }

  @After
  public void teardown() {
    reader.close();
  }

  @Test
  public void get() throws RSSReaderException {
    final RSSFeed feed = reader.load(BBC_NEWS);

    assertEquals("BBC News - World", feed.getTitle());

    assertEquals(
        android.net.Uri.parse("http://www.bbc.co.uk/news/world/#sa-ns_mchannel=rss&ns_source=PublicRSS20-sa"),
        feed.getLink());

    assertEquals(
        "The latest stories from the World section of the BBC News web site.",
        feed.getDescription());
  }

  @Test
  public void notFound() {
    try {
      reader.load("http://www.google.co.uk/not/found");
    } catch (RSSReaderException e) {
      assertEquals(404, e.getStatus());
    }
  }
}
