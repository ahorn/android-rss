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
class RSSParser {

  /**
   * Parses input stream as RSS feed. It is the responsibility of the caller to
   * close the RSS feed input stream.
   * 
   * @param feed RSS 2.0 feed input stream
   * @return in-memory representation of RSS feed
   * @throws RSSFault if an unrecoverable parse error occurs
   */
  RSSFeed parse(InputStream feed) {
    try {
      // TODO: optimize by maintaining factory references
      final SAXParserFactory factory = SAXParserFactory.newInstance();
      final SAXParser parser = factory.newSAXParser();

      return parse(parser, feed);
    } catch (ParserConfigurationException e) {
      throw new RSSFault(e);
    } catch (SAXException e) {
      throw new RSSFault(e);
    } catch (IOException e) {
      throw new RSSFault(e);
    }
  }

  /**
   * Parses input stream as an RSS 2.0 feed.
   * 
   * @return in-memory representation of an RSS feed
   * @throws IllegalArgumentException if either argument is {@code null}
   */
  private static RSSFeed parse(final SAXParser parser, final InputStream feed)
      throws SAXException, IOException {
    if (parser == null) {
      throw new IllegalArgumentException("RSS parser must not be null.");
    } else if (feed == null) {
      throw new IllegalArgumentException("RSS feed must not be null.");
    }

    final InputSource source = new InputSource(feed);
    final XMLReader xmlreader = parser.getXMLReader();
    final RSSHandler handler = new RSSHandler();

    xmlreader.setContentHandler(handler);
    xmlreader.parse(source);

    return handler.feed();
  }

}
