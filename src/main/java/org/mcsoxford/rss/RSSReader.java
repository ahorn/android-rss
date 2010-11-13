package org.mcsoxford.rss;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * HTTP client to retrieve and parse RSS feeds. Callers must call
 * {@link RSSReader#close()} to release all resources.
 * 
 * @author Mr Horn
 */
public class RSSReader implements java.io.Closeable {

  /**
   * Thread-safe {@link HttpClient} implementation.
   */
  private final HttpClient httpclient;

  /**
   * Thread-safe RSS parser.
   */
  private final RSSParser parser;

  /**
   * Instantiate a thread-safe HTTP connector to retrieve RSS feeds. The
   * injected {@link HttpClient} implementation must be thread-safe.
   * 
   * @param httpclient thread-safe HTTP client implementation
   */
  public RSSReader(HttpClient httpclient, RSSParser parser) {
    this.httpclient = httpclient;
    this.parser = parser;
  }

  /**
   * Instantiate a thread-safe HTTP client to retrieve and parse RSS feeds.
   */
  public RSSReader() {
    this(new DefaultHttpClient(), new RSSParser());
  }

  /**
   * Retrieve RSS feed with HTTP GET and parse the XML to construct an in-memory
   * representation.
   * 
   * @param uri RSS feed URI to load
   * @throws RSSReaderException if RSS feed could not be retrieved because of
   *           HTTP protocol error
   * @throws RSSFault if an unrecoverable IO error has occurred
   */
  public RSSFeed load(java.net.URI uri) throws RSSReaderException {
    final HttpGet httpget = new HttpGet(uri);

    final InputStream content;
    try {
      // Send GET request to URI
      final HttpResponse response = httpclient.execute(httpget);

      // Check if server response is valid
      final StatusLine status = response.getStatusLine();
      if (status.getStatusCode() != HttpStatus.SC_OK) {
        throw new RSSReaderException(status.getStatusCode(),
            status.getReasonPhrase());
      }

      // Extract content stream from HTTP response
      HttpEntity entity = response.getEntity();
      content = entity.getContent();
    } catch (ClientProtocolException e) {
      throw new RSSFault(e);
    } catch (IOException e) {
      throw new RSSFault(e);
    }

    // Parser closes input stream
    return parser.parse(content);
  }

  /**
   * Release all HTTP client resources.
   */
  @Override
  public void close() {
    httpclient.getConnectionManager().shutdown();
  }

}
