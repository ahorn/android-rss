package org.mcsoxford.rss;

/**
 * Immutable data structure to configure the RSS parser and loader modules. On
 * large data sets, well-chosen configuration values can reduce memory
 * consumption and increase performance.
 * 
 * @author Mr Horn
 */
public final class RSSConfig {

  /**
   * Average number of RSS item &lt;category&gt; elements which serves as the
   * initial capacity of the Set implementation.
   */
  final byte categoryAvg;

  /**
   * Average number of RSS item &lt;media:thumbnail&gt; elements which serves as
   * the initial capacity of the List implementation.
   */
  final byte thumbnailAvg;

  /**
   * Instantiate an RSS configuration with the specified parameters.
   * 
   * @param categoryAvg average number of RSS item &lt;category&gt; elements in
   *          a typical RSS feed
   * @param thumbnailAvg average number of RSS item &lt;metia:thumbnail&gt;
   *          elements in a typical RSS feed
   */
  public RSSConfig(byte categoryAvg, byte thumbnailAvg) {
    this.categoryAvg = categoryAvg;
    this.thumbnailAvg = thumbnailAvg;
  }

  /**
   * Instantiate an RSS configuration with default values.
   */
  public RSSConfig() {
    this.categoryAvg = 3;
    this.thumbnailAvg = 2;
  }

}

