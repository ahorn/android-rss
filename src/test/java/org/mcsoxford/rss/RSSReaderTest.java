package org.mcsoxford.rss;

import java.net.URI;

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
   * BBC News - World RSS feed
   */
  private final static String BBC_NEWS = "http://feeds.bbci.co.uk/news/world/rss.xml";

  /**
   * Class under test
   */
  private RSSReader reader;

  /**
   * URI to RSS feed.
   */
  private URI uri;

  @Before
  public void setup() {
    reader = new RSSReader();
    uri = URI.create(BBC_NEWS);
  }

  @After
  public void teardown() {
    reader.close();
  }

  @Test
  public void get() throws RSSReaderException {
    final RSSFeed feed = reader.load(uri);

    assertEquals("BBC News - World", feed.getTitle());

    assertEquals(
        URI.create("http://www.bbc.co.uk/go/rss/int/news/-/news/world/"),
        feed.getLink());

    assertEquals(
        "The latest stories from the World section of the BBC News web site.",
        feed.getDescription());
  }

  @Test
  public void notFound() {
    try {
      reader.load(URI.create("http://www.example.com/notfound"));
    } catch (RSSReaderException e) {
      assertEquals(404, e.getStatus());
    }
  }
}
