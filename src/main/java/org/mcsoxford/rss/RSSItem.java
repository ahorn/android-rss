package org.mcsoxford.rss;

/**
 * Data about an RSS item.
 * 
 * @author Mr Horn
 */
public class RSSItem extends RSSBase {

  private String category;

  public String getCategory() {
    return category;
  }

  void setCategory(String category) {
    this.category = category;
  }

  /**
   * Returns the title of the RSS item.
   */
  public String toString() {
    return getTitle();
  }

}
