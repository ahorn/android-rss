package org.mcsoxford.rss;

/**
 * Internal SAX handler to efficiently parse RSS feeds. Only a single thread
 * must use this SAX handler.
 * 
 * @author Mr Horn
 */
class RSSHandler extends org.xml.sax.helpers.DefaultHandler {

  /**
   * Constant for XML element name which identifies RSS items.
   */
  private static final String RSS_ITEM = "item";

  /**
   * Constant symbol table to ensure efficient treatment of handler states.
   */
  private final java.util.Map<String, Setter> setters;

  /**
   * Reference is never {@code null}. Visibility must be package-private to
   * ensure efficiency of inner classes.
   */
  final RSSFeed feed = new RSSFeed();

  /**
   * Reference is {@code null} unless started to parse &lt;item&gt; element.
   * Visibility must be package-private to ensure efficiency of inner classes.
   */
  RSSItem item;

  /**
   * If not {@code null}, then buffer the characters inside an XML text element.
   */
  private StringBuilder buffer;

  /**
   * Dispatcher to set either {@link #feed} or {@link #item} fields.
   */
  private Setter setter;

  /**
   * Closure to change fields in POJOs which store RSS data.
   */
  private static interface Setter {

    /**
     * Set the field of an object which represents an RSS element.
     */
    void set(String value);

  }

  /**
   * Setter for RSS &lt;title&gt; elements inside a &lt;channel&gt; or an
   * &lt;item&gt; element. The title of the RSS feed is set only if
   * {@link #item} is {@code null}. Otherwise, the title of the RSS
   * {@link #item} is set.
   */
  private final Setter SET_TITLE = new Setter() {
    @Override
    public void set(String title) {
      if (item == null) {
        feed.setTitle(title);
      } else {
        item.setTitle(title);
      }
    }
  };

  /**
   * Setter for RSS &lt;description&gt; elements inside a &lt;channel&gt; or an
   * &lt;item&gt; element. The title of the RSS feed is set only if
   * {@link #item} is {@code null}. Otherwise, the title of the RSS
   * {@link #item} is set.
   */
  private final Setter SET_DESCRIPTION = new Setter() {
    @Override
    public void set(String description) {
      if (item == null) {
        feed.setDescription(description);
      } else {
        item.setDescription(description);
      }
    }
  };

  /**
   * Setter for RSS &lt;link&gt; elements inside a &lt;channel&gt; or an
   * &lt;item&gt; element. The title of the RSS feed is set only if
   * {@link #item} is {@code null}. Otherwise, the title of the RSS
   * {@link #item} is set.
   */
  private final Setter SET_LINK = new Setter() {
    @Override
    public void set(String link) {
      final java.net.URI uri = URIs.parseURI(link);
      if (item == null) {
        feed.setLink(uri);
      } else {
        item.setLink(uri);
      }
    }
  };

  /**
   * Setter for RSS &lt;pubDate&gt; elements inside a &lt;channel&gt; or an
   * &lt;item&gt; element. The title of the RSS feed is set only if
   * {@link #item} is {@code null}. Otherwise, the title of the RSS
   * {@link #item} is set.
   */
  private final Setter SET_PUBDATE = new Setter() {
    @Override
    public void set(String pubDate) {
      final java.util.Date date = Dates.parseRfc822(pubDate);
      if (item == null) {
        feed.setPubDate(date);
      } else {
        item.setPubDate(date);
      }
    }
  };

  /**
   * Setter for RSS &lt;category&gt; elements inside a &lt;channel&gt; or an
   * &lt;item&gt; element. The title of the RSS feed is set only if
   * {@link #item} is {@code null}. Otherwise, the title of the RSS
   * {@link #item} is set.
   */
  private final Setter SET_CATEGORY = new Setter() {
    @Override
    public void set(String category) {
      if (item == null) {
        feed.setCategory(category);
      } else {
        item.setCategory(category);
      }
    }
  };

  /**
   * Instantiate a SAX handler which can parse a subset of RSS 2.0 feeds.
   */
  RSSHandler() {
    // initialize dispatchers to manage the state of the SAX handler
    setters = new java.util.HashMap<String, Setter>(/* prime */5);
    setters.put("title", SET_TITLE);
    setters.put("description", SET_DESCRIPTION);
    setters.put("link", SET_LINK);
    setters.put("category", SET_CATEGORY);
    setters.put("pubDate", SET_PUBDATE);
  }

  /**
   * Returns the RSS feed after this SAX handler has processed the XML document.
   */
  RSSFeed feed() {
    return feed;
  }

  /**
   * Identify the appropriate dispatcher which should be used to store XML data
   * in a POJO. Unsupported RSS 2.0 elements are currently ignored.
   */
  @Override
  public void startElement(String nsURI, String localName, String qname,
      org.xml.sax.Attributes attributes) {

    // Lookup dispatcher in hash table
    setter = setters.get(qname);
    if (setter == null) {
      if (RSS_ITEM.equals(qname)) {
        item = new RSSItem();
      }
    } else {
      // Buffer supported RSS data
      buffer = new StringBuilder();
    }
  }

  @Override
  public void endElement(String nsURI, String localName, String qname) {
    if (isBuffering()) {
      // set field of an RSS feed or RSS item
      setter.set(buffer.toString());

      // clear buffer
      buffer = null;
    } else if (qname.equals(RSS_ITEM)) {
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
    return buffer != null && setter != null;
  }

}
