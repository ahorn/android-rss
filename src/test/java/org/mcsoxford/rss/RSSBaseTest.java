package org.mcsoxford.rss;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Some unit tests for {@link RSSBase} class.
 * 
 * @author Mr Horn
 */
public class RSSBaseTest {

  // Stub implementation of abstract base class.
  static class Foo extends RSSBase {
    Foo() {
      super((byte) 0);
    }
  }

  /**
   * Class under test
   */
  private RSSBase base;

  @Before
  public void setup() {
    base = new Foo();
  }

  @Test
  public void equalsNull() {
    assertFalse(base.equals(null));
  }

  @Test
  public void equalsOtherType() {
    assertFalse(base.equals(new Object()));
  }

  @Test
  public void equalsSame() {
    assertTrue(base.equals(base));
  }

  @Test
  public void equalsOtherNullLink() {
    RSSBase other = new Foo();
    assertTrue(base.equals(other));

    other.setLink(android.net.Uri.parse("http://example.com/other"));
    assertFalse(base.equals(other));
  }

  @Test
  public void equalsOtherLink() {
    RSSBase other = new Foo();
    other.setLink(android.net.Uri.parse("http://example.com/other"));
    base.setLink(android.net.Uri.parse("http://example.com/"));
    assertFalse(base.equals(other));
  }

  @Test
  public void equalsLink() {
    RSSBase other = new Foo();
    other.setLink(android.net.Uri.parse("http://example.com/"));
    base.setLink(android.net.Uri.parse("http://example.com/"));
    assertTrue(base.equals(other));
  }

}
