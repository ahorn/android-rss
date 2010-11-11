package org.mcsoxford.rss;

/**
 * Common data about RSS feeds and items.
 * 
 * @author Mr Horn
 */
abstract class RSSBase {

  private String title;
  private java.net.URI link;
  private String description;
  private String category;
  private java.util.Date pubdate;

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return this.description;
  }

  public java.net.URI getLink() {
    return link;
  }

  public String getCategory() {
    return category;
  }

  public java.util.Date getPubDate() {
    return pubdate;
  }

  void setTitle(String title) {
    this.title = title;
  }

  void setLink(java.net.URI link) {
    this.link = link;
  }

  void setDescription(String description) {
    this.description = description;
  }

  void setCategory(String category) {
    this.category = category;
  }

  void setPubDate(java.util.Date pubdate) {
    this.pubdate = pubdate;
  }

}
