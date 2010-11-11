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

  public Iterable<RSSItem> getItems() {
    return items;
  }

  void addItem(RSSItem item) {
    items.add(item);
  }

}
