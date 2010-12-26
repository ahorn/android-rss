package org.mcsoxford.rss;

import java.util.List;

/**
 * Data about an RSS item.
 * 
 * @author Mr Horn
 */
public class RSSItem extends RSSBase {
  private final List<MediaThumbnail> thumbnails;

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
   * Returns a reference to the thumbnails.
   * The return value is never {@code null}.
   * Images are in order of importance.
   */
  public List<MediaThumbnail> getThumbnails() {
    return thumbnails;
  }
}
