package org.mcsoxford.rss;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import static org.mcsoxford.rss.RSSHandlerState.*;

/**
 * Towards an RSS 2.0 parser. Only a single thread must use this SAX handler.
 * 
 * @author Mr Horn
 */
public class RSSHandler extends DefaultHandler {

  /**
   * Constant symbol table to ensure efficient lookup of handler states.
   */
  private static java.util.Map<String, RSSHandlerState> STATES;
  static {
    STATES = new java.util.HashMap<String, RSSHandlerState>(7);
    STATES.put("channel", RSS_CHANNEL);
    STATES.put("item", RSS_ITEM);
    STATES.put("title", RSS_TITLE);
    STATES.put("description", RSS_DESCRIPTION);
    STATES.put("link", RSS_LINK);
    STATES.put("category", RSS_CATEGORY);
    STATES.put("pubDate", RSS_PUBDATE);
  }

  /**
   * Buffer the characters inside an XML text element.
   */
  private StringBuilder buffer;

  /**
   * Reference is {@code null} unless started to parse <channel> element.
   */
  private RSSFeed feed;

  /**
   * Reference is {@code null} unless started to parse <item> element.
   */
  private RSSItem item;

  /**
   * Enumerable data type that is set according to the most recently encountered
   * XML element.
   */
  private RSSHandlerState state;

  /**
   * Returns the parsed RSS feed after the handler has terminated.
   */
  public RSSFeed feed() {
    return feed;
  }

  @Override
  public void startElement(String nsURI, String localName, String qname,
      Attributes attributes) {

    // TODO: account for SAX implementations 
    if (qname == null || qname.isEmpty()) {
      qname = localName;
    }

    state = STATES.get(qname);
    if (state == null) {
      // skip unsupported RSS element
      return;
    } else if (state == RSS_CHANNEL) {
      feed = new RSSFeed();
    } else if (state == RSS_ITEM) {
      item = new RSSItem();
    } else {
      // buffer other supported RSS data
      buffer = new StringBuilder();
    }
  }

  @Override
  public void endElement(String nsURI, String localName, String qname) {
    if (isBuffering()) {
      final String bufferString = buffer.toString();
      final RSSBase scope = item == null ? feed : item;
      switch (state) {
        case RSS_TITLE:
          scope.setTitle(bufferString);
          break;
        case RSS_LINK:
          scope.setLink(java.net.URI.create(bufferString));
          break;
        case RSS_DESCRIPTION:
          scope.setDescription(bufferString);
          break;
        case RSS_PUBDATE:
          scope.setPubDate(Dates.parseRfc822(bufferString));
          break;
        case RSS_CATEGORY:
          item.setCategory(bufferString);
          break;
      }

      // clear buffer
      buffer = null;
    }

    if (qname.equals("item")) {
      feed.addItem(item);

      // (re)enter <channel> scope
      item = null;
    }
  }

  @Override
  public void characters(char ch[], int start, int length) {
    if (isBuffering()) {
      buffer.append(ch, start, length);
    }
  }

  /**
   * Determines if the SAX parser is ready to receive data inside an XML element
   * such as &lt;title&gt; or &lt;description&gt;.
   * 
   * @return boolean {@code true} if the SAX handler parses data inside an XML
   *         element, {@code false} otherwise
   */
  boolean isBuffering() {
    return buffer != null && state != null;
  }

}
