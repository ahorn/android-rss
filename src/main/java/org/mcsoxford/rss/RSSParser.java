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
 * SAX parser for RSS 2.0 feeds.
 * 
 * @author Mr Horn
 */
public class RSSParser {

  private final SAXParserFactory factory;

  /**
   * Customize SAX parser.
   */
  public RSSParser(SAXParserFactory factory) {
    this.factory = factory;
  }

  /**
   * Parse RSS with default SAX parser.
   */
  public RSSParser() {
    this(SAXParserFactory.newInstance());
  }

  /**
   * Parse input stream as an RSS feed.
   * 
   * @return RSS 2.0 feed
   */
  public RSSFeed parse(InputStream rssfeed) {
    if (rssfeed == null) {
      throw new IllegalArgumentException("RSS feed must not be null.");
    }

    final InputSource source = new InputSource(rssfeed);
    try {
      final SAXParser parser = factory.newSAXParser();
      factory.setNamespaceAware(true);
      final XMLReader xmlreader = parser.getXMLReader();
      final RSSHandler handler = new RSSHandler();

      xmlreader.setContentHandler(handler);
      xmlreader.parse(source);

      return handler.feed();
    } catch (ParserConfigurationException e) {
      throw new RSSFault(e);
    } catch (SAXException e) {
      throw new RSSFault(e);
    } catch (IOException e) {
      throw new RSSFault(e);
    } finally {
      close(rssfeed);
    }

  }

  /**
   * Close input stream and suppress IO faults.
   * 
   * @return boolean {@code true} if stream has been successfully closed,
   *         {@code false} otherwise
   */
  private static boolean close(InputStream istream) {
    try {
      istream.close();
    } catch (IOException e) {
      return false;
    }

    return true;
  }

}
