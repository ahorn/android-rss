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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HTTP client to retrieve and parse RSS 2.0 feeds. Callers must call
 * {@link RSSReader#close()} to release all resources.
 *
 * @author Mr Horn
 * @author Nick Felker (v1.1)
 *
 * @version 1.1 - Switch from Apache Http Client to Http Url Connection
 */
public class RSSReader implements java.io.Closeable {

    /**
     * Thread-safe RSS parser SPI.
     */
    private final RSSParserSPI parser;

    /**
     * Instantiate a thread-safe HTTP client to retrieve RSS feeds.
     *
     * @param parser thread-safe RSS parser SPI implementation
     */
    public RSSReader(RSSParserSPI parser) {
        this.parser = parser;
    }

    /**
     * Instantiate a thread-safe HTTP client to retrieve RSS feeds. Internal memory
     * consumption and load performance can be tweaked with {@link RSSConfig}.
     *
     * @param config RSS configuration
     */
    public RSSReader(RSSConfig config) {
        this(new RSSParser(config));
    }

    /**
     * Instantiate a thread-safe HTTP client to retrieve and parse RSS feeds.
     * Default RSS configuration capacity values are used.
     */
    public RSSReader() {
        this(new RSSParser(new RSSConfig()));
    }

    /**
     * Send HTTP GET request and parse the XML response to construct an in-memory
     * representation of an RSS 2.0 feed.
     *
     * @param uri RSS 2.0 feed URI
     * @return in-memory representation of downloaded RSS feed
     * @throws RSSReaderException if RSS feed could not be retrieved because of
     *           HTTP error
     * @throws RSSFault if an unrecoverable IO error has occurred
     */
    public RSSFeed load(String uri) throws RSSReaderException {
        InputStream feedStream = null;
        try {
            URL url = new URL(uri);
            // Send GET request to URI
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Check if server response is valid
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RSSReaderException(conn.getResponseCode(),
                        conn.getResponseMessage());
            }

            // Extract content stream from HTTP response
            feedStream = conn.getInputStream();
            RSSFeed feed = parser.parse(feedStream);

            if (feed.getLink() == null) {
                feed.setLink(android.net.Uri.parse(uri));
            }

            return feed;
        } catch (IOException e) {
            throw new RSSFault(e);
        } finally {
            Resources.closeQuietly(feedStream);
        }
    }

    /**
     * Release all HTTP client resources.
     */
    @Deprecated
    public void close() {
        // Don't need to close any HTTP clients.
    }

}
