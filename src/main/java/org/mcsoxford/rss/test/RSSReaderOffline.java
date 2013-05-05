package org.mcsoxford.rss.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.mcsoxford.rss.RSSReader;

public class RSSReaderOffline extends RSSReader
{
  InputStream feedStream;

  /**
   * Creates a new RSSReaderOffline.
   * @param xmlfeed string containing the xml code for the feed
   */
  public RSSReaderOffline(String xmlfeed) {
    this( new ByteArrayInputStream( xmlfeed.getBytes( ) ) );
  }

  /**
   * Creates a new RSSReaderOffline.
   * @param xmlfeed StringBuffer containing the xml code for the feed
   */
  public RSSReaderOffline(StringBuffer xmlfeed) {
    this( xmlfeed.toString() );
  }

  /**
   * Creates a new RSSReaderOffline.
   * @param is input stream for reading the feed
   */
  public RSSReaderOffline(InputStream is) {
    this.feedStream = is;
  }
	
  
  @Override
  protected InputStream connectAndGetFeed(String uri) {
	return this.feedStream;
  }

}
