package org.mcsoxford.rss;

/**
 * Contingency to which an RSS client should react and recover.
 * 
 * @author Mr Horn
 */
public class RSSException extends Exception {

  /**
   * Unsupported serialization
   */
  private static final long serialVersionUID = 1L;

  public RSSException(String message) {
    super(message);
  }

  public RSSException(Throwable cause) {
    super(cause);
  }

  public RSSException(String message, Throwable cause) {
    super(message, cause);
  }

}
