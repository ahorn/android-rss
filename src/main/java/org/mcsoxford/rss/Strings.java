package org.mcsoxford.rss;

/**
 * Internal helper class for strings.
 * 
 * @author Mr Horn
 */
class Strings {

  private static final String ELLIPSES = "...";

  /**
   * Hide constructor.
   */
  private Strings() {}

  /**
   * Truncates the string with ellipses if it exceeds the limit.
   */
  static String truncate(String s, int limit) {
    if (s.length() > limit) {
      return s.substring(0, limit) + ELLIPSES;
    } else {
      return s;
    }
  }
}
