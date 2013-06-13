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
 * Data about an RSS item.
 * 
 * @author Mr Horn
 */
public class RSSItem extends RSSBase {
  private final java.util.List<MediaThumbnail> thumbnails;
  private String content;
	private MediaEnclosure enclosure;

  /* Internal constructor for RSSHandler */
  RSSItem(byte categoryCapacity, byte thumbnailCapacity) {
    super(categoryCapacity);
    thumbnails = new java.util.ArrayList<MediaThumbnail>(thumbnailCapacity);
  }

  /* Internal method for RSSHandler */
  void addThumbnail(MediaThumbnail thumbnail) {
    thumbnails.add(thumbnail);
  }

  /**
   * Returns an unmodifiable list of thumbnails. The return value is never
   * {@code null}. Images are in order of importance.
   */
  public java.util.List<MediaThumbnail> getThumbnails() {
    return java.util.Collections.unmodifiableList(thumbnails);
  }
  
  /**
   * Returns the value of the optional &lt;content:encoded&gt; tag
   * @return string value of the element data
   */
  public String getContent() {
    return content;
  }

  /* Internal method for RSSHandler */
  void setContent(String content) {
    this.content = content;
  }

	public MediaEnclosure getEnclosure() {
		return enclosure;
	}

	void setEnclosure(MediaEnclosure enclosure) {
		this.enclosure = enclosure;
	}

}
