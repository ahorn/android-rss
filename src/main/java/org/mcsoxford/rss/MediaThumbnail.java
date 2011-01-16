/*
 * Copyright (C) 2010 A. Horn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mcsoxford.rss;

/**
 * Immutable class for media thumbnail RSS 2.0 data.
 * 
 * @author Mr Horn
 * @see http://search.yahoo.com/mrss/
 */
public final class MediaThumbnail {

  private final android.net.Uri url;
  private final int height;
  private final int width;

  /**
   * Returns the URL of the thumbnail.
   * The return value is never {@code null}.
   */
  public android.net.Uri getUrl() {
    return url;
  }

  /**
   * Returns the thumbnail's height or {@code -1} if unspecified.
   */
  public int getHeight() {
    return height;
  }

  /**
   * Returns the thumbnail's width or {@code -1} if unspecified.
   */
  public int getWidth() {
    return width;
  }

  /* Internal constructor for RSSHandler */
  MediaThumbnail(android.net.Uri url, int height, int width) {
    this.url = url;
    this.height = height;
    this.width = width;
  }

  /**
   * Returns the thumbnail's URL as a string.
   */
  public String toString() {
    return url.toString();
  }

  /**
   * Returns the hash code of the thumbnail's URL.
   */
  @Override
  public int hashCode() {
    return url.hashCode();
  }

  /**
   * Compares the URLs of two thumbnails for equality.
   */
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    } else if (object instanceof MediaThumbnail) {
      final MediaThumbnail other = (MediaThumbnail) (object);

      /* other is not null */
      return url.equals(other.url);
    } else {
      return false;
    }
  }

}

