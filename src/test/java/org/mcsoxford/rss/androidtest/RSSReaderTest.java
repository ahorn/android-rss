package org.mcsoxford.rss.androidtest;

import junit.framework.*;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;


/**
 * Integration test of HTTP client which retrieves RSS feeds.
 * 
 * @author Mr Horn
 */
public class RSSReaderTest extends TestCase {

  /**
   * URI of the "BBC News - World" RSS feed
   */
  private final static String BBC_NEWS = "http://feeds.bbci.co.uk/news/world/rss.xml";

  /**
   * Class under test
   */
  private RSSReader reader;


  public void setUp() throws Exception {
	super.setUp();
    reader = new RSSReader();
  }

  
  public void tearDown() throws Exception {
	super.tearDown();
    reader.close();
  }

  
  public void testGet() throws RSSReaderException {
    final RSSFeed feed = reader.load(BBC_NEWS);

    assertEquals("BBC News - World", feed.getTitle());

    assertEquals(
        android.net.Uri.parse("http://www.bbc.co.uk/news/world/#sa-ns_mchannel=rss&ns_source=PublicRSS20-sa"),
        feed.getLink());

    assertEquals(
        "The latest stories from the World section of the BBC News web site.",
        feed.getDescription());
  }

  
  public void testNotFound() {
    try {
      reader.load("http://www.google.co.uk/not/found");
    } catch (RSSReaderException e) {
      assertEquals(404, e.getStatus());
    }
  }
}
