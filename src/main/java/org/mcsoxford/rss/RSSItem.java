package org.mcsoxford.rss;

/**
 * Data about an RSS item.
 * 
 * @author Mr Horn
 */
public class RSSItem extends RSSBase {

  /**
   * Limit the number of characters returned by {@link #toString()}.
   */
  private final int limit;
  private String category;

  /**
   * An RSS item with a custom limit.
   * 
   * @param limit number of characters displayed of the title
   */
  RSSItem(int limit) {
    this.limit = limit;
  }

  /**
   * An RSS item with a default title limit of 42 characters.
   */
  RSSItem() {
    this(42);
  }

  public String getCategory() {
    return category;
  }

  void setCategory(String category) {
    this.category = category;
  }

  /**
   * Returns the title of the RSS item. A title which exceeds the limit of
   * characters is truncated with ellipses.
   */
  public String toString() {
    return Strings.truncate(getTitle(), limit);
  }

}
