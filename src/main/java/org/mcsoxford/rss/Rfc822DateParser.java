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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Canonical DateParser implementation for RSS spec. Checks against RFC-822.
 * 
 * @author Mr Horn
 */
final class Rfc822DateParser implements DateParser {

  /**
   * @see <a href="http://www.ietf.org/rfc/rfc0822.txt">RFC 822</a>
   */

  private static final String RFC822Format = "EEE, dd MMM yyyy HH:mm:ss Z";

  //use ThreadLocal for thread safety: https://stackoverflow.com/questions/6840803/why-is-javas-simpledateformat-not-thread-safe
  private static final ThreadLocal<SimpleDateFormat> RFC822 = new ThreadLocal<SimpleDateFormat>(){
    @Override
    protected SimpleDateFormat initialValue()
    {
      return new SimpleDateFormat(RFC822Format, java.util.Locale.ENGLISH);
    }
  };

  public Rfc822DateParser() {}
  
  /**
   * Parses string as an RFC 822 date/time.
   */
  static java.util.Date parseRfc822(String date) {
    try {
      return RFC822.get().parse(date);
    } catch (ParseException e) {
      return null;
    }
  }

  @Override
  public Date parse(String date) {
    return parseRfc822(date);
  }
}

