package org.mcsoxford.rss;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;

/*
 * RSS feed to string parser.
 *
 * @author Joris van Winden
 */

public class RSSStringDumpParser {

  public RSSStringDumpParser() {}

  /*
   * Parses input stream as a string. It is the responsibility of the caller to
   * close the stream.
   * 
   * @param feed RSS 2.0 feed input stream
   * @return string representation of the feed
   * @throws RSSFault when io goes wrong
   */
  public String parse(InputStream feed) {

    BufferedReader reader = new BufferedReader(new InputStreamReader(feed));

    StringBuilder result = new StringBuilder();

    String line;

    try {
      while((line = reader.readLine()) != null) {
        result.append(line);
      }
    } catch (IOException e) {
      throw new RSSFault(e);
    }

    return result.toString();
  }
}
