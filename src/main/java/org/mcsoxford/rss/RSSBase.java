package org.mcsoxford.rss;

/**
 * Common data about RSS feeds and items.
 * 
 * @author Mr Horn
 */
abstract class RSSBase {

  private String title;
  private android.net.Uri link;
  private String description;
  private java.util.Set<String> categories;
  private java.util.Date pubdate;

  /**
   * Inject a Set ADT implementation.
   */
  RSSBase(java.util.Set<String> categories) {
    this.categories = categories;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public android.net.Uri getLink() {
    return link;
  }

  public java.util.Set<String> getCategories() {
    return java.util.Collections.unmodifiableSet(categories);
  }

  public java.util.Date getPubDate() {
    return pubdate;
  }

  void setTitle(String title) {
    this.title = title;
  }

  void setLink(android.net.Uri link) {
    this.link = link;
  }

  void setDescription(String description) {
    this.description = description;
  }

  void addCategory(String category) {
    this.categories.add(category);
  }

  void setPubDate(java.util.Date pubdate) {
    this.pubdate = pubdate;
  }

  /**
   * Returns the title.
   */
  public String toString() {
    return title;
  }

  @Override
  public int hashCode() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object object) {
    throw new UnsupportedOperationException();
  }

}

