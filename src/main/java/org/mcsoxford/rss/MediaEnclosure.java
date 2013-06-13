/*
 * Copyright (C) 2013 Redsolution LTD
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
 * Class for enclosure RSS 2.0 data.
 *
 * @author Alexander Ivanov
 */
public final class MediaEnclosure {

    private final android.net.Uri url;
    private final int length;
    private final String mimeType;

    /**
     * Returns the URL of the enclosure. The return value is never {@code null}.
     */
    public android.net.Uri getUrl() {
        return url;
    }

    /**
     * Returns the length of the enclosure.
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the mime type of the enclosure. The return value is never
     * {@code null}.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Internal constructor for RSSHandler
     */
    MediaEnclosure(android.net.Uri url, int length, String mimeType) {
        this.url = url;
        this.length = length;
        this.mimeType = mimeType;
    }

}
