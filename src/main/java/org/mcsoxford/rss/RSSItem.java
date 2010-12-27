package org.mcsoxford.rss;

/**
 * Data about an RSS item.
 * 
 * @author Mr Horn
 */
public class RSSItem extends RSSBase {
  private final java.util.List<MediaThumbnail> thumbnails;

  /* Internal constructor for RSSHandler */
  RSSItem() {
    super();
    thumbnails = new java.util.ArrayList<MediaThumbnail>(2);
  }

  /* Internal method for RSSHandler */
  void addThumbnail(MediaThumbnail thumbnail) {
    thumbnails.add(thumbnail);
  }

  /**
   * Returns an unmodifiable list of thumbnails.
   * The return value is never {@code null}.
   * Images are in order of importance.
   */
  public java.util.List<MediaThumbnail> getThumbnails() {
    return java.util.Collections.unmodifiableList(thumbnails);
  }
}
