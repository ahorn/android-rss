package org.mcsoxford.rss;

/**
 * Data about an RSS feed and its RSS items.
 * 
 * @author Mr Horn
 */
public class RSSFeed extends RSSBase {

  private final java.util.List<RSSItem> items;

  RSSFeed() {
    items = new java.util.LinkedList<RSSItem>();
  }

  /**
   * Returns an unmodifiable list of RSS items.
   */
  public java.util.List<RSSItem> getItems() {
    return java.util.Collections.unmodifiableList(items);
  }

  void addItem(RSSItem item) {
    items.add(item);
  }

}
