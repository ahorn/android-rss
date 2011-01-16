package org.mcsoxford.rss;

/**
 * Contingency to which an RSS client should react. Unlike {@link RSSFault}
 * runtime exceptions, the occurrence of an {@link RSSException} denotes an
 * alternative execution flow for which RSS clients should account.
 * 
 * @author Mr Horn
 * @see RSSFault
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

