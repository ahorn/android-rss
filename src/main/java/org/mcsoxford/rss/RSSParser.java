package org.mcsoxford.rss;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Thread-safe RSS parser.
 * 
 * @author Mr Horn
 */
public class RSSParser {

  /**
   * Parses input stream as RSS feed. The input stream is automatically closed.
   * 
   * @param rssfeed RSS 2.0 feed
   * @return RSS feed in-memory representation
   * @throws RSSFault if an unrecoverable parse error occurs
   */
  public RSSFeed parse(InputStream rssfeed) {
    try {
      // TODO: optimize by maintaining factory references
      final SAXParserFactory factory = SAXParserFactory.newInstance();
      final SAXParser parser = factory.newSAXParser();

      return parse(parser, rssfeed);
    } catch (ParserConfigurationException e) {
      throw new RSSFault(e);
    } catch (SAXException e) {
      throw new RSSFault(e);
    } catch (IOException e) {
      throw new RSSFault(e);
    } finally {
      closeQuietly(rssfeed);
    }

  }

  /**
   * Parses input stream as an RSS 2.0 feed. It is the responsibility of the
   * caller to close the input stream.
   * 
   * @return RSS feed
   * @throws IllegalArgumentException if either argument is {@code null}
   */
  static RSSFeed parse(final SAXParser parser, final InputStream rssfeed)
      throws SAXException, IOException {
    if (parser == null) {
      throw new IllegalArgumentException("RSS feed must not be null.");
    } else if (rssfeed == null) {
      throw new IllegalArgumentException("RSS feed must not be null.");
    }

    final InputSource source = new InputSource(rssfeed);
    final XMLReader xmlreader = parser.getXMLReader();
    final RSSHandler handler = new RSSHandler();

    xmlreader.setContentHandler(handler);
    xmlreader.parse(source);

    return handler.feed();
  }

  /**
   * Closes stream and suppresses IO faults.
   * 
   * @return {@code null} if stream has been successfully closed,
   *         {@link IOException} otherwise
   */
  private static IOException closeQuietly(java.io.Closeable stream) {
    try {
      stream.close();
    } catch (IOException e) {
      return e;
    }

    return null;
  }

}
