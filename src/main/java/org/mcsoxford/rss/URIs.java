package org.mcsoxford.rss;

import java.net.URISyntaxException;

/**
 * Internal helper class for URIs.
 * 
 * @author Mr Horn
 */
final class URIs {

  /* Hide constructor */
  private URIs() {}

  /**
   * Parses string as a URI.
   * 
   * @throws RSSFault if the string is not a valid URI
   */
  static java.net.URI parseURI(String uri) {
    try {
      return new java.net.URI(uri);
    } catch (URISyntaxException e) {
      throw new RSSFault(e);
    }
  }
}
