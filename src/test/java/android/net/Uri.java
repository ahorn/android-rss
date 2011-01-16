package android.net;

/**
 * Stub implementation of android.net.Uri.parse(String) static method.
 */
public final class Uri {

  private final String uri;

  private Uri(String uri) {
    this.uri = uri;
  }

  public static Uri parse(String uri) {
    return new Uri(uri);
  }

  /**
   * Compares two URIs for string equality.
   */
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    } else if (object instanceof Uri) {
      final Uri other = (Uri) (object);

      /* other is not null */
      return uri.equals(other.uri);
    } else {
      return false;
    }
  }

  /**
   * Computes the hash code of the URI's string representation.
   */
  @Override
  public int hashCode() {
    return uri.hashCode();
  }

  /**
   * Returns the string representation of the URI.
   */
  @Override
  public String toString() {
    return uri;
  }

}

